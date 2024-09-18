package org.cyclops.everlastingabilities;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

/**
 * @author rubensworks
 */
public class Capabilities {

    public static final class AbilityStore {
        public static final BlockCapability<IAbilityStore, Direction> BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ability_store"), IAbilityStore.class);
        public static final ItemCapability<IAbilityStore, Void> ITEM = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ability_store"), IAbilityStore.class);
        public static final EntityCapability<IAbilityStore, Void> ENTITY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "ability_store"), IAbilityStore.class);
    }

    public static final class MutableAbilityStore {
        public static final BlockCapability<IMutableAbilityStore, Direction> BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "mutable_ability_store"), IMutableAbilityStore.class);
        public static final ItemCapability<IMutableAbilityStore, Void> ITEM = ItemCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "mutable_ability_store"), IMutableAbilityStore.class);
        public static final EntityCapability<IMutableAbilityStore, Void> ENTITY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "mutable_ability_store"), IMutableAbilityStore.class);
    }

}
