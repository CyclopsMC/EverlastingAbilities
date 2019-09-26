package org.cyclops.everlastingabilities.api.capability;

import lombok.NonNull;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
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
    @NonNull
    public Ability getAbility(IAbilityType abilityType);

    public default ITextComponent getTextComponent() {
        return TextComponentUtils.makeList(getAbilities(), Ability::getTextComponent);
    }
}
