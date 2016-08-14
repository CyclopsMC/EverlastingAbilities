package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.config.AbilityStepAssistConfig;

/**
 * Ability type for flight.
 * @author rubensworks
 */
public class AbilityTypeStepAssist extends AbilityTypeDefault {

    private static final String PLAYER_NBT_KEY = Reference.MOD_ID + ":" + "stepAssist";

    public AbilityTypeStepAssist(String id, EnumRarity rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        player.stepHeight = player.isSneaking() ? 0.5F : level;
    }

    @Override
    public void onChangedLevel(EntityPlayer player, int oldLevel, int newLevel) {
        if (oldLevel > 0 && newLevel == 0) {
            float stepHeight = 0.5F;
            if(player.getEntityData().hasKey(PLAYER_NBT_KEY)) {
                if (!AbilityStepAssistConfig.forceDefaultStepHeight) {
                    stepHeight = player.getEntityData().getFloat(PLAYER_NBT_KEY);
                }
                player.getEntityData().removeTag(PLAYER_NBT_KEY);
            }
            player.stepHeight = stepHeight;
        } else if (oldLevel == 0 && newLevel > 0) {
            player.getEntityData().setFloat(PLAYER_NBT_KEY, player.stepHeight);
        }
    }
}
