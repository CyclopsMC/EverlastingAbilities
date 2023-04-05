package org.cyclops.everlastingabilities.ability.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectSelf;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * @author rubensworks
 */
public class AbilityTypePotionEffectSelfSerializerConfig extends AbilitySerializerConfig<AbilityTypePotionEffectSelf> {

    public AbilityTypePotionEffectSelfSerializerConfig() {
        super("potion_effect_self", (eConfig) -> RecordCodecBuilder.create(builder -> builder
                .group(
                        Codec.STRING.fieldOf("name").forGetter(AbilityTypePotionEffectSelf::getTranslationKey),
                        AbilityHelpers.CODEC_RARITY.fieldOf("rarity").forGetter(AbilityTypePotionEffectSelf::getRarity),
                        Codec.INT.fieldOf("max_level").forGetter(AbilityTypePotionEffectSelf::getMaxLevel),
                        Codec.INT.fieldOf("xp_per_level").forGetter(AbilityTypePotionEffectSelf::getXpPerLevelRaw),
                        Codec.BOOL.optionalFieldOf("obtainable_on_player_spawn", true).forGetter(AbilityTypePotionEffectSelf::isObtainableOnPlayerSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_mob_spawn", true).forGetter(AbilityTypePotionEffectSelf::isObtainableOnMobSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_craft", true).forGetter(AbilityTypePotionEffectSelf::isObtainableOnCraft),
                        Codec.BOOL.optionalFieldOf("obtainable_on_loot", true).forGetter(AbilityTypePotionEffectSelf::isObtainableOnLoot),
                        Codec.STRING.fieldOf("potion_effect").forGetter(AbilityTypePotionEffectSelf::getPotionEffectId),
                        Codec.INT.optionalFieldOf("tick_modulus", 10).forGetter(AbilityTypePotionEffectSelf::getTickModulus),
                        Codec.DOUBLE.optionalFieldOf("amplifier_factor", 1D).forGetter(AbilityTypePotionEffectSelf::getAmplifierFactor),
                        Codec.BOOL.optionalFieldOf("level_based_duration", false).forGetter(AbilityTypePotionEffectSelf::isLevelBasedDuration),
                        Codec.DOUBLE.optionalFieldOf("duration_factor", 5D).forGetter(AbilityTypePotionEffectSelf::getDurationFactor))
                .apply(builder, AbilityTypePotionEffectSelf::new))
        );
    }
}
