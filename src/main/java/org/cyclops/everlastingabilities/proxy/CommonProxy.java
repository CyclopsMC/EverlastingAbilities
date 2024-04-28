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

        packetHandler.register(MoveAbilityPacket.ID, MoveAbilityPacket::new);
        packetHandler.register(RequestAbilityStorePacket.ID, RequestAbilityStorePacket::new);
        packetHandler.register(SendAbilityStorePacket.ID, SendAbilityStorePacket::new);
    }
}
