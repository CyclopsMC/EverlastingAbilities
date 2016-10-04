package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.everlastingabilities.ability.AbilityTypeFlight;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityFlightConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityFlightConfig() {
        super(
                true,
                "flight",
                "Allows you to fly"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypeFlight(getNamedId(), EnumRarity.EPIC, 1, 150);
    }
}
