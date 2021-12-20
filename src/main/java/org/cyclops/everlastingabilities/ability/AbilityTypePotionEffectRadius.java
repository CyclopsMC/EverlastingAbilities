package org.cyclops.everlastingabilities.ability;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.GeneralConfig;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Ability type for potion effects in an area.
 * @author rubensworks
 */
public class AbilityTypePotionEffectRadius extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final MobEffect potion;
    private boolean hostile;

    public AbilityTypePotionEffectRadius(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                                         Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                                         Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot, MobEffect potion, boolean isHostile) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.potion = potion;
        this.hostile = isHostile;
        if (this.potion == null) {
            EverlastingAbilities.clog(org.apache.logging.log4j.Level.WARN, "Tried to register a null potion for ability " + id + ". This is possibly caused by a mod forcefully removing the potion effect for this ability.");
        }
    }
    public AbilityTypePotionEffectRadius(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                                         Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                                         Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot, MobEffect potion) {
        this(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot, potion, true);
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
                ? new ResourceLocation("player") : ForgeRegistries.ENTITIES.getKey(mob.getType());
        String mobName = resourceLocation == null ? "null" : resourceLocation.toString();
        return (mob == player ||
                player.isAlliedTo(mob) ||
                // TODO TameableEntity was IEntityOwnable
                (mob instanceof TamableAnimal && ((TamableAnimal) mob).getOwner() == player) ||
                GeneralConfig.friendlyMobs.stream().anyMatch(mobName::matches));
    }

}
