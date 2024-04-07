package org.cyclops.everlastingabilities.core.helper;

import net.minecraft.core.RegistryAccess;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

/**
 * Helpers for world related logic.
 * TODO: use CyclopsCore's version in >1.19.2
 * @author rubensworks
 *
 */
public class WorldHelpers {

    /**
     * @return The registry access client-side server-side.
     */
    @Nullable
    public static RegistryAccess getRegistryAccess() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            return ServerLifecycleHooks.getCurrentServer().registryAccess();
        }
        return DistExecutor.unsafeRunForDist(()->WorldHelpersClient::getRegistryAccess, ()->WorldHelpersServer::getRegistryAccess);
    }

}
