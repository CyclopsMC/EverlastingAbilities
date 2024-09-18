package org.cyclops.everlastingabilities.api.capability;

import com.google.common.collect.Maps;
import net.minecraft.core.Holder;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Default implementation of {@link IAbilityStore} for storing abilities as a capability.
 * @author rubensworks
 */
public class DefaultMutableAbilityStore extends DefaultAbilityStore implements IMutableAbilityStore {

    public DefaultMutableAbilityStore() {

    }

    public DefaultMutableAbilityStore(Map<Holder<IAbilityType>, Integer> abilityTypes) {
        setAbilities(abilityTypes);
    }

    public DefaultMutableAbilityStore(@Nullable IAbilityStore abilityStore) {
        this(abilityStore == null ? Maps.newHashMap() : abilityStore.getAbilitiesRaw());
    }

    @Override
    public Ability addAbility(Ability ability, boolean doAdd) {
        Holder<IAbilityType> abilityType = ability.getAbilityTypeHolder();
        int currentLevel = abilityTypes.containsKey(abilityType) ? abilityTypes.get(abilityType) : 0;
        int maxLevel = abilityType.value().getMaxLevelInfinitySafe();
        int toAddLevel = ability.getLevel();
        int finalLevel = Math.min(currentLevel + toAddLevel, maxLevel);
        int addedLevel = finalLevel - currentLevel;
        Ability newAbility = new Ability(abilityType, finalLevel);
        Ability addedAbility = addedLevel == 0 ? Ability.EMPTY : new Ability(abilityType, addedLevel);

        if (doAdd) {
            if (newAbility.getLevel() == 0) {
                abilityTypes.remove(newAbility.getAbilityTypeHolder());
            } else {
                abilityTypes.put(newAbility.getAbilityTypeHolder(), newAbility.getLevel());
            }
        }
        return addedAbility;
    }

    @Override
    public Ability removeAbility(Ability ability, boolean doRemove) {
        Holder<IAbilityType> abilityType = ability.getAbilityTypeHolder();
        int currentLevel = abilityTypes.containsKey(abilityType) ? abilityTypes.get(abilityType) : 0;
        int toRemoveLevel = ability.getLevel();
        int finalLevel = Math.max(currentLevel - toRemoveLevel, 0);
        int removedLevel = currentLevel - finalLevel;
        Ability newAbility = new Ability(abilityType, finalLevel);
        Ability removedAbility = removedLevel == 0 ? Ability.EMPTY : new Ability(abilityType, removedLevel);

        if (doRemove) {
            if (newAbility.getLevel() == 0) {
                abilityTypes.remove(newAbility.getAbilityTypeHolder());
            } else {
                abilityTypes.put(newAbility.getAbilityTypeHolder(), newAbility.getLevel());
            }
        }
        return removedAbility;
    }
}
