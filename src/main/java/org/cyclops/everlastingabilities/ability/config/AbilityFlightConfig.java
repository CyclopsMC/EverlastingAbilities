package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.Rarity;
import net.minecraftforge.fml.config.ModConfig;
import org.cyclops.cyclopscore.config.ConfigurableProperty;
import org.cyclops.everlastingabilities.ability.AbilityTypeFlight;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityFlightConfig extends AbilityConfig<AbilityTypeFlight> {

    @ConfigurableProperty(category = "ability", comment = "Rarity of this ability.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int rarity = Rarity.EPIC.ordinal();

    @ConfigurableProperty(category = "ability", comment = "The maximum ability level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int maxLevel = 1;

    @ConfigurableProperty(category = "ability", comment = "The xp required per level.", requiresMcRestart = true, configLocation = ModConfig.Type.SERVER)
    public static int xpPerLevel = 150;

    public AbilityFlightConfig() {
        super("flight",
                eConfig -> new AbilityTypeFlight(eConfig.getNamedId(), rarity, maxLevel, xpPerLevel));
    }

}
