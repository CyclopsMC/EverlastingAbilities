package org.cyclops.everlastingabilities.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * Holder class for the ability registry.
 * @author rubensworks
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityTypes {

    public static IForgeRegistry<IAbilityType> REGISTRY;

    @SubscribeEvent
    public static void onRegistriesCreate(RegistryEvent.NewRegistry event) {
        REGISTRY = new RegistryBuilder<IAbilityType>()
                .setName(new ResourceLocation("everlastingabilities", "abilities"))
                .setType(IAbilityType.class)
                .create();
    }

}
