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
public class AbilityJumpBoostConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityJumpBoostConfig() {
        super(
                true,
                "jump_boost",
                "Jump Higher",
                new AbilityTypePotionEffectSelf("jump_boost", EnumRarity.COMMON, 5, 10, MobEffects.JUMP_BOOST)
        );
    }
}
