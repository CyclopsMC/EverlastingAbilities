package org.cyclops.everlastingabilities.api;

import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.core.AbilityTypePotionEffect;

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

    public static void load() {
        SPEED = REGISTRY.register(new AbilityTypePotionEffect("speed", EnumRarity.COMMON, 3, 10, MobEffects.SPEED));
        HASTE = REGISTRY.register(new AbilityTypePotionEffect("haste", EnumRarity.COMMON, 3, 10, MobEffects.HASTE));
        STRENGTH = REGISTRY.register(new AbilityTypePotionEffect("strength", EnumRarity.UNCOMMON, 3, 10, MobEffects.STRENGTH));
        JUMP_BOOST = REGISTRY.register(new AbilityTypePotionEffect("jump_boost", EnumRarity.COMMON, 3, 10, MobEffects.JUMP_BOOST));
        REGENERATION = REGISTRY.register(new AbilityTypePotionEffect("regeneration", EnumRarity.RARE, 3, 10, MobEffects.REGENERATION));
        RESISTANCE = REGISTRY.register(new AbilityTypePotionEffect("resistance", EnumRarity.RARE, 3, 10, MobEffects.RESISTANCE));
        FIRE_RESISTANCE = REGISTRY.register(new AbilityTypePotionEffect("fire_resistance", EnumRarity.UNCOMMON, 3, 10, MobEffects.FIRE_RESISTANCE));
    }

}
