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
public class AbilityTypePotionEffect extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS / 2;

    private final Potion potion;

    public AbilityTypePotionEffect(String id, EnumRarity rarity, int maxLevel, int baseXpPerLevel, Potion potion) {
        super(id, rarity, maxLevel, baseXpPerLevel);
        this.potion = potion;
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        if (player.worldObj.getWorldTime() % TICK_MODULUS == 0) {
            player.addPotionEffect(
                    new PotionEffect(potion, TICK_MODULUS * getDurationMultiplier(), level - 1, true, false));
        }
    }
}
