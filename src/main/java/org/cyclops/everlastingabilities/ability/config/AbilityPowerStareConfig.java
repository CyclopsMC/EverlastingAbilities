package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
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
                "Push away entities you're looking at",
                new AbilityTypePowerStare("power_stare", EnumRarity.UNCOMMON, 5, 50)
        );
    }
}
