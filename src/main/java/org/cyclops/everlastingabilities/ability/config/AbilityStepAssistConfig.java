package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.ability.AbilityTypeStepAssist;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityStepAssistConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityStepAssistConfig() {
        super(
                true,
                "step_assist",
                "Automatically step up a certain number of blocks depending on the level",
                new AbilityTypeStepAssist("step_assist", EnumRarity.COMMON, 3, 25)
        );
    }
}
