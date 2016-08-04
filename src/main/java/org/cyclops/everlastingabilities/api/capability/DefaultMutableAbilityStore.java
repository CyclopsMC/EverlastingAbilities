package org.cyclops.everlastingabilities.api.capability;

import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

/**
 * Default implementation of {@link IAbilityStore} for storing abilities as a capability.
 * @author rubensworks
 */
public class DefaultMutableAbilityStore extends DefaultAbilityStore implements IMutableAbilityStore {

    public DefaultMutableAbilityStore() {

    }

    public DefaultMutableAbilityStore(DefaultAbilityStore abilityStore) {
        setAbilities(abilityStore.abilityTypes);
    }

    @Override
    public Ability addAbility(Ability ability, boolean doAdd) {
        IAbilityType abilityType = ability.getAbilityType();
        int currentLevel = abilityTypes.containsKey(abilityType) ? abilityTypes.get(abilityType) : 0;
        int maxLevel = abilityType.getMaxLevel();
        int toAddLevel = ability.getLevel();
        int finalLevel = Math.min(currentLevel + toAddLevel, maxLevel);
        Ability newAbility = new Ability(abilityType, finalLevel);

        if (doAdd) {
            abilityTypes.put(newAbility.getAbilityType(), newAbility.getLevel());
        }
        return newAbility;
    }

    @Override
    public Ability removeAbility(Ability ability, boolean doRemove) {
        IAbilityType abilityType = ability.getAbilityType();
        int currentLevel = abilityTypes.containsKey(abilityType) ? abilityTypes.get(abilityType) : 0;
        int toRemoveLevel = ability.getLevel();
        int finalLevel = Math.max(currentLevel - toRemoveLevel, 0);
        Ability newAbility = new Ability(abilityType, finalLevel);

        if (doRemove) {
            abilityTypes.put(newAbility.getAbilityType(), newAbility.getLevel());
        }
        return newAbility;
    }
}
