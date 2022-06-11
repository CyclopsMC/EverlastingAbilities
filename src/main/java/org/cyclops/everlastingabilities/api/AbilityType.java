package org.cyclops.everlastingabilities.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

/**
 * Default implementation of an ability type.
 * @author rubensworks
 */
public class AbilityType implements IAbilityType {

    private final String translationKey;
    private final String unlocalizedDescription;
    private final Supplier<Rarity> rarity;
    private final Supplier<Integer> maxLevel;
    private final Supplier<Integer> baseXpPerLevel;
    private final Supplier<Boolean> obtainableOnPlayerSpawn;
    private final Supplier<Boolean> obtainableOnMobSpawn;
    private final Supplier<Boolean> obtainableOnCraft;
    private final Supplier<Boolean> obtainableOnLoot;

    public AbilityType(String translationKey, String unlocalizedDescription, Supplier<Rarity> rarity, Supplier<Integer> maxLevel,
                       Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                       Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot) {
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
        return rarity.get();
    }

    @Override
    public int getMaxLevel() {
        return maxLevel.get();
    }

    @Override
    public int getBaseXpPerLevel() {
        return baseXpPerLevel.get();
    }

    @Override
    public boolean isObtainableOnPlayerSpawn() {
        return obtainableOnPlayerSpawn.get();
    }

    @Override
    public boolean isObtainableOnMobSpawn() {
        return obtainableOnMobSpawn.get();
    }

    @Override
    public boolean isObtainableOnCraft() {
        return obtainableOnCraft.get();
    }

    @Override
    public boolean isObtainableOnLoot() {
        return obtainableOnLoot.get();
    }

    @Override
    public void onTick(Player player, int level) {

    }

    @Override
    public void onChangedLevel(Player player, int oldLevel, int newLevel) {

    }
}
