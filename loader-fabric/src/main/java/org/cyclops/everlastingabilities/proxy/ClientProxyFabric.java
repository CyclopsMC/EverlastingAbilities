package org.cyclops.everlastingabilities.proxy;

import org.cyclops.cyclopscore.events.ILivingEntityRendererEvent;
import org.cyclops.cyclopscore.init.ModBaseFabric;
import org.cyclops.cyclopscore.proxy.ClientProxyComponentFabric;
import org.cyclops.everlastingabilities.EverlastingAbilitiesFabric;
import org.cyclops.everlastingabilities.client.gui.RenderLivingHandler;

/**
 * Proxy for the client side.
 *
 * @author rubensworks
 *
 */
public class ClientProxyFabric extends ClientProxyComponentFabric {

    public ClientProxyFabric() {
        super(new CommonProxyFabric());
    }

    @Override
    public ModBaseFabric<?> getMod() {
        return EverlastingAbilitiesFabric._instance;
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();
        ILivingEntityRendererEvent.EVENT.register((livingEntity, livingEntityRenderer, v, poseStack, multiBufferSource, i) -> RenderLivingHandler.onRenderLiving(livingEntity));
    }
}
