package org.cyclops.everlastingabilities.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.cyclops.cyclopscore.client.gui.component.button.GuiButtonArrow;
import org.cyclops.cyclopscore.client.gui.container.GuiContainerConfigurable;
import org.cyclops.cyclopscore.client.gui.container.GuiContainerExtended;
import org.cyclops.cyclopscore.helper.*;
import org.cyclops.cyclopscore.inventory.container.ExtendedInventoryContainer;
import org.cyclops.cyclopscore.inventory.container.button.IButtonActionClient;
import org.cyclops.cyclopscore.item.IInformationProvider;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * Gui for the labeller.
 * @author rubensworks
 */
public class GuiAbilityContainer extends GuiContainerConfigurable<ContainerAbilityContainer> {

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected static final int ABILITY_LIST_SIZE = 6;
    protected static final int ABILITY_BOX_HEIGHT = 18;
    protected static final int ABILITY_BOX_WIDTH = 60;

    private static final int BUTTON_UP_1 = 0;
    private static final int BUTTON_DOWN_1 = 1;
    private static final int BUTTON_UP_2 = 2;
    private static final int BUTTON_DOWN_2 = 3;
    private static final int BUTTON_LEFT = 4;
    private static final int BUTTON_RIGHT = 5;

    private final EntityPlayer player;

    protected int startIndexPlayer = 0;
    protected int startIndexItem = 0;

    protected GuiButtonArrow buttonUp1;
    protected GuiButtonArrow buttonDown1;
    protected GuiButtonArrow buttonUp2;
    protected GuiButtonArrow buttonDown2;
    protected GuiButtonArrow buttonLeft;
    protected GuiButtonArrow buttonRight;

    /**
     * Make a new instance.
     * @param player The player.
     * @param itemIndex The index of the item in use inside the player inventory.
     */
    public GuiAbilityContainer(EntityPlayer player, int itemIndex) {
        super(new ContainerAbilityContainer(player, itemIndex));
        this.player = player;
        ContainerAbilityContainer container = getContainer();
        container.setGui(this);

        putButtonAction(BUTTON_UP_1, new IButtonActionClient<GuiContainerExtended, ExtendedInventoryContainer>() {
            @Override
            public void onAction(int buttonId, GuiContainerExtended gui, ExtendedInventoryContainer container) {
                if (startIndexPlayer > 0) startIndexPlayer--;
            }
        });
        putButtonAction(BUTTON_DOWN_1, new IButtonActionClient<GuiContainerExtended, ExtendedInventoryContainer>() {
            @Override
            public void onAction(int buttonId, GuiContainerExtended gui, ExtendedInventoryContainer container) {
                if (startIndexPlayer + ABILITY_LIST_SIZE < Math.min(ABILITY_LIST_SIZE, getPlayerAbilitiesCount()) - 1) startIndexPlayer++;
            }
        });
        putButtonAction(BUTTON_UP_1, new IButtonActionClient<GuiContainerExtended, ExtendedInventoryContainer>() {
            @Override
            public void onAction(int buttonId, GuiContainerExtended gui, ExtendedInventoryContainer container) {
                if (startIndexItem > 0) startIndexItem--;
            }
        });
        putButtonAction(BUTTON_DOWN_1, new IButtonActionClient<GuiContainerExtended, ExtendedInventoryContainer>() {
            @Override
            public void onAction(int buttonId, GuiContainerExtended gui, ExtendedInventoryContainer container) {
                if (startIndexItem + ABILITY_LIST_SIZE < Math.min(ABILITY_LIST_SIZE, getItemAbilitiesCount()) - 1) startIndexItem++;
            }
        });
    }

    @Override
    public void initGui() {
        super.initGui();

        buttonList.add(buttonUp1 = new GuiButtonArrow(BUTTON_UP_1, this.guiLeft + 73,  this.guiTop + 83, GuiButtonArrow.Direction.NORTH));
        buttonList.add(buttonDown1 = new GuiButtonArrow(BUTTON_DOWN_1, this.guiLeft + 73,  this.guiTop + 174, GuiButtonArrow.Direction.SOUTH));
        buttonList.add(buttonUp2 = new GuiButtonArrow(BUTTON_UP_2, this.guiLeft + 88,  this.guiTop + 83, GuiButtonArrow.Direction.NORTH));
        buttonList.add(buttonDown2 = new GuiButtonArrow(BUTTON_DOWN_2, this.guiLeft + 88,  this.guiTop + 174, GuiButtonArrow.Direction.SOUTH));

        buttonList.add(buttonLeft = new GuiButtonArrow(BUTTON_LEFT, this.guiLeft + 78,  this.guiTop + 130, GuiButtonArrow.Direction.WEST));
        buttonList.add(buttonRight = new GuiButtonArrow(BUTTON_RIGHT, this.guiLeft + 88,  this.guiTop + 130, GuiButtonArrow.Direction.EAST));
    }

