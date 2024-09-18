package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

import java.util.Optional;

/**
 * Packet from server to client to update capabilities.
 * @author rubensworks
 *
 */
public class MoveAbilityPacket extends PacketCodec {

    public static final Type<MoveAbilityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "move_ability"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MoveAbilityPacket> CODEC = getCodec(MoveAbilityPacket::new);

    @CodecField
    private String abilityName;
    @CodecField
    private int abilityLevel;
    @CodecField
    private int movement;

    public MoveAbilityPacket() {
        super(TYPE);
    }

    public MoveAbilityPacket(Registry<IAbilityType> registry, Ability ability, Movement movement) {
        this();
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
            Optional<Holder.Reference<IAbilityType>> abilityTypeOptional = AbilityHelpers.getRegistry(world.registryAccess()).getHolder(ResourceLocation.parse(abilityName));
            if (abilityTypeOptional.isPresent()) {
                Ability ability = new Ability(abilityTypeOptional.get(), abilityLevel);
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
