package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.GeneralConfig;

import java.util.function.Supplier;

/**
 * Ability type for potion effects.
 * @author rubensworks
 */
public class AbilityTypePotionEffectSelf extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final Effect potion;

    public AbilityTypePotionEffectSelf(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                                       Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                                       Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot, Effect potion) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.potion = potion;
        if (this.potion == null) {
            EverlastingAbilities.clog(Level.WARN, "Tried to register a null potion for ability " + id + ". This is possibly caused by a mod forcefully removing the potion effect for this ability.");
        }
    }

    protected int getDuration(int tickModulus, int level) {
        return tickModulus * 5;
    }

    protected int getTickModulus(int level) {
        return TICK_MODULUS;
    }

    protected int getAmplifier(int level) {
        return level - 1;
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        if (potion != null && player.world.getGameTime() % getTickModulus(level) == 0) {
            player.addPotionEffect(
                    new EffectInstance(potion, getDuration(getTickModulus(level), level), getAmplifier(level), true, GeneralConfig.showPotionEffectParticles));
        }
    }
}
