package org.cyclops.everlastingabilities.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.cyclopscore.config.extendedconfig.CapabilityConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;

/**
 * Config for the worker capability.
 * @author rubensworks
 *
 */
public class AbilityStoreConfig extends CapabilityConfig {

    /**
     * The unique instance.
     */
    public static AbilityStoreConfig _instance;

    public static Capability<IAbilityStore> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    /**
     * Make a new instance.
     */
    public AbilityStoreConfig() {
        super(EverlastingAbilities._instance,
                "abilityStore",
                IAbilityStore.class);
    }
}
