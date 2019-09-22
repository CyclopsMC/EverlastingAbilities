package org.cyclops.everlastingabilities.ability;

import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.AbilityType;

import java.util.function.Supplier;

/**
 * Internal default ability type.
 * @author rubensworks
 */
public class AbilityTypeDefault extends AbilityType {

    private static final String PREFIX = "ability.everlastingabilities.";

    public AbilityTypeDefault(String id, Supplier<Integer> rarity, Supplier<Integer> maxLevel,
                              Supplier<Integer> baseXpPerLevel, Supplier<Boolean> obtainableOnPlayerSpawn, Supplier<Boolean> obtainableOnMobSpawn,
                              Supplier<Boolean> obtainableOnCraft, Supplier<Boolean> obtainableOnLoot) {
        super(PREFIX + id, PREFIX + id + ".info", AbilityHelpers.getSafeRarity(rarity), maxLevel,
                () -> baseXpPerLevel.get() * GeneralConfig.abilityXpMultiplier, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

}
