package org.cyclops.everlastingabilities.api.capability;

import lombok.NonNull;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;

import java.util.Collection;
import java.util.Map;

/**
 * Capability type for storing capabilities.
 * @author rubensworks
 */
public interface IAbilityStore {

    public void setAbilities(Map<Holder<IAbilityType>, Integer> abilityTypes);
    public boolean hasAbilityType(Holder<IAbilityType> abilityType);
    public Collection<Holder<IAbilityType>> getAbilityTypes();
    public Collection<Ability> getAbilities();
    public Map<Holder<IAbilityType>, Integer> getAbilitiesRaw();
    @NonNull
    public Ability getAbility(Holder<IAbilityType> abilityType);

    public default Component getTextComponent() {
        return ComponentUtils.formatAndSortList(getAbilities(), Ability::getTextComponent);
    }
}
