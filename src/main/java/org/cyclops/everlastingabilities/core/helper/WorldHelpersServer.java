package org.cyclops.everlastingabilities.core.helper;

import net.minecraft.core.RegistryAccess;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * @author rubensworks
 */
public class WorldHelpersServer {

    public static RegistryAccess getRegistryAccess() {
        return ServerLifecycleHooks.getCurrentServer().registryAccess();
    }

}
