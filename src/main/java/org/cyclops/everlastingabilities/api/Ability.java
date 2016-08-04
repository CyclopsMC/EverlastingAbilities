package org.cyclops.everlastingabilities.api;

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

}
