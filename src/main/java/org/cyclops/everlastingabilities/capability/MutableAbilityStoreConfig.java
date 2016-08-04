package org.cyclops.everlastingabilities.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.everlastingabilities.api.capability.AbilityStoreStorage;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.core.config.extendedconfig.CapabilityConfig;

/**
 * Config for the worker capability.
 * @author rubensworks
 *
 */
public class MutableAbilityStoreConfig extends CapabilityConfig {

    /**
     * The unique instance.
     */
    public static MutableAbilityStoreConfig _instance;

    @CapabilityInject(IMutableAbilityStore.class)
    public static Capability<IMutableAbilityStore> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public MutableAbilityStoreConfig() {
        super(
                true,
                "mutableAbilityStore",
                "Mutable storage for abilities.",
                IMutableAbilityStore.class,
                new AbilityStoreStorage(),
                DefaultMutableAbilityStore.class
        );
    }
}
