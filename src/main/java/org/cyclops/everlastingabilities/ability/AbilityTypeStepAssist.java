package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.PlayerEntity;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.config.AbilityStepAssistConfig;

/**
 * Ability type for flight.
 * @author rubensworks
 */
public class AbilityTypeStepAssist extends AbilityTypeDefault {

    private static final String PLAYER_NBT_KEY = Reference.MOD_ID + ":" + "stepAssist";

    public AbilityTypeStepAssist(String id, int rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    @Override
    public void onTick(PlayerEntity player, int level) {
        player.stepHeight = player.isSneaking() ? 0.5F : level;
    }

    @Override
    public void onChangedLevel(PlayerEntity player, int oldLevel, int newLevel) {
        if (oldLevel > 0 && newLevel == 0) {
            float stepHeight = 0.6F;
            if(player.getPersistantData().contains(PLAYER_NBT_KEY)) {
                if (!AbilityStepAssistConfig.forceDefaultStepHeight) {
                    stepHeight = player.getPersistantData().getFloat(PLAYER_NBT_KEY);
                }
                player.getPersistantData().remove(PLAYER_NBT_KEY);
            }
            player.stepHeight = stepHeight;
        } else if (oldLevel == 0 && newLevel > 0) {
            player.getPersistantData().putFloat(PLAYER_NBT_KEY, player.stepHeight);
        }
    }
}
