package org.cyclops.everlastingabilities.api;

import org.cyclops.cyclopscore.helper.L10NHelpers;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An ability instance.
 * @author rubensworks
 */
public class Ability {

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
        return String.format("[%s @ %s]", L10NHelpers.localize(abilityType.getUnlocalizedName()), level);
    }
}
