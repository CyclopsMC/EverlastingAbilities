package org.cyclops.everlastingabilities.core.helper;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Rarity;
import org.cyclops.everlastingabilities.ability.AbilityTypeEffect;

import java.util.Locale;

/**
 * @author rubensworks
 */
public class CodecHelpers {

    public static final Codec<Rarity> CODEC_RARITY = ExtraCodecs.orCompressed(
            ExtraCodecs.stringResolverCodec(
                    rarity -> rarity.name().toLowerCase(Locale.ROOT),
                    name -> Rarity.valueOf(name.toUpperCase(Locale.ROOT))
            ),
            ExtraCodecs.idResolverCodec(
                    Enum::ordinal,
                    (id) -> id >= 0 && id < Rarity.values().length ? Rarity.values()[id] : null, -1
            )
    );

    public static final Codec<AbilityTypeEffect.Target> CODEC_TARGET = ExtraCodecs.orCompressed(
            ExtraCodecs.stringResolverCodec(
                    rarity -> rarity.name().toLowerCase(Locale.ROOT),
                    name -> AbilityTypeEffect.Target.valueOf(name.toUpperCase(Locale.ROOT))
            ),
            ExtraCodecs.idResolverCodec(
                    Enum::ordinal,
                    (id) -> id >= 0 && id < AbilityTypeEffect.Target.values().length ? AbilityTypeEffect.Target.values()[id] : null, -1
            )
    );

}
