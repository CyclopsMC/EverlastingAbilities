package org.cyclops.everlastingabilities.api;

import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.init.IRegistry;

import java.util.Collection;
import java.util.List;

/**
 * Registry for {@link IAbilityType}.
 * @author rubensworks
 */
public interface IAbilityTypeRegistry extends IRegistry {

    public <A extends IAbilityType> A register(A abilityType);
    public IAbilityType getAbilityType(String unlocalizedName);
    public Collection<IAbilityType> getAbilityTypes();
    public List<IAbilityType> getAbilityTypes(EnumRarity rarity);

}
