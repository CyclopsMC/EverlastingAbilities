package org.cyclops.everlastingabilities.api;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * An ability instance.
 * @author rubensworks
 */
public class Ability implements Comparable<Ability> {

    public static final Ability EMPTY = new Ability(new AbilityTypeAdapter("", Rarity.COMMON, 0, 0, true, true, true, true) {
        @Override
        public Codec<? extends IAbilityType> codec() {
            return null;
        }
    }, 0);

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

    public Component getTextComponent() {
        return Component.literal("[")
                .append(Component.translatable(abilityType.getTranslationKey()))
                .append(" @ " + level + "]");
    }

    public boolean isEmpty() {
        return getLevel() <= 0;
    }

}
