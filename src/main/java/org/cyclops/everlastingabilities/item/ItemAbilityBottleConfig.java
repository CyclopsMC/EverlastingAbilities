package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.cyclops.cyclopscore.config.extendedconfig.ItemConfig;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemDataMutableAbilityStore;

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
        EverlastingAbilities._instance.getModEventBus().addListener(this::modifyComponents);
        EverlastingAbilities._instance.getModEventBus().addListener(this::registerCapability);
    }

    protected void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(getInstance(), (builder) -> builder.set(RegistryEntries.DATACOMPONENT_ABILITY_STORE.get(), new DefaultMutableAbilityStore()));
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.MutableAbilityStore.ITEM, (stack, context) -> new ItemDataMutableAbilityStore(stack), getInstance());
    }

}
