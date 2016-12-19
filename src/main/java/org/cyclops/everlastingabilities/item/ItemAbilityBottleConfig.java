package org.cyclops.everlastingabilities.item;

import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * Config for the ability bottle.
 * @author rubensworks
 */
public class ItemAbilityBottleConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemAbilityBottleConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemAbilityBottleConfig() {
        super(
                EverlastingAbilities._instance,
                true,
                "ability_bottle",
                null,
                ItemAbilityBottle.class
        );
    }

}
