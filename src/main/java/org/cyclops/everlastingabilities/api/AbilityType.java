package org.cyclops.everlastingabilities.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;

/**
 * Default implementation of an ability type.
 * @author rubensworks
 */
public class AbilityType implements IAbilityType {

    private final String translationKey;
    private final String unlocalizedDescription;
    private final EnumRarity rarity;
    private final int maxLevel;
    private final int baseXpPerLevel;

    public AbilityType(String translationKey, String unlocalizedDescription, EnumRarity rarity,
                       int maxLevel, int baseXpPerLevel) {
        this.translationKey = translationKey;
        this.unlocalizedDescription = unlocalizedDescription;
        this.rarity = rarity;
        this.maxLevel = maxLevel;
        this.baseXpPerLevel = baseXpPerLevel;
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
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

    @Override
    public void onTick(EntityPlayer player, int level) {

    }

    @Override
    public void onChangedLevel(EntityPlayer player, int oldLevel, int newLevel) {

    }
}
