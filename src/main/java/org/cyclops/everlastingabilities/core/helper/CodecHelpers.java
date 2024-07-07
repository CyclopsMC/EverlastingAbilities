package org.cyclops.everlastingabilities.core.helper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Rarity;
import org.cyclops.everlastingabilities.ability.AbilityTypeEffect;
import org.cyclops.everlastingabilities.api.AbilityTypes;
import org.cyclops.everlastingabilities.api.capability.DefaultAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author rubensworks
 */
public class CodecHelpers {

    public static final Codec<Rarity> CODEC_RARITY = ExtraCodecs.orCompressed(
            Codec.stringResolver(
                    rarity -> rarity.name().toLowerCase(Locale.ROOT),
                    name -> Rarity.valueOf(name.toUpperCase(Locale.ROOT))
            ),
            ExtraCodecs.idResolverCodec(
                    Enum::ordinal,
                    (id) -> id >= 0 && id < Rarity.values().length ? Rarity.values()[id] : null, -1
            )
    );

    public static final Codec<AbilityTypeEffect.Target> CODEC_TARGET = ExtraCodecs.orCompressed(
            Codec.stringResolver(
                    rarity -> rarity.name().toLowerCase(Locale.ROOT),
                    name -> AbilityTypeEffect.Target.valueOf(name.toUpperCase(Locale.ROOT))
            ),
            ExtraCodecs.idResolverCodec(
                    Enum::ordinal,
                    (id) -> id >= 0 && id < AbilityTypeEffect.Target.values().length ? AbilityTypeEffect.Target.values()[id] : null, -1
            )
    );

    public static final Codec<IAbilityStore> CODEC_ABILITY_STORE = RecordCodecBuilder.create(builder -> builder
            .group(
                    ExtraCodecs.strictUnboundedMap(AbilityTypes.REFERENCE_CODEC, Codec.INT)
                            .fieldOf("abilities").forGetter(IAbilityStore::getAbilitiesRaw)
            )
            .apply(builder, DefaultAbilityStore::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, IAbilityStore> STREAM_CODEC_ABILITY_STORE = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, AbilityTypes.STREAM_CODEC, ByteBufCodecs.INT), IAbilityStore::getAbilitiesRaw,
            DefaultAbilityStore::new
    );

}
