package org.cyclops.everlastingabilities.ability;

import net.minecraft.world.entity.player.Player;
import org.cyclops.everlastingabilities.Reference;

import java.util.function.Supplier;

/**
 * Ability type for flight.
 * @author rubensworks
 */
public class AbilityTypeFlight extends AbilityTypeDefault {

    private static final String PLAYER_NBT_KEY = Reference.MOD_ID + ":" + "lastFlight";

    public AbilityTypeFlight(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                             Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                             Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot) {
        super(id, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public void onTick(Player player, int level) {
        player.getAbilities().mayfly = true;
    }

    @Override
    public void onChangedLevel(Player player, int oldLevel, int newLevel) {
        if (oldLevel > 0 && newLevel == 0) {
            boolean allowFlying = false;
            if(player.getPersistentData().contains(PLAYER_NBT_KEY)) {
                allowFlying = player.getPersistentData().getBoolean(PLAYER_NBT_KEY);
                player.getPersistentData().remove(PLAYER_NBT_KEY);
            }
            player.getAbilities().mayfly = allowFlying;
            if (!allowFlying) {
                player.getAbilities().flying = false;
            }
        } else if (oldLevel == 0 && newLevel > 0) {
            player.getPersistentData().putBoolean(PLAYER_NBT_KEY, player.getAbilities().mayfly);
        }
    }
}
