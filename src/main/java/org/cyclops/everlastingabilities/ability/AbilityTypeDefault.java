package org.cyclops.everlastingabilities.ability;

import net.minecraft.item.Rarity;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.AbilityType;

/**
 * Internal default ability type.
 * @author rubensworks
 */
public class AbilityTypeDefault extends AbilityType {

    private static final String PREFIX = "ability.everlastingabilities.";

    public AbilityTypeDefault(String id, Rarity rarity, int maxLevel, int baseXpPerLevel,
                              boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn,
                              boolean obtainableOnCraft, boolean obtainableOnLoot) {
        super(PREFIX + id, PREFIX + id + ".info", rarity, maxLevel,
                baseXpPerLevel * GeneralConfig.abilityXpMultiplier, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

    public AbilityTypeDefault(String id, int rarity, int maxLevel, int baseXpPerLevel,
                              boolean obtainableOnPlayerSpawn, boolean obtainableOnMobSpawn,
                              boolean obtainableOnCraft, boolean obtainableOnLoot) {
        this(id, AbilityHelpers.getSafeRarity(rarity), maxLevel, baseXpPerLevel, obtainableOnPlayerSpawn, obtainableOnMobSpawn, obtainableOnCraft, obtainableOnLoot);
    }

}
