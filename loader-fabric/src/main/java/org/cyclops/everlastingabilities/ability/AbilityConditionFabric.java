package org.cyclops.everlastingabilities.ability;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import org.cyclops.everlastingabilities.api.IAbilityCondition;

/**
 * @author rubensworks
 */
public class AbilityConditionFabric implements IAbilityCondition {

    private final ResourceCondition condition;

    public AbilityConditionFabric(ResourceCondition condition) {
        this.condition = condition;
    }

    public ResourceCondition getCondition() {
        return condition;
    }
}
