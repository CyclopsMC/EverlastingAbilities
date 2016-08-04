package org.cyclops.everlastingabilities.core;

import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.api.AbilityType;

/**
 * Internal default ability type.
 * @author rubensworks
 */
public class DefaultAbilityType extends AbilityType {

    private static final String PREFIX = "ability.abilities.everlastingabilities.";

    public DefaultAbilityType(String id, EnumRarity rarity, int maxLevel, int baseXpPerLevel) {
        super(PREFIX + id + ".name", PREFIX + id + ".info", rarity, maxLevel, baseXpPerLevel);
    }
}
