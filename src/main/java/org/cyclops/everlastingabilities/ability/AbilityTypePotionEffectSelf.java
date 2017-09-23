package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * Ability type for potion effects.
 * @author rubensworks
 */
public class AbilityTypePotionEffectSelf extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final Potion potion;

    public AbilityTypePotionEffectSelf(String id, int rarity, int maxLevel, int baseXpPerLevel, Potion potion) {
        super(id, rarity, maxLevel, baseXpPerLevel);
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
    public void onTick(EntityPlayer player, int level) {
        if (potion != null && player.world.getWorldTime() % getTickModulus(level) == 0) {
            player.addPotionEffect(
                    new PotionEffect(potion, getDuration(getTickModulus(level), level), getAmplifier(level), true, false));
        }
    }
}
