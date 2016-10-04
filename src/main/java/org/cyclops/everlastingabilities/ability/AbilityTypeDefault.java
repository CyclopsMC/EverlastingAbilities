package org.cyclops.everlastingabilities.ability;

import net.minecraft.item.EnumRarity;
import org.cyclops.cyclopscore.config.configurable.IConfigurable;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.AbilityType;

/**
 * Internal default ability type.
 * @author rubensworks
 */
public class AbilityTypeDefault extends AbilityType implements IConfigurable {

    private static final String PREFIX = "ability.abilities.everlastingabilities.";

    public AbilityTypeDefault(String id, EnumRarity rarity, int maxLevel, int baseXpPerLevel) {
        super(PREFIX + id + ".name", PREFIX + id + ".info", rarity, maxLevel, baseXpPerLevel * GeneralConfig.abilityXpMultiplier);
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return null;
    }
}
