package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

/**
 * @author rubensworks
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AbilityTypeSerializers {

    public static final ResourceKey<Registry<MapCodec<? extends IAbilityType>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("everlastingabilities", "ability_serializers"));
    public static Registry<MapCodec<? extends IAbilityType>> REGISTRY;

    @SubscribeEvent
    public static void onRegistriesCreate(NewRegistryEvent event) {
        REGISTRY = event.create(new RegistryBuilder<>(REGISTRY_KEY));
    }

}
