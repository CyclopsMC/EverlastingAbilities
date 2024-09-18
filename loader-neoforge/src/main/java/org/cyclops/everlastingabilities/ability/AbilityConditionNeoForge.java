package org.cyclops.everlastingabilities.ability;

import net.neoforged.neoforge.common.conditions.ICondition;
import org.cyclops.everlastingabilities.api.IAbilityCondition;

/**
 * @author rubensworks
 */
public class AbilityConditionNeoForge implements IAbilityCondition {

    private final ICondition condition;

    public AbilityConditionNeoForge(ICondition condition) {
        this.condition = condition;
    }

    public ICondition getCondition() {
        return condition;
    }
}
