package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.EverlastingAbilities;
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
	public void actionClient(World world, PlayerEntity player) {

	}

	@Override
	public void actionServer(World world, ServerPlayerEntity player) {
		try {
			UUID uuid = UUID.fromString(entityUuid);
			Entity entity = ((ServerWorld) world).getEntityByUuid(uuid);
			if (entity != null) {
				entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
					INBT contents = MutableAbilityStoreConfig.CAPABILITY.writeNBT(abilityStore, null);
					CompoundNBT tag = new CompoundNBT();
					tag.put("contents", contents);
					EverlastingAbilities._instance.getPacketHandler().sendToPlayer(new SendAbilityStorePacket(entity.getEntityId(), tag), player);
				});
			}
		} catch (IllegalArgumentException e) {
			EverlastingAbilities.clog(Level.ERROR, e.getMessage());
		}
	}
	
}