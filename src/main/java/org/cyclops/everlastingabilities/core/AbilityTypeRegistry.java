package org.cyclops.everlastingabilities.core;

import com.google.common.collect.Maps;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.IAbilityTypeRegistry;

import java.util.Map;

/**
 * Default ability type registry.
 * @author rubensworks
 */
public class AbilityTypeRegistry implements IAbilityTypeRegistry {

    private static final AbilityTypeRegistry INSTANCE = new AbilityTypeRegistry();

    private final Map<String, IAbilityType> abilities = Maps.newHashMap();

    private AbilityTypeRegistry() {

    }

    public static AbilityTypeRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <A extends IAbilityType> A register(A abilityType) {
        abilities.put(abilityType.getUnlocalizedName(), abilityType);
        return abilityType;
    }

    @Override
    public IAbilityType getAbilityType(String unlocalizedName) {
        return abilities.get(unlocalizedName);
    }
}
