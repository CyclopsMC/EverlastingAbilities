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

    public static IAbilityType SPEED = null; // TODO

    public static void load() {
        SPEED = new DefaultAbilityType("speed", EnumRarity.COMMON, 3, 10);
    }

}
