package org.cyclops.everlastingabilities.proxy;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.cyclops.cyclopscore.init.ModBaseForge;
import org.cyclops.cyclopscore.proxy.ClientProxyComponentForge;
import org.cyclops.everlastingabilities.EverlastingAbilitiesForge;
import org.cyclops.everlastingabilities.client.gui.RenderLivingHandler;

/**
 * Proxy for the client side.
 *
 * @author rubensworks
 *
 */
public class ClientProxyForge extends ClientProxyComponentForge {

    public ClientProxyForge() {
        super(new CommonProxyForge());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public ModBaseForge<?> getMod() {
        return EverlastingAbilitiesForge._instance;
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post event) {
        RenderLivingHandler.onRenderLiving(event.getEntity());
    }

}
