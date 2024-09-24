package org.cyclops.everlastingabilities.proxy;

import org.cyclops.cyclopscore.init.ModBaseForge;
import org.cyclops.cyclopscore.network.IPacketHandler;
import org.cyclops.cyclopscore.proxy.CommonProxyComponentForge;
import org.cyclops.everlastingabilities.EverlastingAbilitiesForge;
import org.cyclops.everlastingabilities.network.packet.MoveAbilityPacket;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.network.packet.SendAbilityStorePacket;

/**
 * Proxy for server and client side.
 * @author rubensworks
 *
 */
public class CommonProxyForge extends CommonProxyComponentForge {

    @Override
    public ModBaseForge<?> getMod() {
        return EverlastingAbilitiesForge._instance;
    }

    @Override
    public void registerPackets(IPacketHandler packetHandler) {
        super.registerPackets(packetHandler);

        packetHandler.register(MoveAbilityPacket.class, MoveAbilityPacket.TYPE, MoveAbilityPacket.CODEC);
        packetHandler.register(RequestAbilityStorePacket.class, RequestAbilityStorePacket.TYPE, RequestAbilityStorePacket.CODEC);
        packetHandler.register(SendAbilityStorePacket.class, SendAbilityStorePacket.TYPE, SendAbilityStorePacket.CODEC);
    }
}
