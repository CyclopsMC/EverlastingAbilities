package org.cyclops.everlastingabilities.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Rarity;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * An ability type.
 * @author rubensworks
 */
public interface IAbilityType extends IForgeRegistryEntry<IAbilityType> {

    public String getTranslationKey();
    public String getUnlocalizedDescription();
    public Rarity getRarity();
    public int getMaxLevel();
    public default int getMaxLevelInfinitySafe() {
        return getMaxLevel() < 0 ? Integer.MAX_VALUE : getMaxLevel();
    }
    public int getBaseXpPerLevel();
    public boolean isObtainableOnPlayerSpawn();
    public boolean isObtainableOnMobSpawn();
    public boolean isObtainableOnCraft();
    public boolean isObtainableOnLoot();

    public void onTick(PlayerEntity player, int level);
    public void onChangedLevel(PlayerEntity player, int oldLevel, int newLevel);

}
