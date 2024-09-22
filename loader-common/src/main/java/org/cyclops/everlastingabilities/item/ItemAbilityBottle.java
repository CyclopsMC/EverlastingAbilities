package org.cyclops.everlastingabilities.item;

import net.minecraft.world.item.Item;

/**
 * A bottle with abilities.
 * @author rubensworks
 */
public abstract class ItemAbilityBottle extends ItemGuiAbilityContainer {

    public ItemAbilityBottle(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean canMoveFromPlayer() {
        return true;
    }

}
