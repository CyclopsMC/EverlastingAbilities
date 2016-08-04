package org.cyclops.everlastingabilities.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import org.cyclops.cyclopscore.client.gui.container.GuiContainerConfigurable;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

/**
 * Gui for the labeller.
 * @author rubensworks
 */
public class GuiAbilityContainer extends GuiContainerConfigurable<ContainerAbilityContainer> {

    /**
     * Make a new instance.
     * @param player The player.
     * @param itemIndex The index of the item in use inside the player inventory.
     */
    public GuiAbilityContainer(EntityPlayer player, int itemIndex) {
        super(new ContainerAbilityContainer(player, itemIndex));
        ContainerAbilityContainer container = getContainer();
        container.setGui(this);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected int getBaseYSize() {
        return 113;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

}
