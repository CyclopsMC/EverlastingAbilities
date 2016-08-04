package org.cyclops.everlastingabilities.api.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.Map;

/**
 * Default implementation of {@link IAbilityStore} for storing abilities as a capability.
 * @author rubensworks
 */
public class DefaultAbilityStore implements IAbilityStore {

    protected final Map<IAbilityType, Integer> abilityTypes = Maps.newLinkedHashMap();

    public DefaultAbilityStore() {

    }

    public DefaultAbilityStore(DefaultMutableAbilityStore abilityStore) {
        setAbilities(abilityStore.abilityTypes);
    }

    @Override
    public void setAbilities(Map<IAbilityType, Integer> abilityTypes) {
        this.abilityTypes.clear();
        this.abilityTypes.putAll(abilityTypes);
    }

    @Override
    public boolean hasAbilityType(IAbilityType abilityType) {
        return abilityTypes.containsKey(abilityType);
    }

    @Override
    public Collection<IAbilityType> getAbilityTypes() {
        return abilityTypes.keySet();
    }

    @Override
    public Collection<Ability> getAbilities() {
        Collection<Ability> abilities = Lists.newArrayList();
        for (IAbilityType abilityType : getAbilityTypes()) {
            abilities.add(getAbility(abilityType));
        }
        return abilities;
    }

    @Override
    public Ability getAbility(IAbilityType abilityType) {
        if (!hasAbilityType(abilityType)) {
            return null;
        }
        return new Ability(abilityType, abilityTypes.get(abilityType));
    }
}
