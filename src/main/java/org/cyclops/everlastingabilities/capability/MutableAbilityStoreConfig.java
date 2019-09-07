package org.cyclops.everlastingabilities.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.capability.AbilityStoreStorage;
import org.cyclops.everlastingabilities.api.capability.DefaultMutableAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

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
        super(EverlastingAbilities._instance,
                "mutableAbilityStore",
                IMutableAbilityStore.class,
                new AbilityStoreStorage(),
                DefaultMutableAbilityStore::new);
    }
}
