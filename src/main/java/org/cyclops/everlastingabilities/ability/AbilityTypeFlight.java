package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.PlayerEntity;
import org.cyclops.everlastingabilities.Reference;

/**
 * Ability type for flight.
 * @author rubensworks
 */
public class AbilityTypeFlight extends AbilityTypeDefault {

    private static final String PLAYER_NBT_KEY = Reference.MOD_ID + ":" + "lastFlight";

    public AbilityTypeFlight(String id, int rarity, int maxLevel, int baseXpPerLevel,
                             boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn,
                             boolean obtainableOnCraft, boolean obtainableOnLoot) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        player.abilities.allowFlying = true;
    }

    @Override
    public void onChangedLevel(PlayerEntity player, int oldLevel, int newLevel) {
        if (oldLevel > 0 && newLevel == 0) {
            boolean allowFlying = false;
            if(player.getPersistantData().contains(PLAYER_NBT_KEY)) {
                allowFlying = player.getPersistantData().getBoolean(PLAYER_NBT_KEY);
                player.getPersistantData().remove(PLAYER_NBT_KEY);
            }
            player.abilities.allowFlying = allowFlying;
            if (!allowFlying) {
                player.abilities.isFlying = false;
            }
        } else if (oldLevel == 0 && newLevel > 0) {
            player.getPersistantData().putBoolean(PLAYER_NBT_KEY, player.abilities.allowFlying);
        }
    }
}
