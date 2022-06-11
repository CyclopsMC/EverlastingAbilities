package org.cyclops.everlastingabilities.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * Holder class for the ability registry.
 * @author rubensworks
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityTypes {

    public static IForgeRegistry<IAbilityType> REGISTRY;

    @SubscribeEvent
    public static void onRegistriesCreate(NewRegistryEvent event) {
        event.create(new RegistryBuilder<IAbilityType>()
                .setName(new ResourceLocation("everlastingabilities", "abilities")),
                registry -> REGISTRY = registry);
    }

}
