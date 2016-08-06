package org.cyclops.everlastingabilities.api.capability;

import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.Map;

/**
 * Capability type for storing capabilities.
 * @author rubensworks
 */
public interface IAbilityStore {

    public void setAbilities(Map<IAbilityType, Integer> abilityTypes);
    public boolean hasAbilityType(IAbilityType abilityType);
    public Collection<IAbilityType> getAbilityTypes();
    public Collection<Ability> getAbilities();
    public Map<IAbilityType, Integer> getAbilitiesRaw();
    public Ability getAbility(IAbilityType abilityType);

}
