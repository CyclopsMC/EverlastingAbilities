package org.cyclops.everlastingabilities.core.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author rubensworks
 */
public class WorldHelpersClient {

    @OnlyIn(Dist.CLIENT)
    public static RegistryAccess getRegistryAccess() {
        return Minecraft.getInstance().level.registryAccess();
    }

}
