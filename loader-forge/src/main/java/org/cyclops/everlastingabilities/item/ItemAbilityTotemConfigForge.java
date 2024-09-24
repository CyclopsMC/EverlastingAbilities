package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.GatherComponentsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
                (eConfig) -> new ItemAbilityTotemForge(new Item.Properties()
                        .stacksTo(1)));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCreativeModeTabBuildContents);
        MinecraftForge.EVENT_BUS.addListener(this::modifyComponents);
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
