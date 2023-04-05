package org.cyclops.everlastingabilities.core.helper;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.DistExecutor;

/**
 * Helpers for world related logic.
 * TODO: use CyclopsCore's version in >1.19.2
 * @author rubensworks
 *
 */
public class WorldHelpers {

    /**
     * @return The current level client-side, or the overworld server-side.
     */
    public static Level getActiveLevel() {
        return DistExecutor.unsafeRunForDist(()->WorldHelpersClient::getActiveLevel, ()->WorldHelpersServer::getActiveLevel);
    }

}
