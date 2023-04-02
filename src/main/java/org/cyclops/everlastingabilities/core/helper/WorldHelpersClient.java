package org.cyclops.everlastingabilities.core.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author rubensworks
 */
public class WorldHelpersClient {

    @OnlyIn(Dist.CLIENT)
    public static Level getActiveLevel() {
        return Minecraft.getInstance().level;
    }

}
