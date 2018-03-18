package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.cyclopscore.config.ConfigurableTypeCategory;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
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
     * Forces the default step height value to 0.6 when this ability is deactivated.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, comment = "Forces the default step height value to 0.6 when this ability is deactivated.", isCommandable = true)
    public static boolean forceDefaultStepHeight = true;
    /**
     * Rarity of this ability.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, categoryRaw = "ability", comment = "Rarity of this ability.", requiresMcRestart = true)
    public static int rarity = EnumRarity.COMMON.ordinal();
    /**
     * The maximum ability level.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, categoryRaw = "ability", comment = "The maximum ability level.", requiresMcRestart = true)
    public static int maxLevel = 3;
    /**
     * The xp required per level.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.GENERAL, categoryRaw = "ability", comment = "The xp required per level.", requiresMcRestart = true)
    public static int xpPerLevel = 25;

    /**
     * Make a new instance.
     */
    public AbilityStepAssistConfig() {
        super(
                true,
                "step_assist",
                "Automatically step up a certain number of blocks depending on the level"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypeStepAssist(getNamedId(), rarity, maxLevel, xpPerLevel);
    }
}
