package org.cyclops.everlastingabilities.api;

import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An ability instance.
 * @author rubensworks
 */
public class Ability implements Comparable<Ability> {

    public static final Ability EMPTY = new Ability(new AbilityType("", "", () -> Rarity.COMMON, () -> 0, () -> 0, () -> true, () -> true, () -> true, () -> true), 0);

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
        return String.format("[%s @ %s]", abilityType.getTranslationKey(), level);
    }

    @Override
    public int compareTo(Ability other) {
        return this.toString().compareTo(other.toString());
    }

    public ITextComponent getTextComponent() {
        return new StringTextComponent("[")
                .append(new TranslationTextComponent(abilityType.getTranslationKey()))
                .append(" @ " + level + "]");
    }

    public boolean isEmpty() {
        return getLevel() <= 0;
    }

}
