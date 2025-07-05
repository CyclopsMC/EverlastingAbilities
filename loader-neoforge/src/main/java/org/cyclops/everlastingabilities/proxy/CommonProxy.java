package org.cyclops.everlastingabilities.proxy;

import org.cyclops.cyclopscore.init.ModBaseNeoForge;
import org.cyclops.cyclopscore.network.IPacketHandler;
import org.cyclops.cyclopscore.proxy.CommonProxyComponent;
import org.cyclops.everlastingabilities.EverlastingAbilitiesNeoForge;
import org.cyclops.everlastingabilities.network.packet.MoveAbilityPacket;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.network.packet.SendAbilityStorePacket;

/**
 * Proxy for server and client side.
 * @author rubensworks
 *
 */
public class CommonProxy extends CommonProxyComponent {

    @Override
    public ModBaseNeoForge getMod() {
        return EverlastingAbilitiesNeoForge._instance;
    }

    @Override
    public void registerPackets(IPacketHandler packetHandler) {
        super.registerPackets(packetHandler);

        packetHandler.register(MoveAbilityPacket.class, MoveAbilityPacket.TYPE, MoveAbilityPacket.CODEC);
        packetHandler.register(RequestAbilityStorePacket.class, RequestAbilityStorePacket.TYPE, RequestAbilityStorePacket.CODEC);
        packetHandler.register(SendAbilityStorePacket.class, SendAbilityStorePacket.TYPE, SendAbilityStorePacket.CODEC);
    }
}
