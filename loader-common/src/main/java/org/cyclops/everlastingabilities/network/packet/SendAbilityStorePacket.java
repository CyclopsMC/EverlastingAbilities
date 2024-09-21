package org.cyclops.everlastingabilities.network.packet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cyclops.cyclopscore.network.CodecField;
import org.cyclops.cyclopscore.network.PacketCodec;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.Reference;

/**
 * Packet from client to server to request an entity's ability store.
 * Additionally, {@link GeneralConfig#maxPlayerAbilities} will be synced.
 * @author rubensworks
 *
 */
public class SendAbilityStorePacket extends PacketCodec {

    public static final Type<SendAbilityStorePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "send_ability_store"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SendAbilityStorePacket> CODEC = getCodec(SendAbilityStorePacket::new);

    @CodecField
    private int entityId;
    @CodecField
    private CompoundTag tag;
    @CodecField
    private int maxPlayerAbilities;

    public SendAbilityStorePacket() {
        super(TYPE);
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
    public void actionClient(Level world, Player player) {
        try {
            if (world != null) {
                Entity entity = world.getEntity(entityId);
                if (entity != null) {
                    // Sync ability store
                    EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getEntityAbilityStore(entity).ifPresent(abilityStore -> {
                        EverlastingAbilitiesInstance.MOD.getAbilityHelpers().deserialize(EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRegistry(world.registryAccess()), abilityStore, tag.get("contents"));
                    });

                    // Sync max abilities value
                    EverlastingAbilitiesInstance.MOD.getAbilityHelpers().setMaxPlayerAbilitiesClient(this.maxPlayerAbilities);
                }
            }
        } catch (IllegalArgumentException e) {
            EverlastingAbilitiesInstance.MOD.log(org.apache.logging.log4j.Level.ERROR, e.getMessage());
        }
    }

    @Override
    public void actionServer(Level world, ServerPlayer player) {

    }

}
