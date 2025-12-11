package org.cyclops.everlastingabilities.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.cyclops.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.cyclopscore.proxy.ClientProxyComponent;
import org.cyclops.everlastingabilities.EverlastingAbilitiesNeoForge;
import org.cyclops.everlastingabilities.client.gui.GuiItemRenderState;
import org.cyclops.everlastingabilities.client.gui.GuiItemRenderer;
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
        getMod().getModEventBus().addListener(this::onRegisterPictureInPictureRenderers);
    }

    @Override
    public ModBaseNeoForge<EverlastingAbilitiesNeoForge> getMod() {
        return EverlastingAbilitiesNeoForge._instance;
    }

    @SubscribeEvent
    public void onRenderLiving(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            RenderLivingHandler.onRenderLiving(livingEntity);
        }
    }

    public void onRegisterPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event) {
        event.register(GuiItemRenderState.class, buffers -> new GuiItemRenderer(buffers, Minecraft.getInstance().getEntityRenderDispatcher()));
    }

}
