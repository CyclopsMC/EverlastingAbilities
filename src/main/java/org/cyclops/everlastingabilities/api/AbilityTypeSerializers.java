package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Function;

/**
 * @author rubensworks
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AbilityTypeSerializers {

    public static IForgeRegistry<Codec<? extends IAbilityType>> REGISTRY;

    /**
     * Codec for (de)serializing abilities inline.
     * Mods can use this for data generation.
     */
    public static final Codec<IAbilityType> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> REGISTRY.getCodec())
            .dispatch(IAbilityType::codec, Function.identity());

    @SubscribeEvent
    public static void onRegistriesCreate(NewRegistryEvent event) {
        event.create(new RegistryBuilder<Codec<? extends IAbilityType>>()
                        .setName(new ResourceLocation("everlastingabilities", "ability_serializers"))
                        .disableSaving()
                        .disableSync(),
                registry -> REGISTRY = registry);
    }

}
