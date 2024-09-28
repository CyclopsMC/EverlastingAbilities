package org.cyclops.everlastingabilities.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * A bottle with abilities.
 * @author rubensworks
 */
public class ItemAbilityBottleFabric extends ItemAbilityBottle {

    public ItemAbilityBottleFabric(Properties properties) {
        super(properties);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

}
