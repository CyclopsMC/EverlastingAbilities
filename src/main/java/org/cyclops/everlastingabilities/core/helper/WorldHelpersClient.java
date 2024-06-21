package org.cyclops.everlastingabilities.core.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * @author rubensworks
 */
public class WorldHelpersClient {

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static RegistryAccess getRegistryAccess() {
        if (Minecraft.getInstance().level == null) {
            return null;
        }
        return Minecraft.getInstance().level.registryAccess();
    }

}
