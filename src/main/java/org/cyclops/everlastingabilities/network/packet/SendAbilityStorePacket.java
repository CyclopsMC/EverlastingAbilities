package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

/**
 * Packet from client to server to request an entity's ability store.
 * Additionally, {@link GeneralConfig#maxPlayerAbilities} will be synced.
 * @author rubensworks
 *
 */
public class SendAbilityStorePacket extends PacketCodec {

	@CodecField
	private int entityId;
	@CodecField
	private NBTTagCompound tag;
	@CodecField
	private int maxPlayerAbilities;

    public SendAbilityStorePacket() {

    }

	public SendAbilityStorePacket(int entityId, NBTTagCompound tag) {
		this.entityId = entityId;
		this.tag = tag;
		this.maxPlayerAbilities = GeneralConfig.maxPlayerAbilities;
	}

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void actionClient(World world, EntityPlayer player) {
		try {
			if (world != null) {
				Entity entity = world.getEntityByID(entityId);
				if (entity != null && entity.hasCapability(MutableAbilityStoreConfig.CAPABILITY, null)) {
					// Sync ability store
					IMutableAbilityStore abilityStore = entity.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
					MutableAbilityStoreConfig.CAPABILITY.readNBT(abilityStore, null, tag.getTag("contents"));

					// Sync max abilities value
					AbilityHelpers.maxPlayerAbilitiesClient = this.maxPlayerAbilities;
				}
			}
		} catch (IllegalArgumentException e) {
			EverlastingAbilities.clog(Level.ERROR, e.getMessage());
		}
	}

	@Override
	public void actionServer(World world, EntityPlayerMP player) {

	}
	
}