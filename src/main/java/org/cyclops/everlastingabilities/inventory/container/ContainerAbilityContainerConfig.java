package org.cyclops.everlastingabilities.inventory.container;

import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.client.gui.ContainerScreenAbilityContainer;

/**
 * Config for {@link ContainerAbilityContainer}.
 * @author rubensworks
 */
public class ContainerAbilityContainerConfig extends GuiConfig<ContainerAbilityContainer> {

    public ContainerAbilityContainerConfig() {
        super(EverlastingAbilities._instance,
                "ability_container",
                eConfig -> new ContainerTypeData<>(ContainerAbilityContainer::new));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <U extends Screen & IHasContainer<ContainerAbilityContainer>> ScreenManager.IScreenFactory<ContainerAbilityContainer, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenAbilityContainer::new);
    }

}
