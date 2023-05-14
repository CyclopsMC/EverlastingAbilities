package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.crafting.conditions.ICondition;


/**
 * An ability type.
 * @author rubensworks
 */
public interface IAbilityType {

    public Codec<? extends IAbilityType> codec();

    public ICondition getCondition();
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
