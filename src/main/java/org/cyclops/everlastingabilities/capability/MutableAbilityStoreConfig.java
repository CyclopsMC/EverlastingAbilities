package org.cyclops.everlastingabilities.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
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

    public static Capability<IMutableAbilityStore> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    /**
     * Make a new instance.
     */
    public MutableAbilityStoreConfig() {
        super(EverlastingAbilities._instance,
                "mutableAbilityStore",
                IMutableAbilityStore.class);
    }
}
