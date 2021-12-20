package org.cyclops.everlastingabilities.item;

/**
 * A bottle with abilities.
 * @author rubensworks
 */
public class ItemAbilityBottle extends ItemGuiAbilityContainer {

    public ItemAbilityBottle(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canMoveFromPlayer() {
        return true;
    }

}
