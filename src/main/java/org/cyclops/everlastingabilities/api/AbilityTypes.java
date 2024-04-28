package org.cyclops.everlastingabilities.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;


/**
 * Holder class for the ability registry.
 * @author rubensworks
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityTypes {

    public static final ResourceKey<Registry<IAbilityType>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(("everlastingabilities:abilities")));

    public static final DeferredRegister<IAbilityType> REGISTRY = DeferredRegister
            .create(REGISTRY_KEY, "everlastingabilities");

    @SubscribeEvent
    public static void onDatapackRegistryCreate(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY_KEY, AbilityTypeSerializers.DIRECT_CODEC, AbilityTypeSerializers.DIRECT_CODEC);
    }

}
