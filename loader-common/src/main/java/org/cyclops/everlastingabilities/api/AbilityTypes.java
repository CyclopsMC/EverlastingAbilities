package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;


/**
 * Holder class for the ability registry.
 * @author rubensworks
 */
public class AbilityTypes {

    public static final ResourceKey<Registry<IAbilityType>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("everlastingabilities", "abilities"));

    public static final Codec<IAbilityType> DIRECT_CODEC = Codec.lazyInitialized(() -> AbilityTypeSerializers.NAME_CODEC)
            .dispatch(IAbilityType::codec, Function.identity());
    public static final Codec<Holder<IAbilityType>> REFERENCE_CODEC = RegistryFileCodec.create(REGISTRY_KEY, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IAbilityType>> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(REFERENCE_CODEC);

}
