package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.ItemStack;

/**
 * A totem with abilities.
 * @author rubensworks
 */
public class ItemAbilityTotemForge extends ItemAbilityTotem {

    public ItemAbilityTotemForge(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
