package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Ability type for mob effects.
 * @author rubensworks
 */
public class AbilityTypeEffect extends AbilityTypeAdapter {

    private final Target target;
    private final boolean targetsFriendlyMobs;
    private final double radiusFactor;
    private final String effectId;
    private final Holder<MobEffect> potion;
    private final int tickModulus;
    private final double amplifierFactor;
    private final boolean levelBasedDuration;
    private final double durationFactor;

    public AbilityTypeEffect(IAbilityCondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                             boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                             Target target, boolean targetsFriendlyMobs, double radiusFactor,
                             String effectId, int tickModulus, double amplifierFactor, boolean levelBasedDuration, double durationFactor) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.target = target;
        this.targetsFriendlyMobs = targetsFriendlyMobs;
        this.radiusFactor = radiusFactor;
        this.effectId = effectId;
        Optional<Holder.Reference<MobEffect>> potionOptional = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId));
        if (potionOptional.isEmpty()) {
            EverlastingAbilities.clog(org.apache.logging.log4j.Level.INFO, "No potion effect was found with id: " + effectId + ". Marking as disabled.");
            this.setCondition(EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityConditionFalse());
            this.potion = null;
        } else {
            this.potion = potionOptional.get();
        }
        this.tickModulus = tickModulus;
        this.amplifierFactor = amplifierFactor;
        this.levelBasedDuration = levelBasedDuration;
        this.durationFactor = durationFactor;
    }

    public Target getTarget() {
        return target;
    }

    public boolean isTargetsFriendlyMobs() {
        return targetsFriendlyMobs;
    }

    public double getRadiusFactor() {
        return radiusFactor;
    }

    public String getEffectId() {
        return effectId;
    }

    public int getTickModulus() {
        return this.tickModulus;
    }

    public double getAmplifierFactor() {
        return amplifierFactor;
    }

    public boolean isLevelBasedDuration() {
        return levelBasedDuration;
    }

    public double getDurationFactor() {
        return durationFactor;
    }

    @Override
    public MapCodec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_EFFECT.get());
    }

    protected int getDuration(int tickModulus, int level) {
        if (isLevelBasedDuration()) {
            int maxLevel = getMaxLevel() == -1 ? 5 : getMaxLevel();
            return (int) (MinecraftHelpers.SECOND_IN_TICKS * ((float) level / maxLevel * 20F) * getDurationFactor());
        }
        return (int) (tickModulus * getDurationFactor());
    }

    protected int getTickModulus(int level) {
        return getTickModulus();
    }

    protected int getAmplifier(int level) {
        return (int) ((level - 1) * this.getAmplifierFactor());
    }

    @Override
    public void onTick(Player player, int level) {
        Level world = player.level();
        if (potion != null && !world.isClientSide && world.getGameTime() % getTickModulus(level) == 0) {
            switch (getTarget()) {
                case SELF -> {
                    player.addEffect(
                            new MobEffectInstance(this.potion, getDuration(getTickModulus(level), level), getAmplifier(level), true, GeneralConfig.showPotionEffectParticles));
                }
                case RADIUS -> {
                    double radius = level * getRadiusFactor();
                    List<LivingEntity> mobs = world.getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(radius, radius, radius), EntitySelector.NO_SPECTATORS);
                    for (LivingEntity mob : mobs) {
                        if (!(this.targetsFriendlyMobs && isFriendlyMob(mob, player))) {
                            mob.addEffect(new MobEffectInstance(this.potion, getDuration(getTickModulus(level), level), getAmplifier(level), true, GeneralConfig.showPotionEffectParticles));
                        }
                    }
                }
            }

        }
    }

    public static boolean isFriendlyMob(LivingEntity mob, Player player) {
        ResourceLocation resourceLocation = mob instanceof Player
                ? ResourceLocation.parse("player") : BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        String mobName = resourceLocation == null ? "null" : resourceLocation.toString();
        return (mob == player ||
                player.isAlliedTo(mob) ||
                (mob instanceof TamableAnimal && ((TamableAnimal) mob).getOwner() == player) ||
                GeneralConfig.friendlyMobs.stream().anyMatch(mobName::matches));
    }

    public static enum Target {
        SELF,
        RADIUS
    }
}
