package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GatherComponentsEvent;
import org.cyclops.everlastingabilities.EverlastingAbilitiesForge;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;

/**
 * Config for the ability bottle.
 * @author rubensworks
 */
public class ItemAbilityBottleConfigForge extends ItemAbilityBottleConfig<EverlastingAbilitiesForge> {

    public ItemAbilityBottleConfigForge() {
        super(EverlastingAbilitiesForge._instance,
                (eConfig) -> new ItemAbilityBottleForge(new Item.Properties()
                        .stacksTo(1)));
        MinecraftForge.EVENT_BUS.addListener(this::modifyComponents);
    }

    protected void modifyComponents(GatherComponentsEvent.Item event) {
        if (event.getOwner() == getInstance()) {
            event.register(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore());
        }
    }

}
