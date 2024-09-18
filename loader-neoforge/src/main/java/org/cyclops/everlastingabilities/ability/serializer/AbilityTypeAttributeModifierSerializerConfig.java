package org.cyclops.everlastingabilities.ability.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.ability.AbilityTypeAttributeModifier;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;
import org.cyclops.everlastingabilities.core.helper.CodecHelpers;

/**
 * @author rubensworks
 */
public class AbilityTypeAttributeModifierSerializerConfig extends AbilitySerializerConfig<AbilityTypeAttributeModifier> {
    public AbilityTypeAttributeModifierSerializerConfig() {
        super("attribute_modifier", (eConfig) -> RecordCodecBuilder.mapCodec(builder -> builder.group(
                        EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityConditionCodec().optionalFieldOf("condition", EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityConditionTrue()).forGetter(IAbilityType::getCondition),
                        Codec.STRING.fieldOf("name").forGetter(AbilityTypeAttributeModifier::getTranslationKey),
                        CodecHelpers.CODEC_RARITY.fieldOf("rarity").forGetter(AbilityTypeAttributeModifier::getRarity),
                        Codec.INT.fieldOf("max_level").forGetter(AbilityTypeAttributeModifier::getMaxLevel),
                        Codec.INT.fieldOf("xp_per_level").forGetter(AbilityTypeAttributeModifier::getXpPerLevelRaw),
                        Codec.BOOL.optionalFieldOf("obtainable_on_player_spawn", true).forGetter(AbilityTypeAttributeModifier::isObtainableOnPlayerSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_mob_spawn", true).forGetter(AbilityTypeAttributeModifier::isObtainableOnMobSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_craft", true).forGetter(AbilityTypeAttributeModifier::isObtainableOnCraft),
                        Codec.BOOL.optionalFieldOf("obtainable_on_loot", true).forGetter(AbilityTypeAttributeModifier::isObtainableOnLoot),
                        Codec.STRING.fieldOf("attribute").forGetter(AbilityTypeAttributeModifier::getAttributeId),
                        Codec.DOUBLE.optionalFieldOf("amount_factor", 1D).forGetter(AbilityTypeAttributeModifier::getAmountFactor),
                        AttributeModifier.Operation.CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADD_VALUE).forGetter(AbilityTypeAttributeModifier::getOperation))
                .apply(builder, AbilityTypeAttributeModifier::new))
        );
    }
}
