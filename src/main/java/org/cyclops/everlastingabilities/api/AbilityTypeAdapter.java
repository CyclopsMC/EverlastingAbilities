package org.cyclops.everlastingabilities.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.cyclops.everlastingabilities.GeneralConfig;

/**
 * Default implementation of an ability type.
 * @author rubensworks
 */
public abstract class AbilityTypeAdapter implements IAbilityType {

    private ICondition condition;
    private final String name;
    private final Rarity rarity;
    private final int maxLevel;
    private final int xpPerLevel;
    private final boolean obtainableOnPlayerSpawn;
    private final boolean obtainableOnMobSpawn;
    private final boolean obtainableOnCraft;
    private final boolean obtainableOnLoot;

    public AbilityTypeAdapter(ICondition condition, String name, Rarity rarity, int maxLevel,
                              int xpPerLevel, boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn,
                              boolean obtainableOnCraft, boolean obtainableOnLoot) {
        this.condition = condition;
        this.name = name;
        this.rarity = rarity;
        this.maxLevel = maxLevel;
        this.xpPerLevel = xpPerLevel;
        this.obtainableOnPlayerSpawn = obtainableOnPlayerSpawn;
        this.obtainableOnMobSpawn = obtainableOnMobSpawn;
        this.obtainableOnCraft = obtainableOnCraft;
        this.obtainableOnLoot = obtainableOnLoot;
    }

    @Override
    public ICondition getCondition() {
        return condition;
    }

    protected void setCondition(ICondition condition) {
        this.condition = condition;
    }

    @Override
    public String getTranslationKey() {
        return this.name;
    }

    @Override
    public String getUnlocalizedDescription() {
        return this.name + ".info";
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
    public int getXpPerLevelRaw() {
        return xpPerLevel;
    }

    @Override
    public int getXpPerLevelScaled() {
        return getXpPerLevelRaw() * GeneralConfig.abilityXpMultiplier;
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
    public void onTick(Player player, int level) {

    }

    @Override
    public void onChangedLevel(Player player, int oldLevel, int newLevel) {

    }
}
