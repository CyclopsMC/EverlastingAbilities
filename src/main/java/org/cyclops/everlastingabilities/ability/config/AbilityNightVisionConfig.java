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
public class AbilityNightVisionConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityNightVisionConfig() {
        super(
                true,
                "night_vision",
                "See in the dark",
                new AbilityTypePotionEffectSelf("night_vision", EnumRarity.UNCOMMON, 1, 15, MobEffects.NIGHT_VISION) {
                    @Override
                    protected int getDurationMultiplier() {
                        return 27;
                    }
                }
        );
    }
}
