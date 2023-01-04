package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * Config for the ability bottle.
 * @author rubensworks
 */
public class ItemAbilityBottleConfig extends ItemConfig {

    public ItemAbilityBottleConfig() {
        super(EverlastingAbilities._instance,
                "ability_bottle",
                (eConfig) -> new ItemAbilityBottle(new Item.Properties()
                        .stacksTo(1)));
    }

}
