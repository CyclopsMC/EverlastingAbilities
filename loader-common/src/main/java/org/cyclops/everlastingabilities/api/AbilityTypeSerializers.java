package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * @author rubensworks
 */
public class AbilityTypeSerializers {

    public static final ResourceKey<Registry<MapCodec<? extends IAbilityType>>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("everlastingabilities", "ability_serializers"));
    public static Registry<MapCodec<? extends IAbilityType>> REGISTRY;
    public static Codec<MapCodec<? extends IAbilityType>> NAME_CODEC;

}
