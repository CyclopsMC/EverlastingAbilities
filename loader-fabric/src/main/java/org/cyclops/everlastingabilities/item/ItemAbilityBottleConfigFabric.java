package org.cyclops.everlastingabilities.item;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.world.item.Item;
import org.cyclops.everlastingabilities.EverlastingAbilitiesFabric;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;

/**
 * Config for the ability bottle.
 * @author rubensworks
 */
public class ItemAbilityBottleConfigFabric extends ItemAbilityBottleConfig<EverlastingAbilitiesFabric> {

    public ItemAbilityBottleConfigFabric() {
        super(EverlastingAbilitiesFabric._instance,
                (eConfig) -> new ItemAbilityBottleFabric(new Item.Properties()
                        .stacksTo(1)));
        DefaultItemComponentEvents.MODIFY.register(this::onSetDefaultComponents);
    }

    private void onSetDefaultComponents(DefaultItemComponentEvents.ModifyContext modifyContext) {
        modifyContext.modify(getInstance(), (builder) -> builder.set(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore()));
    }

}
