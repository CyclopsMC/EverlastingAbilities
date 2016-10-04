package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.everlastingabilities.ability.AbilityTypeFertility;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityFertilityConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityFertilityConfig() {
        super(
                true,
                "fertility",
                "Animals in the area become fertile"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypeFertility(getNamedId(), EnumRarity.UNCOMMON, 3, 30);
    }
}
