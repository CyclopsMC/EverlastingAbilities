package org.cyclops.everlastingabilities.proxy;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.client.gui.RenderLivingHandler;

/**
 * Proxy for the client side.
 *
 * @author rubensworks
 *
 */
public class ClientProxy extends ClientProxyComponent {

    public ClientProxy() {
        super(new CommonProxy());
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public ModBase getMod() {
        return EverlastingAbilities._instance;
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Post event) {
        RenderLivingHandler.onRenderLiving(event.getEntity());
    }

}
