package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;
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
                new AbilityTypePotionEffectSelf("saturation", EnumRarity.EPIC, 3, 30, MobEffects.SATURATION) {
                    @Override
                    protected int getDuration(int tickModulus) {
                        return 1;
                    }

                    @Override
                    protected int getTickModulus() {
                        return MinecraftHelpers.SECOND_IN_TICKS * 10;
                    }
                }
        );
    }
}
