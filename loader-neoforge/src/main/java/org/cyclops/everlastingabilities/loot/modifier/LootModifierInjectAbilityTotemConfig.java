package org.cyclops.everlastingabilities.loot.modifier;

import org.cyclops.cyclopscore.config.extendedconfig.LootModifierConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;

/**
 * @author rubensworks
 */
public class LootModifierInjectAbilityTotemConfig extends LootModifierConfig<LootModifierInjectAbilityTotem> {
    public LootModifierInjectAbilityTotemConfig() {
        super(EverlastingAbilities._instance, "inject_ability_totem", (eConfig) -> LootModifierInjectAbilityTotem.CODEC.get());
    }
}
