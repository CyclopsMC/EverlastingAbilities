package org.cyclops.everlastingabilities.ability;

import net.minecraftforge.common.crafting.conditions.ICondition;
import org.cyclops.everlastingabilities.api.IAbilityCondition;

/**
 * @author rubensworks
 */
public class AbilityConditionForge implements IAbilityCondition {

    private final ICondition condition;

    public AbilityConditionForge(ICondition condition) {
        this.condition = condition;
    }

    public ICondition getCondition() {
        return condition;
    }
}
