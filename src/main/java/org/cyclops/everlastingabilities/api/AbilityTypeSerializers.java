package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Function;

/**
 * @author rubensworks
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityTypeSerializers {

    public static final ResourceKey<Registry<Codec<? extends IAbilityType>>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation("everlastingabilities", "ability_serializers"));
    public static Registry<Codec<? extends IAbilityType>> REGISTRY;

    /**
     * Codec for (de)serializing abilities inline.
     * Mods can use this for data generation.
     */
    public static final Codec<IAbilityType> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> REGISTRY.byNameCodec())
            .dispatch(IAbilityType::codec, Function.identity());

    @SubscribeEvent
    public static void onRegistriesCreate(NewRegistryEvent event) {
        REGISTRY = event.create(new RegistryBuilder<>(REGISTRY_KEY));
    }

}