    @Override
    protected int getBaseYSize() {
        return 220;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // Draw abilities
        drawAbilitiesTooltip(8, 84, getPlayerAbilities(), startIndexPlayer, mouseX, mouseY);
        drawAbilitiesTooltip(106, 84, getItemAbilities(), startIndexItem, mouseX, mouseY);
    }

    protected @Nullable List<Ability> getPlayerAbilities() {
        return getContainer().getPlayerAbilities();
    }

    protected List<Ability> getItemAbilities() {
        return getContainer().getItemAbilities();
    }

    protected int getPlayerAbilitiesCount() {
        return getPlayerAbilities().size();
    }

    protected int getItemAbilitiesCount() {
        return getItemAbilities().size();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        buttonUp1.enabled = startIndexPlayer > 0;
        buttonDown1.enabled = startIndexPlayer + ABILITY_LIST_SIZE < Math.min(ABILITY_LIST_SIZE, getPlayerAbilitiesCount()) - 1;
        buttonUp2.enabled = startIndexItem > 0;
        buttonDown2.enabled = startIndexItem + ABILITY_LIST_SIZE < Math.min(ABILITY_LIST_SIZE, getItemAbilitiesCount()) - 1;

        buttonLeft.enabled = false;//TODO
        buttonRight.enabled = false;//TODO

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int i = this.guiLeft;
        int j = this.guiTop;
        drawFancyBackground(i + 8, j + 17, 66, 61, false, 0.2F); // TODO: variable 'activity' (it's not really working btw)
        GuiInventory.drawEntityOnScreen(i + 41, j + 74, 30, (float)(i + 41) - mouseX, (float)(j + 76 - 50) - mouseY, this.mc.thePlayer);
        drawFancyBackground(i + 102, j + 17, 66, 61, true, 1F); // TODO: variable 'activity'
        drawItemOnScreen(i + 134, j + 46, 50, (float)(i + 134) - mouseX, (float)(j + 46 - 30) - mouseY, getContainer().getItemStack(this.mc.thePlayer));

        // Draw abilities
        drawAbilities(this.guiLeft + 8, this.guiTop + 84, getPlayerAbilities(), startIndexPlayer, Integer.MAX_VALUE);
        drawAbilities(this.guiLeft + 106, this.guiTop + 84, getItemAbilities(), startIndexItem, player.experienceTotal);
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

    protected void drawXp(int x, int y) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(x, y, 0, 219, 5, 5);
    }

    private void drawAbilities(int x, int y, List<Ability> abilities, int startIndex, int playerXp) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            Ability ability = abilities.get(i + startIndex);

            // Name
            RenderHelpers.drawScaledCenteredString(fontRendererObj,
                    ability.getAbilityType().getRarity().rarityColor + L10NHelpers.localize(ability.getAbilityType().getUnlocalizedName()),
                    x + 27, boxY + 6, 0, 1.0F, 50, -1);

            // Level
            RenderHelpers.drawScaledCenteredString(fontRendererObj,
                    "" + ability.getLevel(),
                    x + 58, boxY + 4, 0, 0.8F, 10000);

            // XP
            int requiredXp = ability.getAbilityType().getBaseXpPerLevel();
            if (playerXp < requiredXp) {
                GlStateManager.color(0.3F, 0.3F, 0.3F, 1);
            } else {
                GlStateManager.color(1, 1, 1, 1);
            }
            drawXp(x + 57, boxY + 9);
            RenderHelpers.drawScaledCenteredString(fontRendererObj,
                    "" + requiredXp,
                    x + 53, boxY + 12, 0, 0.5F, Helpers.RGBToInt(40, 215, 40));

            // TODO: select box (+hover)
        }
        GlStateManager.color(1, 1, 1, 1);
    }

    private void drawAbilitiesTooltip(int x, int y, List<Ability> abilities, int startIndex, int mouseX, int mouseY) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            if(isPointInRegion(new Rectangle(x, boxY, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT), new Point(mouseX, mouseY))) {
                Ability ability = abilities.get(i + startIndex);
                List<String> lines = Lists.newLinkedList();

                // Name
                lines.add(ability.getAbilityType().getRarity().rarityColor
                        + L10NHelpers.localize(ability.getAbilityType().getUnlocalizedName()));

                // Level
                lines.add(L10NHelpers.localize("general.everlastingabilities.level", ability.getLevel(), ability.getAbilityType().getMaxLevel()));

                // Description
                String localizedDescription = L10NHelpers.localize(ability.getAbilityType().getUnlocalizedDescription());
                lines.addAll(StringHelpers.splitLines(localizedDescription, L10NHelpers.MAX_TOOLTIP_LINE_LENGTH,
                        IInformationProvider.INFO_PREFIX));

                // Xp
                lines.add(TextFormatting.DARK_GREEN + L10NHelpers.localize("general.everlastingabilities.xp", ability.getAbilityType().getBaseXpPerLevel()));

                drawTooltip(lines, mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
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
