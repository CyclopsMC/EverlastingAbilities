package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.Rarity;
import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.everlastingabilities.ability.AbilityTypeStepAssist;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityStepAssistConfig extends AbilityConfig<AbilityTypeStepAssist> {

    @ConfigurableProperty(category = "ability", comment = "Forces the default step height value to 0.6 when this ability is deactivated.", isCommandable = true, configLocation = ModConfig.Type.SERVER)
    public static boolean forceDefaultStepHeight = true;

    @ConfigurableProperty(category = "ability", comment = "Rarity of this ability.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int rarity = Rarity.COMMON.ordinal();

    @ConfigurableProperty(category = "ability", comment = "The maximum ability level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int maxLevel = 3;

    @ConfigurableProperty(category = "ability", comment = "The xp required per level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int xpPerLevel = 25;

    public AbilityStepAssistConfig() {
        super("step_assist",
                eConfig -> new AbilityTypeStepAssist(eConfig.getNamedId(), rarity, maxLevel, xpPerLevel));
    }

}
