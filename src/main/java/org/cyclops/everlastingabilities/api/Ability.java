package org.cyclops.everlastingabilities.api;

import org.cyclops.cyclopscore.helper.L10NHelpers;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.lang.Comparable;

/**
 * An ability instance.
 * @author rubensworks
 */
public class Ability implements Comparable<Ability> {

    private final IAbilityType abilityType;
    private final int level;

    public Ability(@Nonnull IAbilityType abilityType, int level) {
        this.abilityType = Objects.requireNonNull(abilityType);
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
        return String.format("[%s @ %s]",abilityType.getTranslationKey(), level);
    }

    @Override
    public int compareTo(Ability other) {
        return this.toString().compareTo(other.toString());
    }
}
