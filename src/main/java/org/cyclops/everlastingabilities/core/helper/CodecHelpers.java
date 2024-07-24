package org.cyclops.everlastingabilities.core.helper;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.cyclops.everlastingabilities.ability.AbilityTypeEffect;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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
    public static final Codec<AttributeModifier.Operation> CODEC_ATTRIBUTE_MODIFIER_OPERATION = ExtraCodecs.stringResolverCodec(
            operation -> operation.name().toLowerCase(Locale.ROOT),
            name -> switch (name) {
                case "add_value" -> AttributeModifier.Operation.ADDITION;
                case "add_multiplied_base" -> AttributeModifier.Operation.MULTIPLY_BASE;
                case "add_multiplied_total" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
                default -> AttributeModifier.Operation.valueOf(name.toUpperCase(Locale.ROOT));
            }
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

    public static final Codec<ICondition> CODEC_CONDITION = new PrimitiveCodec<ICondition>() {
        @Override
        public <T> DataResult<ICondition> read(DynamicOps<T> ops, T input) {
            try {
                return DataResult.success(CraftingHelper.getCondition((JsonObject) opsToJson(ops, input)));
            } catch (RuntimeException error) {
                return DataResult.error(error::getMessage);
            }
        }

        @Override
        public <T> T write(DynamicOps<T> ops, ICondition value) {
            return jsonToOps(ops, CraftingHelper.serialize(value));
        }
    };

    public static <T> JsonElement opsToJson(DynamicOps<T> ops, T input) {
        if (input instanceof JsonElement jsonElement) {
            return jsonElement;
        }

        Optional<String> optionalString = ops.getStringValue(input).result();
        if (optionalString.isPresent()) {
            return new JsonPrimitive(optionalString.get());
        }

        Optional<Number> optionalNumber = ops.getNumberValue(input).result();
        if (optionalNumber.isPresent()) {
            return new JsonPrimitive(optionalNumber.get());
        }

        Optional<MapLike<T>> optionalMap = ops.getMap(input).result();
        if (optionalMap.isPresent()) {
            MapLike<T> map = optionalMap.get();
            JsonObject jsonObject = new JsonObject();
            map.entries().forEach(pair -> {
                jsonObject.add(opsToJson(ops, pair.getFirst()).getAsString(), opsToJson(ops, pair.getSecond()));
            });
            return jsonObject;
        }

        throw new IllegalArgumentException("Unknown JSON entry " + input);
    }

    public static <T> T jsonToOps(DynamicOps<T> ops, JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isString()) {
                return ops.createString(primitive.getAsString());
            }
            if (primitive.isNumber()) {
                return ops.createNumeric(primitive.getAsNumber());
            }
        }

        if (jsonElement instanceof JsonObject jsonObject) {
            HashMap<T, T> map = Maps.newHashMap();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                map.put(ops.createString(entry.getKey()), jsonToOps(ops, entry.getValue()));
            }
            return ops.createMap(map);
        }

        throw new IllegalArgumentException("Unknown JSON entry " + jsonElement);
    }

}
