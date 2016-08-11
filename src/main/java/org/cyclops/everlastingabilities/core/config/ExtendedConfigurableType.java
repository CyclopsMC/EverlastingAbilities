package org.cyclops.everlastingabilities.core.config;

import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.everlastingabilities.core.config.configurabletypeaction.AbilityAction;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilityConfig;

/**
 * The different types of {@link org.cyclops.cyclopscore.config.configurable.IConfigurable}.
 * @author rubensworks
 *
 */
public class ExtendedConfigurableType {
    /**
     * Degradation effect type.
     */
    public static final ConfigurableType ABILITY = new ConfigurableType(false, AbilityConfig.class, new AbilityAction(), "ability");
}
