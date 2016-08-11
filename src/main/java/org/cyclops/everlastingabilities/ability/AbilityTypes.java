package org.cyclops.everlastingabilities.ability;

import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.IAbilityTypeRegistry;

/**
 * Ability types.
 * @author rubensworks
 */
public class AbilityTypes {

    public static final IAbilityTypeRegistry REGISTRY = EverlastingAbilities._instance.getRegistryManager().getRegistry(IAbilityTypeRegistry.class);

}
