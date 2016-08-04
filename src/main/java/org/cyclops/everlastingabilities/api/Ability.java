package org.cyclops.everlastingabilities.api;

import org.cyclops.cyclopscore.helper.L10NHelpers;

/**
 * An ability instance.
 * @author rubensworks
 */
public class Ability {

    private final IAbilityType abilityType;
    private final int level;

    public Ability(IAbilityType abilityType, int level) {
        this.abilityType = abilityType;
        this.level = level;
    }

    public IAbilityType getAbilityType() {
        return abilityType;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format("[%s @ %s]", L10NHelpers.localize(abilityType.getUnlocalizedName()), level);
    }
}
