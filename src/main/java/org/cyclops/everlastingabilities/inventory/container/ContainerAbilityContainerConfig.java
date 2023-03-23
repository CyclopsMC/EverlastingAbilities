package org.cyclops.everlastingabilities.inventory.container;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.cyclops.cyclopscore.client.gui.ScreenFactorySafe;
import org.cyclops.cyclopscore.config.extendedconfig.GuiConfig;
import org.cyclops.cyclopscore.inventory.container.ContainerTypeData;
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
                eConfig -> new ContainerTypeData<>(ContainerAbilityContainer::new, FeatureFlags.VANILLA_SET));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <U extends Screen & MenuAccess<ContainerAbilityContainer>> MenuScreens.ScreenConstructor<ContainerAbilityContainer, U> getScreenFactory() {
        return new ScreenFactorySafe<>(ContainerScreenAbilityContainer::new);
    }

}
