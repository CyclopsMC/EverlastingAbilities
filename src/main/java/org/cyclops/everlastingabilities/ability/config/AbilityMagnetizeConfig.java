package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.ability.AbilityTypeMagnetize;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityMagnetizeConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityMagnetizeConfig() {
        super(
                true,
                "magnetize",
                "Attract nearby items",
                new AbilityTypeMagnetize("magnetize", EnumRarity.UNCOMMON, 5, 20)
        );
    }
}
