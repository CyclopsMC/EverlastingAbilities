package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityWaterBreathingConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityWaterBreathingConfig() {
        super(
                true,
                "water_breathing",
                "Breathe underwater",
                new AbilityTypePotionEffectSelf("water_breathing", EnumRarity.COMMON, 1, 10, MobEffects.WATER_BREATHING)
        );
    }
}
