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
        int maxLevel = abilityType.getMaxLevelInfinitySafe();
        int toAddLevel = ability.getLevel();
        int finalLevel = Math.min(currentLevel + toAddLevel, maxLevel);
        int addedLevel = finalLevel - currentLevel;
        Ability newAbility = new Ability(abilityType, finalLevel);
        Ability addedAbility = addedLevel == 0 ? Ability.EMPTY : new Ability(abilityType, addedLevel);

        if (doAdd) {
            if (newAbility.getLevel() == 0) {
                abilityTypes.remove(newAbility.getAbilityType());
            } else {
                abilityTypes.put(newAbility.getAbilityType(), newAbility.getLevel());
            }
        }
        return addedAbility;
    }

    @Override
    public Ability removeAbility(Ability ability, boolean doRemove) {
        IAbilityType abilityType = ability.getAbilityType();
        int currentLevel = abilityTypes.containsKey(abilityType) ? abilityTypes.get(abilityType) : 0;
        int toRemoveLevel = ability.getLevel();
        int finalLevel = Math.max(currentLevel - toRemoveLevel, 0);
        int removedLevel = currentLevel - finalLevel;
        Ability newAbility = new Ability(abilityType, finalLevel);
        Ability removedAbility = removedLevel == 0 ? Ability.EMPTY : new Ability(abilityType, removedLevel);

        if (doRemove) {
            if (newAbility.getLevel() == 0) {
                abilityTypes.remove(newAbility.getAbilityType());
            } else {
                abilityTypes.put(newAbility.getAbilityType(), newAbility.getLevel());
            }
        }
        return removedAbility;
    }
}
