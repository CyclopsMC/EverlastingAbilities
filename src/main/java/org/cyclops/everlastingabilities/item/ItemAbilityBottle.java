package org.cyclops.everlastingabilities.item;

import org.cyclops.cyclopscore.config.extendedconfig.ExtendedConfig;

/**
 * A bottle with abilities.
 * @author rubensworks
 */
public class ItemAbilityBottle extends ItemGuiAbilityContainer {

    private static ItemAbilityBottle _instance = null;

    /**
     * Get the unique instance.
     * @return The instance.
     */
    public static ItemAbilityBottle getInstance() {
        return _instance;
    }

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ItemAbilityBottle(ExtendedConfig eConfig) {
        super(eConfig);
        this.maxStackSize = 1;
    }

    @Override
    public boolean canMoveFromPlayer() {
        return true;
    }


}
