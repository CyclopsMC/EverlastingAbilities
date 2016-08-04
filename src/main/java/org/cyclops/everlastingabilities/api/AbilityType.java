package org.cyclops.everlastingabilities.api;

import net.minecraft.item.EnumRarity;

/**
 * Default implementation of an ability type.
 * @author rubensworks
 */
public class AbilityType implements IAbilityType {

    private final String unlocalizedName;
    private final String unlocalizedDescription;
    private final EnumRarity rarity;
    private final int maxLevel;
    private final int baseXpPerLevel;

    public AbilityType(String unlocalizedName, String unlocalizedDescription, EnumRarity rarity,
                       int maxLevel, int baseXpPerLevel) {
        this.unlocalizedName = unlocalizedName;
        this.unlocalizedDescription = unlocalizedDescription;
        this.rarity = rarity;
        this.maxLevel = maxLevel;
        this.baseXpPerLevel = baseXpPerLevel;
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    @Override
    public String getUnlocalizedDescription() {
        return unlocalizedDescription;
    }

    @Override
    public EnumRarity getRarity() {
        return rarity;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getBaseXpPerLevel() {
        return baseXpPerLevel;
    }
}
