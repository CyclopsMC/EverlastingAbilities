package org.cyclops.everlastingabilities.ability;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.IAbilityTypeRegistry;

/**
 * All ability types.
 * @author rubensworks
 */
public class AbilityTypes {

    public static final IAbilityTypeRegistry REGISTRY = EverlastingAbilities._instance.getRegistryManager().getRegistry(IAbilityTypeRegistry.class);

    public static IAbilityType SPEED = null;
    public static IAbilityType HASTE = null;
    public static IAbilityType STRENGTH = null;
    public static IAbilityType JUMP_BOOST = null;
    public static IAbilityType REGENERATION = null;
    public static IAbilityType RESISTANCE = null;
    public static IAbilityType FIRE_RESISTANCE = null;
    public static IAbilityType WATER_BREATHING = null;
    public static IAbilityType INVISIBILITY = null;
    public static IAbilityType NIGHT_VISION = null;
    public static IAbilityType ABSORPTION = null;
    public static IAbilityType SATURATION = null;
    public static IAbilityType LUCK = null;

    public static IAbilityType SLOWNESS = null;
    public static IAbilityType MINING_FATIGUE = null;
    public static IAbilityType NAUSEA = null;
    public static IAbilityType BLINDNESS = null;
    public static IAbilityType HUNGER = null;
    public static IAbilityType WEAKNESS = null;
    public static IAbilityType POISON = null;
    public static IAbilityType WITHER = null;
    public static IAbilityType GLOWING = null;
    public static IAbilityType LEVITATION = null;
    public static IAbilityType UNLUCK = null;

    public static IAbilityType FLIGHT = null;
    public static IAbilityType STEP_ASSIST = null;
    public static IAbilityType FERTILITY = null;
    public static IAbilityType BONEMEALER = null;
    public static IAbilityType POWER_STARE = null;
    public static IAbilityType MAGNETIZE = null;

    public static void load() {
        SPEED = REGISTRY.register(new AbilityTypePotionEffectSelf("speed", EnumRarity.COMMON, 5, 10, MobEffects.SPEED));
        HASTE = REGISTRY.register(new AbilityTypePotionEffectSelf("haste", EnumRarity.COMMON, 5, 15, MobEffects.HASTE));
        STRENGTH = REGISTRY.register(new AbilityTypePotionEffectSelf("strength", EnumRarity.UNCOMMON, 5, 20, MobEffects.STRENGTH));
        JUMP_BOOST = REGISTRY.register(new AbilityTypePotionEffectSelf("jump_boost", EnumRarity.COMMON, 5, 10, MobEffects.JUMP_BOOST));
        REGENERATION = REGISTRY.register(new AbilityTypePotionEffectSelf("regeneration", EnumRarity.RARE, 3, 50, MobEffects.REGENERATION));
        RESISTANCE = REGISTRY.register(new AbilityTypePotionEffectSelf("resistance", EnumRarity.RARE, 3, 50, MobEffects.RESISTANCE));
        FIRE_RESISTANCE = REGISTRY.register(new AbilityTypePotionEffectSelf("fire_resistance", EnumRarity.UNCOMMON, 1, 20, MobEffects.FIRE_RESISTANCE));
        WATER_BREATHING = REGISTRY.register(new AbilityTypePotionEffectSelf("water_breathing", EnumRarity.COMMON, 1, 10, MobEffects.WATER_BREATHING));
        INVISIBILITY = REGISTRY.register(new AbilityTypePotionEffectSelf("invisibility", EnumRarity.UNCOMMON, 1, 20, MobEffects.INVISIBILITY));
        NIGHT_VISION = REGISTRY.register(new AbilityTypePotionEffectSelf("night_vision", EnumRarity.UNCOMMON, 1, 15, MobEffects.NIGHT_VISION) {
            @Override
            protected int getDurationMultiplier() {
                return 27;
            }
        });
        ABSORPTION = REGISTRY.register(new AbilityTypePotionEffectSelf("absorbtion", EnumRarity.RARE, 5, 75, MobEffects.ABSORPTION));
        SATURATION = REGISTRY.register(new AbilityTypePotionEffectSelf("saturation", EnumRarity.RARE, 3, 30, MobEffects.SATURATION));
        LUCK = REGISTRY.register(new AbilityTypePotionEffectSelf("luck", EnumRarity.RARE, 3, 40, MobEffects.LUCK));

        SLOWNESS = REGISTRY.register(new AbilityTypePotionEffectRadius("slowness", EnumRarity.UNCOMMON, 3, 40, MobEffects.SLOWNESS));
        MINING_FATIGUE = REGISTRY.register(new AbilityTypePotionEffectRadius("mining_fatigue", EnumRarity.UNCOMMON, 3, 40, MobEffects.MINING_FATIGUE));
        NAUSEA = REGISTRY.register(new AbilityTypePotionEffectRadius("nausea", EnumRarity.UNCOMMON, 3, 50, MobEffects.NAUSEA));
        BLINDNESS = REGISTRY.register(new AbilityTypePotionEffectRadius("blindness", EnumRarity.RARE, 3, 50, MobEffects.BLINDNESS));
        HUNGER = REGISTRY.register(new AbilityTypePotionEffectRadius("hunger", EnumRarity.RARE, 3, 70, MobEffects.HUNGER));
        WEAKNESS = REGISTRY.register(new AbilityTypePotionEffectRadius("weakness", EnumRarity.RARE, 3, 80, MobEffects.WEAKNESS));
        POISON = REGISTRY.register(new AbilityTypePotionEffectRadius("poison", EnumRarity.EPIC, 3, 100, MobEffects.POISON));
        WITHER = REGISTRY.register(new AbilityTypePotionEffectRadius("wither", EnumRarity.EPIC, 3, 125, MobEffects.WITHER));
        GLOWING = REGISTRY.register(new AbilityTypePotionEffectRadius("glowing", EnumRarity.UNCOMMON, 3, 50, MobEffects.GLOWING));
        LEVITATION = REGISTRY.register(new AbilityTypePotionEffectRadius("levitation", EnumRarity.RARE, 3, 75, MobEffects.LEVITATION));
        UNLUCK = REGISTRY.register(new AbilityTypePotionEffectRadius("unluck", EnumRarity.RARE, 3, 50, MobEffects.UNLUCK));

        FLIGHT = REGISTRY.register(new AbilityTypeFlight("flight", EnumRarity.EPIC, 1, 150));
        STEP_ASSIST = REGISTRY.register(new AbilityTypeStepAssist("step_assist", EnumRarity.COMMON, 3, 25));
        FERTILITY = REGISTRY.register(new AbilityTypeFertility("fertility", EnumRarity.UNCOMMON, 3, 30));
        BONEMEALER = REGISTRY.register(new AbilityTypeBonemealer("bonemealer", EnumRarity.UNCOMMON, 5, 30));
        POWER_STARE = REGISTRY.register(new AbilityTypePowerStare("power_stare", EnumRarity.UNCOMMON, 5, 50));
        MAGNETIZE = REGISTRY.register(new AbilityTypeMagnetize("magnetize", EnumRarity.UNCOMMON, 5, 20));
    }

}
