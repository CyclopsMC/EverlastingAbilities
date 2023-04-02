package org.cyclops.everlastingabilities.core.config;

import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.everlastingabilities.core.config.configurabletypeaction.AbilitySerializerAction;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * Configurable types for abilities.
 * @author rubensworks
 */
public class ExtendedConfigurableType {
    public static final ConfigurableType ABILITY_SERIALIZER = new ConfigurableType(true, AbilitySerializerConfig.class, new AbilitySerializerAction(), "ability_serializer");
}
