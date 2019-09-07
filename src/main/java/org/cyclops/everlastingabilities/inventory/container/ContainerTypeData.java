package org.cyclops.everlastingabilities.inventory.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.network.IContainerFactory;

/**
 * A {@link ContainerType} for a {@link IContainerFactory}.
 * This enables more convenient syntax via lambdas than the default {@link ContainerType}.
 *
 * For example: `new ContainerTypeData<>(ContainerAbilityContainer::new))`.
 *
 * TODO: move to cyclopscore
 * @author rubensworks
 */
public class ContainerTypeData<T extends Container> extends ContainerType<T> {
    public ContainerTypeData(IContainerFactory<T> factory) {
        super(factory);
    }
}
