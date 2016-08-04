package org.cyclops.everlastingabilities.api;

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
    public int getBaseXpPerLevel();
    //public ResourceLocation getIcon(); // TODO

}
