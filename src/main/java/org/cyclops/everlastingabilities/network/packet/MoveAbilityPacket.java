package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

/**
 * Packet from server to client to update capabilities.
 * @author rubensworks
 *
 */
public class MoveAbilityPacket extends PacketCodec {

	@CodecField
	private String abilityName;
	@CodecField
	private int abilityLevel;
	@CodecField
	private int movement;

    public MoveAbilityPacket() {

    }

	public MoveAbilityPacket(Registry<IAbilityType> registry, Ability ability, Movement movement) {
		this.abilityName = registry.getKey(ability.getAbilityType()).toString();
		this.abilityLevel = ability.getLevel();
		this.movement = movement.ordinal();
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
		if (player.containerMenu instanceof ContainerAbilityContainer) {
			ContainerAbilityContainer container = (ContainerAbilityContainer) player.containerMenu;
			IAbilityType abilityType = AbilityHelpers.getRegistry(world.registryAccess()).get(new ResourceLocation(abilityName));
			if (abilityType != null) {
				Ability ability = new Ability(abilityType, abilityLevel);
				container.getPlayerAbilityStore().ifPresent(playerAbilityStore -> {
					container.getItemAbilityStore().ifPresent(itemAbilityStore -> {
						if (movement == Movement.FROM_PLAYER.ordinal()) {
							if (AbilityHelpers.canExtract(ability, playerAbilityStore)
									&& AbilityHelpers.canInsert(ability, itemAbilityStore)) {
								container.moveFromPlayer(ability);
							}
						} else {
							if (AbilityHelpers.canExtract(ability, itemAbilityStore)
									&& AbilityHelpers.canInsert(ability, playerAbilityStore)) {
								container.moveToPlayer(ability);
							}
						}
					});
				});
			}
		}
	}

	public static enum Movement {
		TO_PLAYER, FROM_PLAYER
	}

}
