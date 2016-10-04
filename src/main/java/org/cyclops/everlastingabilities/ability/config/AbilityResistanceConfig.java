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
public class AbilityResistanceConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityResistanceConfig() {
        super(
                true,
                "resistance",
                "Take less damage from attacks"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypePotionEffectSelf(getNamedId(), EnumRarity.RARE, 3, 50, MobEffects.RESISTANCE);
    }
}
