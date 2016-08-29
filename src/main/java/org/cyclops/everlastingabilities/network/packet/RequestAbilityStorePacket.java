package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
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
	@SideOnly(Side.CLIENT)
	public void actionClient(World world, EntityPlayer player) {

	}

	@Override
	public void actionServer(World world, EntityPlayerMP player) {
		try {
			UUID uuid = UUID.fromString(entityUuid);
			Entity entity = ((WorldServer) world).getEntityFromUuid(uuid);
			if (entity != null && entity.hasCapability(MutableAbilityStoreConfig.CAPABILITY, null)) {
				IMutableAbilityStore abilityStore = entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
				NBTBase contents = MutableAbilityStoreConfig.CAPABILITY.writeNBT(abilityStore, null);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("contents", contents);
				EverlastingAbilities._instance.getPacketHandler().sendToPlayer(new SendAbilityStorePacket(entity.getEntityId(), tag), player);
			}
		} catch (IllegalArgumentException e) {
			EverlastingAbilities.clog(Level.ERROR, e.getMessage());
		}
	}
	
}