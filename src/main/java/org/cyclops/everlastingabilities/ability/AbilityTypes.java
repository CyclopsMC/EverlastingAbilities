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

    public static IAbilityType FLIGHT = null;
    public static IAbilityType STEP_ASSIST = null;

    public static void load() {
        SPEED = REGISTRY.register(new AbilityTypePotionEffect("speed", EnumRarity.COMMON, 5, 10, MobEffects.SPEED));
        HASTE = REGISTRY.register(new AbilityTypePotionEffect("haste", EnumRarity.COMMON, 5, 15, MobEffects.HASTE));
        STRENGTH = REGISTRY.register(new AbilityTypePotionEffect("strength", EnumRarity.UNCOMMON, 5, 20, MobEffects.STRENGTH));
        JUMP_BOOST = REGISTRY.register(new AbilityTypePotionEffect("jump_boost", EnumRarity.COMMON, 5, 10, MobEffects.JUMP_BOOST));
        REGENERATION = REGISTRY.register(new AbilityTypePotionEffect("regeneration", EnumRarity.RARE, 3, 50, MobEffects.REGENERATION));
        RESISTANCE = REGISTRY.register(new AbilityTypePotionEffect("resistance", EnumRarity.RARE, 3, 50, MobEffects.RESISTANCE));
        FIRE_RESISTANCE = REGISTRY.register(new AbilityTypePotionEffect("fire_resistance", EnumRarity.UNCOMMON, 1, 20, MobEffects.FIRE_RESISTANCE));
        WATER_BREATHING = REGISTRY.register(new AbilityTypePotionEffect("water_breathing", EnumRarity.COMMON, 1, 10, MobEffects.WATER_BREATHING));
        INVISIBILITY = REGISTRY.register(new AbilityTypePotionEffect("invisibility", EnumRarity.UNCOMMON, 1, 20, MobEffects.INVISIBILITY));
        NIGHT_VISION = REGISTRY.register(new AbilityTypePotionEffect("night_vision", EnumRarity.UNCOMMON, 1, 15, MobEffects.NIGHT_VISION) {
            @Override
            protected int getDurationMultiplier() {
                return 27;
            }
        });
        ABSORPTION = REGISTRY.register(new AbilityTypePotionEffect("absorbtion", EnumRarity.RARE, 5, 75, MobEffects.ABSORPTION));
        SATURATION = REGISTRY.register(new AbilityTypePotionEffect("saturation", EnumRarity.RARE, 3, 30, MobEffects.SATURATION));
        LUCK = REGISTRY.register(new AbilityTypePotionEffect("luck", EnumRarity.RARE, 3, 40, MobEffects.LUCK));

        FLIGHT = REGISTRY.register(new AbilityTypeFlight("flight", EnumRarity.EPIC, 1, 100));
        STEP_ASSIST = REGISTRY.register(new AbilityTypeStepAssist("step_assist", EnumRarity.COMMON, 3, 25));
    }

}
