package org.cyclops.everlastingabilities.item;

import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemAbilityTotemConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemAbilityTotemConfig() {
        super(
                EverlastingAbilities._instance,
                true,
                "abilityTotem",
                null,
                ItemAbilityTotem.class
        );
    }

}
