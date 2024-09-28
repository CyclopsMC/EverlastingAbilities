package org.cyclops.everlastingabilities.attachment;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.core.helper.CodecHelpers;

/**
 * @author rubensworks
 */
public class Attachments {

    public static AttachmentType<Boolean> LAST_FLIGHT;
    public static AttachmentType<Boolean> TOTEM_SPAWNED;
    public static AttachmentType<IAbilityStore> ABILITY_STORE;

    public static void register() {
        LAST_FLIGHT = AttachmentRegistry.<Boolean>builder()
                .copyOnDeath()
                .persistent(Codec.BOOL)
                .buildAndRegister(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "last_flight"));
        TOTEM_SPAWNED = AttachmentRegistry.<Boolean>builder()
                .copyOnDeath()
                .persistent(Codec.BOOL)
                .buildAndRegister(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "totem_spawned"));
        ABILITY_STORE = AttachmentRegistry.<IAbilityStore>builder()
                .copyOnDeath()
                .persistent(CodecHelpers.CODEC_ABILITY_STORE)
                .buildAndRegister(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ability_store"));
    }

}
