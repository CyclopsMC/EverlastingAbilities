package org.cyclops.everlastingabilities.proxy;

import org.cyclops.cyclopscore.init.ModBase;
import org.cyclops.cyclopscore.network.PacketHandler;
import org.cyclops.cyclopscore.proxy.CommonProxyComponent;
import org.cyclops.everlastingabilities.EverlastingAbilities;
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
    public ModBase getMod() {
        return EverlastingAbilities._instance;
    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {
        super.registerPacketHandlers(packetHandler);

        packetHandler.register(MoveAbilityPacket.class, MoveAbilityPacket.TYPE, MoveAbilityPacket.CODEC);
        packetHandler.register(RequestAbilityStorePacket.class, RequestAbilityStorePacket.TYPE, RequestAbilityStorePacket.CODEC);
        packetHandler.register(SendAbilityStorePacket.class, SendAbilityStorePacket.TYPE, SendAbilityStorePacket.CODEC);
    }
}
