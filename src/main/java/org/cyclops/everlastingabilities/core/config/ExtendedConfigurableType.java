package org.cyclops.everlastingabilities.core.config;

import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.everlastingabilities.core.config.configurabletypeaction.CapabilityAction;
import org.cyclops.everlastingabilities.core.config.extendedconfig.CapabilityConfig;

/**
 * The different types of {@link org.cyclops.cyclopscore.config.configurable.IConfigurable}.
 * @author rubensworks
 *
 */
public class ExtendedConfigurableType {
    /**
     * Degradation effect type.
     */
    public static final ConfigurableType CAPABILITY = new ConfigurableType(false, CapabilityConfig.class, new CapabilityAction(), "capability");
}
