package org.cyclops.everlastingabilities.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Rarity;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Default implementation of an ability type.
 * @author rubensworks
 */
public class AbilityType extends ForgeRegistryEntry<IAbilityType> implements IAbilityType {

    private final String translationKey;
    private final String unlocalizedDescription;
    private final Rarity rarity;
    private final int maxLevel;
    private final int baseXpPerLevel;

    public AbilityType(String translationKey, String unlocalizedDescription, Rarity rarity,
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
    public Rarity getRarity() {
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
    public void onTick(PlayerEntity player, int level) {

    }

    @Override
    public void onChangedLevel(PlayerEntity player, int oldLevel, int newLevel) {

    }
}
