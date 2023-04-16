package org.cyclops.everlastingabilities.ability.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.cyclops.everlastingabilities.ability.AbilityTypeEffect;
import org.cyclops.everlastingabilities.ability.AbilityTypeSpecialStepAssist;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * @author rubensworks
 */
public class AbilityTypeSpecialStepAssistSerializerConfig extends AbilitySerializerConfig<AbilityTypeSpecialStepAssist> {

    public AbilityTypeSpecialStepAssistSerializerConfig() {
        super("special_step_assist", (eConfig) -> RecordCodecBuilder.create(builder -> builder
                .group(
                        Codec.STRING.fieldOf("name").forGetter(IAbilityType::getTranslationKey),
                        AbilityTypeEffect.CODEC_RARITY.fieldOf("rarity").forGetter(IAbilityType::getRarity),
                        Codec.INT.fieldOf("max_level").forGetter(IAbilityType::getMaxLevel),
                        Codec.INT.fieldOf("xp_per_level").forGetter(IAbilityType::getXpPerLevelRaw),
                        Codec.BOOL.optionalFieldOf("obtainable_on_player_spawn", true).forGetter(IAbilityType::isObtainableOnPlayerSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_mob_spawn", true).forGetter(IAbilityType::isObtainableOnMobSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_craft", true).forGetter(IAbilityType::isObtainableOnCraft),
                        Codec.BOOL.optionalFieldOf("obtainable_on_loot", true).forGetter(IAbilityType::isObtainableOnLoot),
                        Codec.BOOL.optionalFieldOf("force_default_step_height", true).forGetter(AbilityTypeSpecialStepAssist::isForceDefaultStepHeight))
                .apply(builder, AbilityTypeSpecialStepAssist::new))
        );
    }
}