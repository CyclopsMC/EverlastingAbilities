package org.cyclops.everlastingabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

/**
 * @author rubensworks
 */
public class CapabilitiesForge {

    public static Capability<IMutableAbilityStore> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

}
