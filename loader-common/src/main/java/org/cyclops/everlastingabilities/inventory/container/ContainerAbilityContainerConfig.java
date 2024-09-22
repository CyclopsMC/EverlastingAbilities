package org.cyclops.everlastingabilities.inventory.container;

import net.minecraft.world.flag.FeatureFlags;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigCommon;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.cyclopscore.init.IModBase;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeDataCommon;

/**
 * Config for {@link ContainerAbilityContainer}.
 * @author rubensworks
 */
public class ContainerAbilityContainerConfig<M extends IModBase> extends GuiConfigCommon<ContainerAbilityContainer, M> {

    public ContainerAbilityContainerConfig(M mod) {
        super(mod,
                "ability_container",
                eConfig -> new ContainerTypeDataCommon<>(ContainerAbilityContainer::new, FeatureFlags.VANILLA_SET));
    }

    @Override
    public GuiConfigScreenFactoryProvider<ContainerAbilityContainer> getScreenFactoryProvider() {
        return new ContainerAbilityContainerConfigScreenFactoryProvider();
    }
}
