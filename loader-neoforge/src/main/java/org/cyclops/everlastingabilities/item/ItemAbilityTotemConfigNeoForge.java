package org.cyclops.everlastingabilities.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.ItemDataMutableAbilityStore;

/**
 * Config for the ability totem.
 * @author rubensworks
 */
public class ItemAbilityTotemConfigNeoForge extends ItemAbilityTotemConfig<EverlastingAbilities> {

    public ItemAbilityTotemConfigNeoForge() {
        super(EverlastingAbilities._instance,
                (eConfig) -> new ItemAbilityTotemNeoForge(new Item.Properties()
                        .stacksTo(1)));
        EverlastingAbilities._instance.getModEventBus().addListener(this::onCreativeModeTabBuildContents);
        EverlastingAbilities._instance.getModEventBus().addListener(this::modifyComponents);
        EverlastingAbilities._instance.getModEventBus().addListener(this::registerCapability);
    }

    protected void onCreativeModeTabBuildContents(BuildCreativeModeTabContentsEvent event) {
        this.onCreativeModeTabBuildContentsCommon(event.getTab(), event.getParameters(), event::accept);
    }

    protected void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(getInstance(), (builder) -> builder.set(RegistryEntries.DATACOMPONENT_ABILITY_STORE.value(), new DefaultMutableAbilityStore()));
    }

    protected void registerCapability(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.MutableAbilityStore.ITEM, (stack, context) -> new ItemDataMutableAbilityStore(stack, () -> stack.set(DataComponents.RARITY, ItemAbilityTotem.getRarity(stack))), getInstance());
    }
}
