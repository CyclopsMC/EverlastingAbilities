package org.cyclops.everlastingabilities.item;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.EverlastingAbilitiesNeoForge;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemDataMutableAbilityStore;

/**
 * Config for the ability bottle.
 * @author rubensworks
 */
public class ItemAbilityBottleConfigNeoForge extends ItemAbilityBottleConfig<EverlastingAbilitiesNeoForge> {

    public ItemAbilityBottleConfigNeoForge() {
        super(EverlastingAbilitiesNeoForge._instance,
                (eConfig, properties) -> new ItemAbilityBottleNeoForge(properties
                        .stacksTo(1)));
        EverlastingAbilitiesNeoForge._instance.getModEventBus().addListener(this::modifyComponents);
        EverlastingAbilitiesNeoForge._instance.getModEventBus().addListener(this::registerCapability);
    }

    protected void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(getInstance(), (builder) -> builder.set(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore()));
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.MutableAbilityStore.ITEM, (stack, context) -> new ItemDataMutableAbilityStore(stack), getInstance());
    }

}
