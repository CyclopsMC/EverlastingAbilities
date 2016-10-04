package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
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
                "Reduce hunger"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypePotionEffectSelf(getNamedId(), EnumRarity.EPIC, 3, 30, MobEffects.SATURATION) {
            @Override
            protected int getDuration(int tickModulus, int level) {
                return 1;
            }

            @Override
            protected int getTickModulus(int level) {
                return MinecraftHelpers.SECOND_IN_TICKS * 10;
            }
        };
    }
}
