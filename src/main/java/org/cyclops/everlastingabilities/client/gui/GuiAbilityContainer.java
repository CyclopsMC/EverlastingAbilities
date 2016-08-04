package org.cyclops.everlastingabilities.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.cyclops.cyclopscore.client.gui.container.GuiContainerConfigurable;
import org.cyclops.cyclopscore.helper.RenderHelpers;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

/**
 * Gui for the labeller.
 * @author rubensworks
 */
public class GuiAbilityContainer extends GuiContainerConfigurable<ContainerAbilityContainer> {

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

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
        return 220;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        int i = this.guiLeft;
        int j = this.guiTop;
        drawFancyBackground(i + 8, j + 17, 66, 61, false, 0.2F); // TODO: variable 'activity' (it's not really working btw)
        GuiInventory.drawEntityOnScreen(i + 41, j + 76, 30, (float)(i + 41) - mouseX, (float)(j + 76 - 50) - mouseY, this.mc.thePlayer);
        drawFancyBackground(i + 102, j + 17, 66, 61, true, 1F); // TODO: variable 'activity'
        drawItemOnScreen(i + 134, j + 46, 50, (float)(i + 134) - mouseX, (float)(j + 46 - 30) - mouseY, getContainer().getItemStack(this.mc.thePlayer));
    }

    public void drawFancyBackground(int x, int y, int width, int height, boolean mirror, float activity) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        Minecraft.getMinecraft().getTextureManager().bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        drawTexturedModalRectColor(x, y, 0, 0, width, height, 200, 50, 150, (int) (activity * 255F));
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translate(-f1, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        float rotation = Minecraft.getSystemTime() % 360.0F;
        GlStateManager.rotate(rotation, 1.0F, 0.5F, 1.0F);
        drawTexturedModalRectColor(x, y, 0, 0, width, height, 150, 50, 150, (int) (activity * 255F));
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    public void drawTexturedModalRectColor(int x, int y, int textureX, int textureY, int width, int height, int r, int g, int b, int a) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).color(r, g, b, a).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1)).color(r, g, b, a).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + 0) * f1)).color(r, g, b, a).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1)).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    public static void drawItemOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, ItemStack itemStack) {
        GlStateManager.enableColorMaterial();

        GlStateManager.pushMatrix();

        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.rotate(-(float)Math.atan((double)(mouseX / 40.0F)) * 40.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 20.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();
        RenderHelpers.renderItem(itemStack);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

}
