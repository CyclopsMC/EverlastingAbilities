package org.cyclops.everlastingabilities.api;

import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * All ability types.
 * @author rubensworks
 */
public class AbilityTypes {

    public static final IAbilityTypeRegistry REGISTRY = EverlastingAbilities._instance.getRegistryManager().getRegistry(IAbilityTypeRegistry.class);

    public static IAbilityType SPEED = null; // TODO

    public static void load() {

    }

}
