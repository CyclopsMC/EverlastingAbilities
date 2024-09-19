package org.cyclops.everlastingabilities.core.config;

import org.cyclops.cyclopscore.config.ConfigurableTypeCommon;
import org.cyclops.everlastingabilities.core.config.configurabletypeaction.AbilitySerializerAction;
import org.cyclops.everlastingabilities.core.config.extendedconfig.AbilitySerializerConfig;

/**
 * Configurable types for abilities.
 * @author rubensworks
 */
public class ExtendedConfigurableType {
    public static final ConfigurableTypeCommon ABILITY_SERIALIZER = new ConfigurableTypeCommon(true, AbilitySerializerConfig.class, new AbilitySerializerAction<>(), "ability_serializer");
}
