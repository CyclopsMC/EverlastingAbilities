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
    private final boolean obtainableOnPlayerSpawn;
    private final boolean obtainableOnMobSpawn;
    private final boolean obtainableOnCraft;
    private final boolean obtainableOnLoot;

    public AbilityType(String translationKey, String unlocalizedDescription, Rarity rarity, int maxLevel,
                       int baseXpPerLevel, boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn,
                       boolean obtainableOnCraft, boolean obtainableOnLoot) {
        this.translationKey = translationKey;
        this.unlocalizedDescription = unlocalizedDescription;
        this.rarity = rarity;
        this.maxLevel = maxLevel;
        this.baseXpPerLevel = baseXpPerLevel;
        this.obtainableOnPlayerSpawn = obtainableOnPlayerSpawn;
        this.obtainableOnMobSpawn = obtainableOnMobSpawn;
        this.obtainableOnCraft = obtainableOnCraft;
        this.obtainableOnLoot = obtainableOnLoot;
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
    public boolean isObtainableOnPlayerSpawn() {
        return obtainableOnPlayerSpawn;
    }

    @Override
    public boolean isObtainableOnMobSpawn() {
        return obtainableOnMobSpawn;
    }

    @Override
    public boolean isObtainableOnCraft() {
        return obtainableOnCraft;
    }

    @Override
    public boolean isObtainableOnLoot() {
        return obtainableOnLoot;
    }

    @Override
    public void onTick(PlayerEntity player, int level) {

    }

    @Override
    public void onChangedLevel(PlayerEntity player, int oldLevel, int newLevel) {

    }
}
