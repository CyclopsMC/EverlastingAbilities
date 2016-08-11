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
public class AbilityRegenerationConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityRegenerationConfig() {
        super(
                true,
                "regeneration",
                "Regenerate health faster",
                new AbilityTypePotionEffectSelf("regeneration", EnumRarity.RARE, 3, 50, MobEffects.REGENERATION)
        );
    }
}
