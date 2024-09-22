package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.ItemStack;

/**
 * A totem with abilities.
 * @author rubensworks
 */
public class ItemAbilityTotemNeoForge extends ItemAbilityTotem {

    public ItemAbilityTotemNeoForge(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
