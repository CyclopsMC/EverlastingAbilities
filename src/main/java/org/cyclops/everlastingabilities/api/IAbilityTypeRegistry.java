package org.cyclops.everlastingabilities.api;

import org.cyclops.cyclopscore.init.IRegistry;

/**
 * Registry for {@link IAbilityType}.
 * @author rubensworks
 */
public interface IAbilityTypeRegistry extends IRegistry {

    public <A extends IAbilityType> A register(A abilityType);
    public IAbilityType getAbilityType(String unlocalizedName);

}
