package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.ItemStack;

/**
 * A bottle with abilities.
 * @author rubensworks
 */
public class ItemAbilityBottleNeoForge extends ItemAbilityBottle {

    public ItemAbilityBottleNeoForge(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

}
