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
public class AbilityTypeSpecialStepAssist extends AbilityTypeAdapter {

    private static final String PLAYER_NBT_KEY = Reference.MOD_ID + ":" + "stepAssist";
    private final boolean forceDefaultStepHeight;

    public AbilityTypeSpecialStepAssist(ICondition condition, String name, Rarity rarity, int maxLevel, int baseXpPerLevel,
                                        boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn, boolean obtainableOnCraft, boolean obtainableOnLoot,
                                        boolean forceDefaultStepHeight) {
        super(condition, name, rarity, maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
        this.forceDefaultStepHeight = forceDefaultStepHeight;
    }

    @Override
    public Codec<? extends IAbilityType> codec() {
        return Objects.requireNonNull(RegistryEntries.ABILITYSERIALIZER_SPECIAL_STEP_ASSIST);
    }

    public boolean isForceDefaultStepHeight() {
        return forceDefaultStepHeight;
    }

    @Override
    public void onTick(Player player, int level) {
        player.maxUpStep = player.isCrouching() ? 0.5F : level;
    }

    @Override
    public void onChangedLevel(Player player, int oldLevel, int newLevel) {
        if (oldLevel > 0 && newLevel == 0) {
            float stepHeight = 0.6F;
            if(player.getPersistentData().contains(PLAYER_NBT_KEY)) {
                if (!this.isForceDefaultStepHeight()) {
                    stepHeight = player.getPersistentData().getFloat(PLAYER_NBT_KEY);
                }
                player.getPersistentData().remove(PLAYER_NBT_KEY);
            }
            player.maxUpStep = stepHeight;
        } else if (oldLevel == 0 && newLevel > 0) {
            player.getPersistentData().putFloat(PLAYER_NBT_KEY, player.maxUpStep);
        }
    }
}
