package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectRadius;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityMiningFatigueConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityMiningFatigueConfig() {
        super(
                true,
                "mining_fatigue",
                "Entities in the area mine slower",
                new AbilityTypePotionEffectRadius("mining_fatigue", EnumRarity.UNCOMMON, 3, 40, MobEffects.MINING_FATIGUE)
        );
    }
}
