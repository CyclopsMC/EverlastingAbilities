package org.cyclops.everlastingabilities.ability.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import org.cyclops.everlastingabilities.ability.AbilityTypeEffect;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;
import org.cyclops.everlastingabilities.core.helper.ApplicativeExtended;
import org.cyclops.everlastingabilities.core.helper.CodecHelpers;

/**
 * @author rubensworks
 */
public class AbilityTypeEffectSerializerConfig extends AbilitySerializerConfig<AbilityTypeEffect> {

    public AbilityTypeEffectSerializerConfig() {
        super("effect", (eConfig) -> RecordCodecBuilder.create(builder -> ApplicativeExtended.group17(
                builder,
                        CodecHelpers.CODEC_CONDITION.optionalFieldOf("condition", TrueCondition.INSTANCE).forGetter(IAbilityType::getCondition),
                        Codec.STRING.fieldOf("name").forGetter(AbilityTypeEffect::getTranslationKey),
                        CodecHelpers.CODEC_RARITY.fieldOf("rarity").forGetter(AbilityTypeEffect::getRarity),
                        Codec.INT.fieldOf("max_level").forGetter(AbilityTypeEffect::getMaxLevel),
                        Codec.INT.fieldOf("xp_per_level").forGetter(AbilityTypeEffect::getXpPerLevelRaw),
                        Codec.BOOL.optionalFieldOf("obtainable_on_player_spawn", true).forGetter(AbilityTypeEffect::isObtainableOnPlayerSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_mob_spawn", true).forGetter(AbilityTypeEffect::isObtainableOnMobSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_craft", true).forGetter(AbilityTypeEffect::isObtainableOnCraft),
                        Codec.BOOL.optionalFieldOf("obtainable_on_loot", true).forGetter(AbilityTypeEffect::isObtainableOnLoot),
                        CodecHelpers.CODEC_TARGET.fieldOf("target").forGetter(AbilityTypeEffect::getTarget),
                        Codec.BOOL.optionalFieldOf("targets_friendly_mobs", true).forGetter(AbilityTypeEffect::isTargetsFriendlyMobs),
                        Codec.DOUBLE.optionalFieldOf("radius_factor", 2D).forGetter(AbilityTypeEffect::getRadiusFactor),
                        Codec.STRING.fieldOf("effect").forGetter(AbilityTypeEffect::getEffectId),
                        Codec.INT.optionalFieldOf("tick_modulus", 10).forGetter(AbilityTypeEffect::getTickModulus),
                        Codec.DOUBLE.optionalFieldOf("amplifier_factor", 1D).forGetter(AbilityTypeEffect::getAmplifierFactor),
                        Codec.BOOL.optionalFieldOf("level_based_duration", false).forGetter(AbilityTypeEffect::isLevelBasedDuration),
                        Codec.DOUBLE.optionalFieldOf("duration_factor", 5D).forGetter(AbilityTypeEffect::getDurationFactor))
                .apply(builder, AbilityTypeEffect::new))
        );
    }
}
