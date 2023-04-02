package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.List;

/**
 * Ability type for potion effects in an area.
 * @author rubensworks
 */
public class AbilityTypePotionEffectRadius extends AbilityTypeAdapter {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final String potionEffectId;
    private final MobEffect potion;
    private final boolean hostile;

    public AbilityTypePotionEffectRadius(String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                         boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                         String potionEffectId, boolean isHostile) {
        super(name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.potionEffectId = potionEffectId;
        this.potion = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(potionEffectId));
        this.hostile = isHostile;
        if (this.potion == null) {
            throw new IllegalArgumentException("No potion effect was found with id: " + potionEffectId);
        }
    }

    public String getPotionEffectId() {
        return potionEffectId;
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return RegistryEntries.ABILITYSERIALIZER_POTION_EFFECT_RADIUS;
    }

    public boolean isHostile() {
        return hostile;
    }

    protected int getDuration(int tickModulus, int level) {
        return tickModulus * 6;
    }

    protected int getTickModulus(int level) {
        return TICK_MODULUS;
    }

    protected int getAmplifier(int level) {
        return level - 1;
    }

    @Override
    public void onTick(Player player, int level) {
        Level world = player.level;
        if (potion != null && !world.isClientSide && player.level.getGameTime() % getTickModulus(level) == 0) {
            int radius = level * 2;
            List<LivingEntity> mobs = world.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(radius, radius, radius), EntitySelector.NO_SPECTATORS);
            for (LivingEntity mob : mobs) {
                if (!(this.hostile && isFriendlyMob(mob, player))) {
                    mob.addEffect(new MobEffectInstance(potion, getDuration(getTickModulus(level), level), getAmplifier(level), true, GeneralConfig.showPotionEffectParticles));
                }
            }
        }
    }

    static boolean isFriendlyMob(LivingEntity mob, Player player) {
        ResourceLocation resourceLocation = mob instanceof Player
                ? new ResourceLocation("player") : ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
        String mobName = resourceLocation == null ? "null" : resourceLocation.toString();
        return (mob == player ||
                player.isAlliedTo(mob) ||
                // TODO TameableEntity was IEntityOwnable
                (mob instanceof TamableAnimal && ((TamableAnimal) mob).getOwner() == player) ||
                GeneralConfig.friendlyMobs.stream().anyMatch(mobName::matches));
    }

}
