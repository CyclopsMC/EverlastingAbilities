package org.cyclops.everlastingabilities.api;

import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.core.DefaultAbilityType;

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
        SPEED = REGISTRY.register(new DefaultAbilityType("speed", EnumRarity.COMMON, 3, 10));
        HASTE = REGISTRY.register(new DefaultAbilityType("haste", EnumRarity.COMMON, 3, 10));
        STRENGTH = REGISTRY.register(new DefaultAbilityType("strength", EnumRarity.UNCOMMON, 3, 10));
        JUMP_BOOST = REGISTRY.register(new DefaultAbilityType("jump_boost", EnumRarity.COMMON, 3, 10));
        REGENERATION = REGISTRY.register(new DefaultAbilityType("regeneration", EnumRarity.RARE, 3, 10));
        RESISTANCE = REGISTRY.register(new DefaultAbilityType("resistance", EnumRarity.RARE, 3, 10));
        FIRE_RESISTANCE = REGISTRY.register(new DefaultAbilityType("fire_resistance", EnumRarity.UNCOMMON, 3, 10));
    }

}
