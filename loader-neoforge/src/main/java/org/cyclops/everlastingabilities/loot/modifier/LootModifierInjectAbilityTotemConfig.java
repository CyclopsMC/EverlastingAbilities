package org.cyclops.everlastingabilities.loot.modifier;

import org.cyclops.cyclopscore.config.extendedconfig.LootModifierConfigNeoForge;
import org.cyclops.everlastingabilities.EverlastingAbilitiesNeoForge;

/**
 * @author rubensworks
 */
public class LootModifierInjectAbilityTotemConfig extends LootModifierConfigNeoForge<LootModifierInjectAbilityTotem> {
    public LootModifierInjectAbilityTotemConfig() {
        super(EverlastingAbilitiesNeoForge._instance, "inject_ability_totem", (eConfig) -> LootModifierInjectAbilityTotem.CODEC.get());
    }
}
