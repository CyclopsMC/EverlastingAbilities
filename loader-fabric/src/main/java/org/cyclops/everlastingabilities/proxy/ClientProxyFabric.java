package org.cyclops.everlastingabilities.proxy;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.cyclops.cyclopscore.events.IEntityTickEvent;
import org.cyclops.cyclopscore.init.ModBaseFabric;
import org.cyclops.cyclopscore.proxy.ClientProxyComponentFabric;
import org.cyclops.everlastingabilities.EverlastingAbilitiesFabric;
import org.cyclops.everlastingabilities.client.gui.GuiItemRenderer;
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
        IEntityTickEvent.EVENT.register((Entity entity) -> {
            if (entity instanceof LivingEntity livingEntity) {
                RenderLivingHandler.onRenderLiving(livingEntity);
            }
        });
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            GuiRenderer guiRenderer = Minecraft.getInstance().gameRenderer.guiRenderer;
            GuiItemRenderer guiItemRenderer = new GuiItemRenderer(guiRenderer.bufferSource);
            guiRenderer.pictureInPictureRenderers.put(
                    guiItemRenderer.getRenderStateClass(),
                    guiItemRenderer
            );
        });
    }
}
