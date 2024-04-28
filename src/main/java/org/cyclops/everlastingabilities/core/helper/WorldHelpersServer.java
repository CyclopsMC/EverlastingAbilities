package org.cyclops.everlastingabilities.core.helper;

import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * @author rubensworks
 */
public class WorldHelpersServer {

    public static RegistryAccess getRegistryAccess() {
        return ServerLifecycleHooks.getCurrentServer().registryAccess();
    }

}
