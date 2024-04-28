package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.Capabilities;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;

import java.util.Optional;

/**
 * Packet from client to server to request an entity's ability store.
 * Additionally, {@link GeneralConfig#maxPlayerAbilities} will be synced.
 * @author rubensworks
 *
 */
public class SendAbilityStorePacket extends PacketCodec {

	public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "send_ability_store");

	@CodecField
	private int entityId;
	@CodecField
	private CompoundTag tag;
	@CodecField
	private int maxPlayerAbilities;

    public SendAbilityStorePacket() {
		super(ID);
    }

	public SendAbilityStorePacket(int entityId, CompoundTag tag) {
		this();
		this.entityId = entityId;
		this.tag = tag;
		this.maxPlayerAbilities = GeneralConfig.maxPlayerAbilities;
	}

	@Override
	public boolean isAsync() {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void actionClient(Level world, Player player) {
		try {
			if (world != null) {
				Entity entity = world.getEntity(entityId);
				if (entity != null) {
					// Sync ability store
					Optional.ofNullable(entity.getCapability(Capabilities.MutableAbilityStore.ENTITY)).ifPresent(abilityStore -> {
						AbilityHelpers.deserialize(AbilityHelpers.getRegistry(world.registryAccess()), abilityStore, tag.get("contents"));
					});

					// Sync max abilities value
					AbilityHelpers.maxPlayerAbilitiesClient = this.maxPlayerAbilities;
				}
			}
		} catch (IllegalArgumentException e) {
			EverlastingAbilities.clog(org.apache.logging.log4j.Level.ERROR, e.getMessage());
		}
	}

	@Override
	public void actionServer(Level world, ServerPlayer player) {

	}

}
