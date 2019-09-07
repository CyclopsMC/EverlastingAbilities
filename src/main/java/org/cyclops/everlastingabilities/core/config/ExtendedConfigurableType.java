package org.cyclops.everlastingabilities.core.config;

import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.everlastingabilities.core.config.configurabletypeaction.AbilityAction;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * Configurable types for abilities.
 * @author rubensworks
 */
public class ExtendedConfigurableType {
    /**
     * Degradation effect type.
     */
    public static final ConfigurableType ABILITY = new ConfigurableType(true, AbilityConfig.class, new AbilityAction(), "ability");
}
