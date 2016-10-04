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
public class AbilityLuckConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityLuckConfig() {
        super(
                true,
                "luck",
                "Have a higher chance on better loot"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypePotionEffectSelf(getNamedId(), EnumRarity.RARE, 3, 40, MobEffects.LUCK);
    }
}
