package org.cyclops.everlastingabilities.ability.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.ability.AbilityTypePotionEffectRadius;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * @author rubensworks
 */
public class AbilityTypePotionEffectRadiusSerializerConfig extends AbilitySerializerConfig<AbilityTypePotionEffectRadius> {

    public AbilityTypePotionEffectRadiusSerializerConfig() {
        super("potion_effect_radius", (eConfig) -> RecordCodecBuilder.create(builder -> builder
                .group(
                        Codec.STRING.fieldOf("name").forGetter(AbilityTypePotionEffectRadius::getTranslationKey),
                        AbilityHelpers.CODEC_RARITY.fieldOf("rarity").forGetter(AbilityTypePotionEffectRadius::getRarity),
                        Codec.INT.fieldOf("max_level").forGetter(AbilityTypePotionEffectRadius::getMaxLevel),
                        Codec.INT.fieldOf("xp_per_level").forGetter(AbilityTypePotionEffectRadius::getXpPerLevelRaw),
                        Codec.BOOL.optionalFieldOf("obtainable_on_player_spawn", true).forGetter(AbilityTypePotionEffectRadius::isObtainableOnPlayerSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_mob_spawn", true).forGetter(AbilityTypePotionEffectRadius::isObtainableOnMobSpawn),
                        Codec.BOOL.optionalFieldOf("obtainable_on_craft", true).forGetter(AbilityTypePotionEffectRadius::isObtainableOnCraft),
                        Codec.BOOL.optionalFieldOf("obtainable_on_loot", true).forGetter(AbilityTypePotionEffectRadius::isObtainableOnLoot),
                        Codec.STRING.fieldOf("potion_effect").forGetter(AbilityTypePotionEffectRadius::getPotionEffectId),
                        Codec.BOOL.optionalFieldOf("hostile", true).forGetter(AbilityTypePotionEffectRadius::isHostile))
                .apply(builder, AbilityTypePotionEffectRadius::new))
        );
    }
}
