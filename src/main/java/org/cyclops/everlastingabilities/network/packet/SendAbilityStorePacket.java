package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

/**
 * Packet from client to server to request an entity's ability store.
 * @author rubensworks
 *
 */
public class SendAbilityStorePacket extends PacketCodec {

	@CodecField
	private int entityId;
	@CodecField
	private CompoundNBT tag;

    public SendAbilityStorePacket() {

    }

	public SendAbilityStorePacket(int entityId, CompoundNBT tag) {
		this.entityId = entityId;
		this.tag = tag;
	}

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void actionClient(World world, PlayerEntity player) {
		try {
			if (world != null) {
				Entity entity = world.getEntityByID(entityId);
				if (entity != null) {
					entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null).ifPresent(abilityStore -> {
						MutableAbilityStoreConfig.CAPABILITY.readNBT(abilityStore, null, tag.get("contents"));
					});
				}
			}
		} catch (IllegalArgumentException e) {
			EverlastingAbilities.clog(Level.ERROR, e.getMessage());
		}
	}

	@Override
	public void actionServer(World world, ServerPlayerEntity player) {

	}
	
}