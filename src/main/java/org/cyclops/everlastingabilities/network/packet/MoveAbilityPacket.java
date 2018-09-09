package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.ability.AbilityTypes;
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

	public MoveAbilityPacket(Ability ability, Movement movement) {
		this.abilityName = ability.getAbilityType().getUnlocalizedName();
		this.abilityLevel = ability.getLevel();
		this.movement = movement.ordinal();
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
		if (player.openContainer instanceof ContainerAbilityContainer) {
			ContainerAbilityContainer container = (ContainerAbilityContainer) player.openContainer;
			IAbilityType abilityType = AbilityTypes.REGISTRY.getAbilityType(abilityName);
			if (abilityType != null) {
				Ability ability = new Ability(abilityType, abilityLevel);
				if (movement == Movement.FROM_PLAYER.ordinal()) {
					if (AbilityHelpers.canExtract(ability, container.getPlayerAbilityStore())
							&& AbilityHelpers.canInsert(ability, container.getItemAbilityStore())) {
						container.moveFromPlayer(ability);
					}
				} else {
					if (AbilityHelpers.canExtract(ability, container.getItemAbilityStore())
							&& AbilityHelpers.canInsert(ability, container.getPlayerAbilityStore())) {
						container.moveToPlayer(ability);
					}
				}
			}
		}
	}

	public static enum Movement {
		TO_PLAYER, FROM_PLAYER
	}
	
}