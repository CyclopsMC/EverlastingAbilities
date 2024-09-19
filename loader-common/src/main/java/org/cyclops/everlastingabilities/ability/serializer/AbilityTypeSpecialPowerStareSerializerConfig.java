package org.cyclops.everlastingabilities.ability.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.ability.AbilityTypeSpecialPowerStare;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;
import org.cyclops.everlastingabilities.core.helper.CodecHelpers;

/**
 * @author rubensworks
 */
public class AbilityTypeSpecialPowerStareSerializerConfig<M extends IModBase> extends AbilitySerializerConfig<AbilityTypeSpecialPowerStare, M> {

    public AbilityTypeSpecialPowerStareSerializerConfig(M mod) {
        super(mod, "special_power_stare", (eConfig) -> RecordCodecBuilder.mapCodec(builder -> builder
                .group(
                        EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityConditionCodec().optionalFieldOf("condition", EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAbilityConditionTrue()).forGetter(IAbilityType::getCondition),
                        Codec.STRING.fieldOf("name").forGetter(IAbilityType::getTranslationKey),
                        CodecHelpers.CODEC_RARITY.fieldOf("rarity").forGetter(IAbilityType::getRarity),
                        Codec.INT.fieldOf("max_level").forGetter(IAbilityType::getMaxLevel),
                        Codec.INT.fieldOf("xp_per_level").forGetter(IAbilityType::getXpPerLevelRaw),
                        Codec.BOOL.optionalFieldOf("obtainable_on_player_spawn", true).forGetter(IAbilityType::isObtainableOnPlayerSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_mob_spawn", true).forGetter(IAbilityType::isObtainableOnMobSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_craft", true).forGetter(IAbilityType::isObtainableOnCraft),
                        Codec.BOOL.optionalFieldOf("obtainable_on_loot", true).forGetter(IAbilityType::isObtainableOnLoot),
                        Codec.BOOL.optionalFieldOf("require_sneak", true).forGetter(AbilityTypeSpecialPowerStare::isRequireSneak))
                .apply(builder, AbilityTypeSpecialPowerStare::new))
        );
    }
}
