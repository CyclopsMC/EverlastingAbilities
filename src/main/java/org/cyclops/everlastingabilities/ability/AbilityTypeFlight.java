package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.Reference;

/**
 * Ability type for flight.
 * @author rubensworks
 */
public class AbilityTypeFlight extends AbilityTypeDefault {

    private static final String PLAYER_NBT_KEY = Reference.MOD_ID + ":" + "lastFlight";

    public AbilityTypeFlight(String id, EnumRarity rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        player.capabilities.allowFlying = true;
    }

    @Override
    public void onChangedLevel(EntityPlayer player, int oldLevel, int newLevel) {
        if (oldLevel > 0 && newLevel == 0) {
            boolean allowFlying = false;
            if(player.getEntityData().hasKey(PLAYER_NBT_KEY)) {
                allowFlying = player.getEntityData().getBoolean(PLAYER_NBT_KEY);
                player.getEntityData().removeTag(PLAYER_NBT_KEY);
            }
            player.capabilities.allowFlying = allowFlying;
            if (!allowFlying) {
                player.capabilities.isFlying = false;
            }
        } else if (oldLevel == 0 && newLevel > 0) {
            player.getEntityData().setBoolean(PLAYER_NBT_KEY, player.capabilities.allowFlying);
        }
    }
}
