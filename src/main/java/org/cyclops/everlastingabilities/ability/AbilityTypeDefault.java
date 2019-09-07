package org.cyclops.everlastingabilities.ability;

import net.minecraft.item.Rarity;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.AbilityType;

/**
 * Internal default ability type.
 * @author rubensworks
 */
public class AbilityTypeDefault extends AbilityType {

    private static final String PREFIX = "ability.abilities.everlastingabilities.";

    public AbilityTypeDefault(String id, Rarity rarity, int maxLevel, int baseXpPerLevel) {
        super(PREFIX + id + ".name", PREFIX + id + ".info", rarity, maxLevel, baseXpPerLevel * GeneralConfig.abilityXpMultiplier);
    }

    public AbilityTypeDefault(String id, int rarity, int maxLevel, int baseXpPerLevel) {
        this(id, AbilityHelpers.getSafeRarity(rarity), maxLevel, baseXpPerLevel);
    }

}
