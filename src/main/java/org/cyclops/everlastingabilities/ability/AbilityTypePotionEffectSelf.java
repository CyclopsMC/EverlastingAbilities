package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;

/**
 * Ability type for potion effects.
 * @author rubensworks
 */
public class AbilityTypePotionEffectSelf extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final Potion potion;

    public AbilityTypePotionEffectSelf(String id, EnumRarity rarity, int maxLevel, int baseXpPerLevel, Potion potion) {
        super(id, rarity, maxLevel, baseXpPerLevel);
        this.potion = potion;
    }

    protected int getDuration(int tickModulus) {
        return tickModulus * 3;
    }

    protected int getTickModulus() {
        return TICK_MODULUS;
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        if (player.worldObj.getWorldTime() % getTickModulus() == 0) {
            player.addPotionEffect(
                    new PotionEffect(potion, getDuration(getTickModulus()), level - 1, true, false));
        }
    }
}
