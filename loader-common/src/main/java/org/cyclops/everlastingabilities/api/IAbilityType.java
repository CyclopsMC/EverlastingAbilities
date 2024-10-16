package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;


/**
 * An ability type.
 * @author rubensworks
 */
public interface IAbilityType {

    public MapCodec<? extends IAbilityType> codec();

    public IAbilityCondition getCondition();
    public String getTranslationKey();
    public String getUnlocalizedDescription();
    public Rarity getRarity();
    public int getMaxLevel();
    public default int getMaxLevelInfinitySafe() {
        return getMaxLevel() < 0 ? Integer.MAX_VALUE : getMaxLevel();
    }
    public int getXpPerLevelRaw();
    public int getXpPerLevelScaled();
    public boolean isObtainableOnPlayerSpawn();
    public boolean isObtainableOnMobSpawn();
    public boolean isObtainableOnCraft();
    public boolean isObtainableOnLoot();

    public void onTick(Player player, int level);
    public void onChangedLevel(Player player, int oldLevel, int newLevel);

}
