package org.cyclops.everlastingabilities.proxy;

import org.cyclops.cyclopscore.init.ModBaseFabric;
import org.cyclops.cyclopscore.network.IPacketHandler;
import org.cyclops.cyclopscore.proxy.CommonProxyComponentFabric;
import org.cyclops.everlastingabilities.EverlastingAbilitiesFabric;
import org.cyclops.everlastingabilities.network.packet.MoveAbilityPacket;
import org.cyclops.everlastingabilities.network.packet.RequestAbilityStorePacket;
import org.cyclops.everlastingabilities.network.packet.SendAbilityStorePacket;

/**
 * Proxy for server and client side.
 * @author rubensworks
 *
 */
public class CommonProxyFabric extends CommonProxyComponentFabric {

    @Override
    public ModBaseFabric<?> getMod() {
        return EverlastingAbilitiesFabric._instance;
    }

    @Override
    public void registerPackets(IPacketHandler packetHandler) {
        super.registerPackets(packetHandler);

        packetHandler.register(MoveAbilityPacket.class, MoveAbilityPacket.TYPE, MoveAbilityPacket.CODEC);
        packetHandler.register(RequestAbilityStorePacket.class, RequestAbilityStorePacket.TYPE, RequestAbilityStorePacket.CODEC);
        packetHandler.register(SendAbilityStorePacket.class, SendAbilityStorePacket.TYPE, SendAbilityStorePacket.CODEC);
    }
}
