package org.cyclops.everlastingabilities;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.cyclops.cyclopscore.config.DeferredHolderCommon;

/**
 * @author rubensworks
 */
@Deprecated
public class RegistryEntriesCommon { // TODO: rename class when all entries are moved

    public static final DeferredHolderCommon<Item, Item> ITEM_ABILITY_BOTTLE = DeferredHolderCommon.create(Registries.ITEM, ResourceLocation.parse("everlastingabilities:ability_bottle"));
    public static final DeferredHolderCommon<Item, Item> ITEM_ABILITY_TOTEM = DeferredHolderCommon.create(Registries.ITEM, ResourceLocation.parse("everlastingabilities:ability_totem"));

}
