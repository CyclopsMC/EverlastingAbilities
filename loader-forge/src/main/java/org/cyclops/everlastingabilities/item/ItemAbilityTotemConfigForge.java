package org.cyclops.everlastingabilities.item;

import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.GatherComponentsEvent;
import org.cyclops.everlastingabilities.EverlastingAbilitiesForge;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfigForge extends ItemAbilityTotemConfig<EverlastingAbilitiesForge> {

    public ItemAbilityTotemConfigForge() {
        super(EverlastingAbilitiesForge._instance,
                (eConfig, properties) -> new ItemAbilityTotemForge(properties
                        .stacksTo(1)));
        BuildCreativeModeTabContentsEvent.BUS.addListener(this::onCreativeModeTabBuildContents);
        GatherComponentsEvent.Item.BUS.addListener(this::modifyComponents);
    }

    protected void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        this.onCreativeModeTabBuildContentsCommon(event.getTab(), event.getParameters(), event::accept);
    }

    protected void modifyComponents(GatherComponentsEvent.Item event) {
        if (event.getOwner() == getInstance()) {
            event.register(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore());
        }
    }
}
