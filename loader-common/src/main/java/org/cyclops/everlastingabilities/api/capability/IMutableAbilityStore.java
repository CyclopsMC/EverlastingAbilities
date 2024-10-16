package org.cyclops.everlastingabilities.api.capability;

import org.cyclops.everlastingabilities.api.Ability;

/**
 * Extension of the {@link IAbilityStore} that allows insertion and deletion of abilities.
 * @author rubensworks
 */
public interface IMutableAbilityStore extends IAbilityStore {

    /**
     * Add the given ability.
     * @param ability The ability.
     * @param doAdd If the addition should actually be done.
     * @return The ability part that was added.
     */
    public Ability addAbility(Ability ability, boolean doAdd);

    /**
     * Remove the given ability.
     * @param ability The ability.
     * @param doRemove If the removal should actually be done.
     * @return The ability part that was removed.
     */
    public Ability removeAbility(Ability ability, boolean doRemove);

}
