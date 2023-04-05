package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.ForgeRegistries;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Objects;

/**
 * Ability type for potion effects.
 * @author rubensworks
 */
public class AbilityTypePotionEffectSelf extends AbilityTypeAdapter {

    private final String potionEffectId;
    private final MobEffect potion;
    private final int tickModulus;
    private final double amplifierFactor;
    private final boolean levelBasedDuration;
    private final double durationFactor;

    public AbilityTypePotionEffectSelf(String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                       boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                       String potionEffectId, int tickModulus, double amplifierFactor, boolean levelBasedDuration, double durationFactor) {
        super(name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.potionEffectId = potionEffectId;
        this.potion = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(potionEffectId));
        if (this.potion == null) {
            throw new IllegalArgumentException("No potion effect was found with id: " + potionEffectId);
        }
        this.tickModulus = tickModulus;
        this.amplifierFactor = amplifierFactor;
        this.levelBasedDuration = levelBasedDuration;
        this.durationFactor = durationFactor;
    }

    public String getPotionEffectId() {
        return potionEffectId;
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
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_POTION_EFFECT_SELF);
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
        if (potion != null && player.level.getGameTime() % getTickModulus(level) == 0) {
            player.addEffect(
                    new MobEffectInstance(potion, getDuration(getTickModulus(level), level), getAmplifier(level), true, GeneralConfig.showPotionEffectParticles));
        }
    }
}
