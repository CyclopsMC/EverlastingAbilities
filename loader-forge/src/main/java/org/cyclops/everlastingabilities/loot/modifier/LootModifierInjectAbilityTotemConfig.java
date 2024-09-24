package org.cyclops.everlastingabilities.loot.modifier;

import org.cyclops.cyclopscore.config.extendedconfig.LootModifierConfigForge;
import org.cyclops.everlastingabilities.EverlastingAbilitiesForge;

/**
 * @author rubensworks
 */
public class LootModifierInjectAbilityTotemConfig extends LootModifierConfigForge<LootModifierInjectAbilityTotem> {
    public LootModifierInjectAbilityTotemConfig() {
        super(EverlastingAbilitiesForge._instance, "inject_ability_totem", (eConfig) -> LootModifierInjectAbilityTotem.CODEC.get());
    }
}
