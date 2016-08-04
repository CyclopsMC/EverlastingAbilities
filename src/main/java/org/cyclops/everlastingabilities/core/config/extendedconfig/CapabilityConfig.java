package org.cyclops.everlastingabilities.core.config.extendedconfig;

import net.minecraftforge.common.capabilities.Capability;
import org.cyclops.cyclopscore.config.ConfigurableType;
import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.core.config.ExtendedConfigurableType;

/**
 * Config for degradation effects.
 * TODO: port me to CyclopsCore
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class CapabilityConfig<T> extends ExtendedConfig<CapabilityConfig<T>> {

    private final Class<T> type;
    private final Capability.IStorage<T> storage;
    private final Class<? extends T> implementation;

    /**
     * Make a new instance.
     * @param enabled If this should is enabled.
     * @param namedId The unique name ID for the configurable.
     * @param comment The comment to add in the config file for this configurable.
     * @param type The capability type.
     * @param storage The default capability storage.
     * @param implementation The default capability implementation
     */
    public CapabilityConfig(boolean enabled, String namedId, String comment,
                            Class<T> type, Capability.IStorage<T> storage, Class<? extends T> implementation) {
        super(EverlastingAbilities._instance, enabled, namedId, comment, null);
        this.type = type;
        this.storage = storage;
        this.implementation = implementation;
    }
    
    @Override
	public String getUnlocalizedName() {
		return "capability." + getNamedId();
	}
    
    @Override
	public ConfigurableType getHolderType() {
		return ExtendedConfigurableType.CAPABILITY;
	}

    public Class<T> getType() {
        return type;
    }

    public Capability.IStorage<T> getStorage() {
        return storage;
    }

    public Class<? extends T> getImplementation() {
        return implementation;
    }
}
