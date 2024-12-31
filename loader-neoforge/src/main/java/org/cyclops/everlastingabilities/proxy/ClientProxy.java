package org.cyclops.everlastingabilities.proxy;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.cyclops.cyclopscore.init.ModBaseNeoForge;
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
    public ModBaseNeoForge<EverlastingAbilities> getMod() {
        return EverlastingAbilities._instance;
    }

    @SubscribeEvent
    public void onRenderLiving(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            RenderLivingHandler.onRenderLiving(livingEntity);
        }
    }

}
