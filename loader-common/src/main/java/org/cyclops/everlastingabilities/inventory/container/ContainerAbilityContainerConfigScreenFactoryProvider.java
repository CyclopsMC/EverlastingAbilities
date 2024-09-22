package org.cyclops.everlastingabilities.inventory.container;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfigScreenFactoryProvider;
import org.cyclops.everlastingabilities.client.gui.ContainerScreenAbilityContainer;

/**
 * @author rubensworks
 */
public class ContainerAbilityContainerConfigScreenFactoryProvider extends GuiConfigScreenFactoryProvider<ContainerAbilityContainer> {
    @Override
    public <U extends Screen & MenuAccess<ContainerAbilityContainer>> MenuScreens.ScreenConstructor<ContainerAbilityContainer, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenAbilityContainer::new);
    }
}
