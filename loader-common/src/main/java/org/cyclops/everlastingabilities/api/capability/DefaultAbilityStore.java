package org.cyclops.everlastingabilities.api.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Default implementation of {@link IAbilityStore} for storing abilities as a capability.
 * @author rubensworks
 */
public class DefaultAbilityStore implements IAbilityStore {

    protected final Map<Holder<IAbilityType>, Integer> abilityTypes = Maps.newLinkedHashMap();

    public DefaultAbilityStore() {

    }

    public DefaultAbilityStore(Map<Holder<IAbilityType>, Integer> abilityTypes) {
        setAbilities(abilityTypes);
    }

    public DefaultAbilityStore(IAbilityStore abilityStore) {
        this(abilityStore.getAbilitiesRaw());
    }

    @Override
    public void setAbilities(Map<Holder<IAbilityType>, Integer> abilityTypes) {
        this.abilityTypes.clear();
        this.abilityTypes.putAll(abilityTypes);
    }

    @Override
    public boolean hasAbilityType(Holder<IAbilityType> abilityType) {
        return abilityTypes.containsKey(abilityType);
    }

    @Override
    public Collection<Holder<IAbilityType>> getAbilityTypes() {
        return abilityTypes.keySet();
    }

    @Override
    public Collection<Ability> getAbilities() {
        Collection<Ability> abilities = Lists.newArrayList();
        for (Holder<IAbilityType> abilityType : getAbilityTypes()) {
            abilities.add(getAbility(abilityType));
        }
        return abilities;
    }

    @Override
    public Map<Holder<IAbilityType>, Integer> getAbilitiesRaw() {
        return Collections.unmodifiableMap(abilityTypes);
    }

    @Override
    public Ability getAbility(Holder<IAbilityType> abilityType) {
        if (!hasAbilityType(abilityType)) {
            return Ability.EMPTY;
        }
        return new Ability(abilityType, abilityTypes.get(abilityType));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DefaultAbilityStore defaultAbilityStore) {
            return this.abilityTypes.equals(defaultAbilityStore.abilityTypes);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.abilityTypes.hashCode();
    }
}
