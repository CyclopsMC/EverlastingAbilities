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
                "Adds absorbtion hearts"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypePotionEffectSelf(getNamedId(), EnumRarity.RARE, 5, 75, MobEffects.ABSORPTION) {
            @Override
            protected int getDuration(int tickModulus, int level) {
                return (int) (MinecraftHelpers.SECOND_IN_TICKS * ((float) level / getMaxLevel() * 2F));
            }

            @Override
            protected int getTickModulus(int level) {
                return MinecraftHelpers.SECOND_IN_TICKS * 2;
            }

            @Override
            protected int getAmplifier(int level) {
                return super.getAmplifier(level) / 2;
            }
        };
    }
}
