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
public class AbilitySaturationConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilitySaturationConfig() {
        super(
                true,
                "saturation",
                "Reduce hunger",
                new AbilityTypePotionEffectSelf("saturation", EnumRarity.RARE, 3, 30, MobEffects.SATURATION)
        );
    }
}
