package org.cyclops.everlastingabilities.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;

/**
 * An ability type.
 * @author rubensworks
 */
public interface IAbilityType {

    public String getUnlocalizedName();
    public String getUnlocalizedDescription();
    public EnumRarity getRarity();
    public int getMaxLevel();
    public default int getMaxLevelInfinitySafe() {
        return getMaxLevel() < 0 ? Integer.MAX_VALUE : getMaxLevel();
    }
    public int getBaseXpPerLevel();

    public void onTick(EntityPlayer player, int level);
    public void onChangedLevel(EntityPlayer player, int oldLevel, int newLevel);

}
