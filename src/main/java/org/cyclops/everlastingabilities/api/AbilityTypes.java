package org.cyclops.everlastingabilities.api;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

/**
 * Holder class for the ability registry.
 * @author rubensworks
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityTypes {

    public static final ResourceKey<Registry<IAbilityType>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(("everlastingabilities:abilities")));

    public static final DeferredRegister<IAbilityType> REGISTRY = DeferredRegister
            .create(REGISTRY_KEY, "everlastingabilities");
    public static final Supplier<IForgeRegistry<IAbilityType>> REGISTRY_BUILTIN = REGISTRY
            .makeRegistry(() -> new RegistryBuilder<IAbilityType>()
                    .disableSaving()
                    .dataPackRegistry(AbilityTypeSerializers.DIRECT_CODEC, AbilityTypeSerializers.DIRECT_CODEC));

}
