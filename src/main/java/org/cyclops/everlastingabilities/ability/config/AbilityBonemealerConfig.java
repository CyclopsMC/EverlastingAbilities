package org.cyclops.everlastingabilities.ability.config;

import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.everlastingabilities.ability.AbilityTypeBonemealer;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Config for an ability.
 * @author rubensworks
 *
 */
public class AbilityBonemealerConfig extends AbilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityConfig _instance;

    /**
     * Make a new instance.
     */
    public AbilityBonemealerConfig() {
        super(
                true,
                "bonemealer",
                "Bonemeal the area"
        );
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new AbilityTypeBonemealer("bonemealer", EnumRarity.UNCOMMON, 5, 30);
    }
}
