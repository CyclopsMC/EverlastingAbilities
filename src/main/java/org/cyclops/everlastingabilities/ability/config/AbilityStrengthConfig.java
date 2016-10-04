package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityStrengthConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityStrengthConfig() {
        super(
                true,
                "strength",
                "Inflict more damage when attacking"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypePotionEffectSelf(getNamedId(), EnumRarity.UNCOMMON, 5, 20, MobEffects.STRENGTH);
    }
}
