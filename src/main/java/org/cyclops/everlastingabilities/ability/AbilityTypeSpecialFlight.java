package org.cyclops.everlastingabilities.ability;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.AbilityTypeAdapter;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Objects;

/**
 * Ability type for flight.
 * @author rubensworks
 */
public class AbilityTypeSpecialFlight extends AbilityTypeAdapter {

    private static final String PLAYER_NBT_KEY = Reference.MOD_ID + ":" + "lastFlight";

    public AbilityTypeSpecialFlight(ICondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                    boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_FLIGHT);
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
