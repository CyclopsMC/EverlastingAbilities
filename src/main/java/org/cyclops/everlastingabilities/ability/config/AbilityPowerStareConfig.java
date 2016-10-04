package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.everlastingabilities.ability.AbilityTypePowerStare;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityPowerStareConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityPowerStareConfig() {
        super(
                true,
                "power_stare",
                "Push away entities you're looking at"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypePowerStare(getNamedId(), EnumRarity.UNCOMMON, 5, 50);
    }
}
