package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.capability.AbilityStoreCapabilityProvider;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.UUID;

/**
 * Packet from client to server to request an entity's ability store.
 * @author rubensworks
 *
 */
public class RequestAbilityStorePacket extends PacketCodec {

	@CodecField
	private String entityUuid;

    public RequestAbilityStorePacket() {

    }

	public RequestAbilityStorePacket(String entityUuid) {
		this.entityUuid = entityUuid;
	}

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void actionClient(Level world, Player player) {

	}

	@Override
	public void actionServer(Level world, ServerPlayer player) {
		try {
			UUID uuid = UUID.fromString(entityUuid);
			Entity entity = ((ServerLevel) world).getEntity(uuid);
			if (entity != null) {
				entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
					Tag contents = AbilityStoreCapabilityProvider.serializeNBTStatic(abilityStore);
					CompoundTag tag = new CompoundTag();
					tag.put("contents", contents);
					EverlastingAbilities._instance.getPacketHandler().sendToPlayer(new SendAbilityStorePacket(entity.getId(), tag), player);
				});
			}
		} catch (IllegalArgumentException e) {
			EverlastingAbilities.clog(org.apache.logging.log4j.Level.ERROR, e.getMessage());
		}
	}

}
