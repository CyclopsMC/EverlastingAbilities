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
public class AbilityAbsorbtionConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityAbsorbtionConfig() {
        super(
                true,
                "absorbtion",
                "Adds absorbtion hearts",
                new AbilityTypePotionEffectSelf("absorbtion", EnumRarity.RARE, 5, 75, MobEffects.ABSORPTION)
        );
    }
}
