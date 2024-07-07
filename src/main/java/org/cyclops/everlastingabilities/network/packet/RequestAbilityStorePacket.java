package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;

import java.util.Optional;
import java.util.UUID;

/**
 * Packet from client to server to request an entity's ability store.
 * @author rubensworks
 *
 */
public class RequestAbilityStorePacket extends PacketCodec {

	public static final Type<RequestAbilityStorePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "request_ability_store"));
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestAbilityStorePacket> CODEC = getCodec(RequestAbilityStorePacket::new);

	@CodecField
	private String entityUuid;

    public RequestAbilityStorePacket() {
		super(TYPE);
    }

	public RequestAbilityStorePacket(String entityUuid) {
		this();
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
				Optional.ofNullable(entity.getCapability(Capabilities.MutableAbilityStore.ENTITY)).ifPresent(abilityStore -> {
					Tag contents = AbilityHelpers.serialize(AbilityHelpers.getRegistry(world.registryAccess()), abilityStore);
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
