package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.capability.CompoundTagMutableAbilityStore;

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
        EverlastingAbilities._instance.getModEventBus().addListener(this::registerCapability);
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.MutableAbilityStore.ITEM, (stack, context) -> new CompoundTagMutableAbilityStore(stack::getOrCreateTag), getInstance());
    }

}
