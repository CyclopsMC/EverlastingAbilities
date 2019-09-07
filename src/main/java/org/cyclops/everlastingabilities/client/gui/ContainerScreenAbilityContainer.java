package org.cyclops.everlastingabilities.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.client.gui.component.button.ButtonArrow;
import org.cyclops.cyclopscore.client.gui.container.ContainerScreenExtended;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.L10NHelpers;
import org.cyclops.cyclopscore.helper.RenderHelpers;
import org.cyclops.cyclopscore.helper.StringHelpers;
import org.cyclops.cyclopscore.item.IInformationProvider;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;
import org.cyclops.everlastingabilities.network.packet.MoveAbilityPacket;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Gui for the labeller.
 * @author rubensworks
 */
public class ContainerScreenAbilityContainer extends ContainerScreenExtended<ContainerAbilityContainer> {

    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected static final int ABILITY_LIST_SIZE = 6;
    protected static final int ABILITY_BOX_HEIGHT = 18;
    protected static final int ABILITY_BOX_WIDTH = 63;

    private final PlayerEntity player;

    protected int startIndexPlayer = 0;
    protected int startIndexItem = 0;

    protected int absoluteSelectedIndexPlayer = -1;
    protected int absoluteSelectedIndexItem = -1;

    protected ButtonArrow buttonUp1;
    protected ButtonArrow buttonDown1;
    protected ButtonArrow buttonUp2;
    protected ButtonArrow buttonDown2;
    protected ButtonArrow buttonLeft;
    protected ButtonArrow buttonRight;

    public ContainerScreenAbilityContainer(ContainerAbilityContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.player = inventory.player;
        container.setGui(this);
    }

    @Override
    protected ResourceLocation constructGuiTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/ability_totem.png");
    }

    @Override
    public void init() {
        super.init();

        addButton(buttonUp1 = new ButtonArrow(this.guiLeft + 73,  this.guiTop + 83, L10NHelpers.localize("gui.cyclopscore.up"), button -> {
            if (startIndexPlayer > 0) startIndexPlayer--;
        }, ButtonArrow.Direction.NORTH));
        addButton(buttonDown1 = new ButtonArrow(this.guiLeft + 73,  this.guiTop + 174, L10NHelpers.localize("gui.cyclopscore.down"), button -> {
            if (startIndexPlayer + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getPlayerAbilitiesCount())) startIndexPlayer++;
        }, ButtonArrow.Direction.SOUTH));
        addButton(buttonUp2 = new ButtonArrow(this.guiLeft + 88,  this.guiTop + 83, L10NHelpers.localize("gui.cyclopscore.up"), button -> {
            if (startIndexItem > 0) startIndexItem--;
        }, ButtonArrow.Direction.NORTH));
        addButton(buttonDown2 = new ButtonArrow(this.guiLeft + 88,  this.guiTop + 174, L10NHelpers.localize("gui.cyclopscore.down"), button -> {
            if (startIndexItem + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getItemAbilitiesCount())) startIndexItem++;
        }, ButtonArrow.Direction.SOUTH));

        addButton(buttonLeft = new ButtonArrow(this.guiLeft + 76,  this.guiTop + 130, L10NHelpers.localize("gui.cyclopscore.left"), button -> {
            if (canMoveToPlayer()) {
                EverlastingAbilities._instance.getPacketHandler().sendToServer(
                        new MoveAbilityPacket(getSelectedItemAbilitySingle(), MoveAbilityPacket.Movement.TO_PLAYER));
                moveToPlayer();
            }
        }, ButtonArrow.Direction.WEST));
        addButton(buttonRight = new ButtonArrow(this.guiLeft + 90,  this.guiTop + 130, L10NHelpers.localize("gui.cyclopscore.right"), button -> {
            if (canMoveFromPlayer()) {
                EverlastingAbilities._instance.getPacketHandler().sendToServer(
                        new MoveAbilityPacket(getSelectedPlayerAbilitySingle(), MoveAbilityPacket.Movement.FROM_PLAYER));
                moveFromPlayer();
            }
        }, ButtonArrow.Direction.EAST));
    }

    @Override
    protected int getBaseYSize() {
        return 219;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        if (getContainer().getItemStack(player) == null) {
            return;
        }

        this.font.drawString(player.getDisplayName().getString(), 8, 6, -1);
        this.font.drawString(getContainer().getItemStack(player).getDisplayName().toString(), 102, 6, -1);

        // Draw abilities
        drawAbilitiesTooltip(8, 83, getPlayerAbilities(), startIndexPlayer, mouseX, mouseY);
        drawAbilitiesTooltip(105, 83, getItemAbilities(), startIndexItem, mouseX, mouseY);
    }

    protected List<Ability> getPlayerAbilities() {
        List<Ability> abilities = getContainer().getPlayerAbilities();
        Collections.sort(abilities);
        return abilities;
    }

    protected List<Ability> getItemAbilities() {
        List<Ability> abilities = getContainer().getItemAbilities();
        Collections.sort(abilities);
        return abilities;
    }

    protected IMutableAbilityStore getPlayerAbilityStore() {
        return getContainer().getPlayerAbilityStore().orElse(null);
    }

    protected IMutableAbilityStore getItemAbilityStore() {
        return getContainer().getItemAbilityStore().orElse(null);
    }

    protected int getPlayerAbilitiesCount() {
        return getPlayerAbilities().size();
    }

    protected int getItemAbilitiesCount() {
        return getItemAbilities().size();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (getContainer().getItemStack(player) == null) {
            return;
        }

        buttonUp1.visible = startIndexPlayer > 0;
        buttonDown1.visible = startIndexPlayer + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getPlayerAbilitiesCount());
        buttonUp2.visible = startIndexItem > 0;
        buttonDown2.visible = startIndexItem + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getItemAbilitiesCount());

        buttonLeft.visible = canMoveToPlayer();
        buttonRight.visible = canMoveFromPlayer();
        buttonRight.visible = canMoveFromPlayerByItem();

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int i = this.guiLeft;
        int j = this.guiTop;
        drawFancyBackground(i + 8, j + 17, 66, 61, getPlayerAbilityStore());
        InventoryScreen.drawEntityOnScreen(i + 41, j + 74, 30, (float)(i + 41) - mouseX, (float)(j + 76 - 50) - mouseY, this.getMinecraft().player);
        drawXp(i + 67, j + 70);
        RenderHelpers.drawScaledCenteredString(font, "" + player.experienceTotal, i + 62, j + 73, 0, 0.5F, Helpers.RGBToInt(40, 215, 40));
        drawFancyBackground(i + 102, j + 17, 66, 61, getItemAbilityStore());
        drawItemOnScreen(i + 134, j + 46, 50, (float)(i + 134) - mouseX, (float)(j + 46 - 30) - mouseY, getContainer().getItemStack(this.getMinecraft().player));

        // Draw abilities
        drawAbilities(this.guiLeft + 8, this.guiTop + 83, getPlayerAbilities(), startIndexPlayer, Integer.MAX_VALUE, absoluteSelectedIndexPlayer, mouseX, mouseY, canMoveFromPlayerByItem());
        drawAbilities(this.guiLeft + 105, this.guiTop + 83, getItemAbilities(), startIndexItem, player.experienceTotal, absoluteSelectedIndexItem, mouseX, mouseY, true);
    }

    public void drawFancyBackground(int x, int y, int width, int height, IAbilityStore abilityStore) {
        int r = 140;
        int g = 140;
        int b = 140;
        if (abilityStore != null) {
            if (abilityStore.getAbilityTypes().isEmpty()) return;
            Triple<Integer, Integer, Integer> color = AbilityHelpers.getAverageRarityColor(abilityStore);
            r = color.getLeft();
            g = color.getMiddle();
            b = color.getRight();
        }

        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        RenderHelpers.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
        float f = (float)(Util.milliTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translatef(f, 0.0F, 0.0F);
        GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        drawTexturedModalRectColor(x, y, 0, 0, width, height, r, g, b, 255);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
        float f1 = (float)(Util.milliTime() % 4873L) / 4873.0F / 8.0F;
        GlStateManager.translatef(-f1, 0.0F, 0.0F);
        GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
        float rotation = ((float) (Util.milliTime() / 100 % 3600)) / 10F;
        GlStateManager.rotatef(rotation, 1.0F, 0.5F, 1.0F);
        drawTexturedModalRectColor(x, y, 0, 0, width, height, r, g, b, 255);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        //GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        RenderHelpers.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

        GlStateManager.color4f(1, 1, 1, 1);
    }

    protected void drawXp(int x, int y) {
        RenderHelpers.bindTexture(texture);
        blit(x, y, 0, 219, 5, 5);
    }

    private void drawAbilities(int x, int y, List<Ability> abilities, int startIndex, int playerXp,
                               int currentSelectedIndex, int mouseX, int mouseY, boolean canEdit) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            Ability ability = abilities.get(i + startIndex);

            // select box (+hover)
            if (canEdit) {
                boolean active = currentSelectedIndex == i + startIndex;
                boolean showActive = active || isPointInRegion(new Rectangle(x - this.guiLeft, boxY - this.guiTop, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT), new Point(mouseX, mouseY));
                if (showActive) {
                    drawFancyBackground(x, boxY - 1, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT, null);
                }
            }

            // Name
            RenderHelpers.drawScaledCenteredString(font,
                    ability.getAbilityType().getRarity().color + L10NHelpers.localize(ability.getAbilityType().getTranslationKey()),
                    x + 27, boxY + 7, 0, 1.0F, 50, -1);

            // Level
            RenderHelpers.drawScaledCenteredString(font,
                    "" + ability.getLevel(),
                    x + 58, boxY + 5, 0, 0.8F, -1);

            // XP
            int requiredXp = ability.getAbilityType().getBaseXpPerLevel();
            if (playerXp < requiredXp) {
                GlStateManager.color4f(0.3F, 0.3F, 0.3F, 1);
            } else {
                GlStateManager.color4f(1, 1, 1, 1);
            }
            drawXp(x + 57, boxY + 10);
            RenderHelpers.drawScaledCenteredString(font,
                    "" + requiredXp,
                    x + 53, boxY + 13, 0, 0.5F, Helpers.RGBToInt(40, 215, 40));
        }
        GlStateManager.color4f(1, 1, 1, 1);
    }

    private void drawAbilitiesTooltip(int x, int y, List<Ability> abilities, int startIndex, int mouseX, int mouseY) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            if(isPointInRegion(new Rectangle(x, boxY, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT), new Point(mouseX, mouseY))) {
                Ability ability = abilities.get(i + startIndex);
                List<String> lines = Lists.newLinkedList();

                // Name
                lines.add(ability.getAbilityType().getRarity().color
                        + L10NHelpers.localize(ability.getAbilityType().getTranslationKey()));

                // Level
                lines.add(L10NHelpers.localize("general.everlastingabilities.level", ability.getLevel(),
                        ability.getAbilityType().getMaxLevel() == -1 ? "Inf" : ability.getAbilityType().getMaxLevel()));

                // Description
                String localizedDescription = L10NHelpers.localize(ability.getAbilityType().getUnlocalizedDescription());
                lines.addAll(StringHelpers.splitLines(localizedDescription, L10NHelpers.MAX_TOOLTIP_LINE_LENGTH,
                        IInformationProvider.INFO_PREFIX));

                // Xp
                lines.add(TextFormatting.DARK_GREEN + L10NHelpers.localize("general.everlastingabilities.xp",
                        ability.getAbilityType().getBaseXpPerLevel(),
                        AbilityHelpers.getLevelForExperience(ability.getAbilityType().getBaseXpPerLevel())));

                drawTooltip(lines, mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
    }

    public void drawTexturedModalRectColor(int x, int y, int textureX, int textureY, int width, int height, int r, int g, int b, int a) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.blitOffset).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).color(r, g, b, a).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.blitOffset).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + height) * f1)).color(r, g, b, a).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.blitOffset).tex((double) ((float) (textureX + width) * f), (double) ((float) (textureY + 0) * f1)).color(r, g, b, a).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.blitOffset).tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1)).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    public static void drawItemOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, ItemStack itemStack) {
        GlStateManager.enableColorMaterial();

        GlStateManager.pushMatrix();

        GlStateManager.translatef((float)posX, (float)posY, 50.0F);
        GlStateManager.scalef((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.rotatef(-(float)Math.atan((double)(mouseX / 40.0F)) * 40.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(-((float)Math.atan((double)(mouseY / 20.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

        GlStateManager.pushTextureAttributes();
        GlStateManager.pushLightingAttributes();
        RenderHelper.enableStandardItemLighting();
        RenderHelpers.renderItem(itemStack);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttributes();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        int newSelectedPlayer = canMoveFromPlayerByItem() ? clickAbilities(8, 83, getPlayerAbilities(), startIndexPlayer, absoluteSelectedIndexPlayer, mouseX, mouseY) : -2;
        int newSelectedItem = clickAbilities(105, 83, getItemAbilities(), startIndexItem, absoluteSelectedIndexItem, mouseX, mouseY);

        if (newSelectedPlayer >= -1) {
            absoluteSelectedIndexPlayer = newSelectedPlayer;
        }
        if (newSelectedItem >= -1) {
            absoluteSelectedIndexItem = newSelectedItem;
        }

        if (newSelectedPlayer < 0 && newSelectedItem < 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        return true;
    }

    private int clickAbilities(int x, int y, List<Ability> abilities, int startIndex, int currentSelectedIndex,
                               double mouseX, double mouseY) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            if (isPointInRegion(new Rectangle(x, boxY, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT), new Point((int) mouseX, (int) mouseY))) {
                int absoluteIndex = startIndex + i;
                if (currentSelectedIndex == absoluteIndex) {
                    return -1;
                } else {
                    return absoluteIndex;
                }
            }
        }
        return -2;
    }

    public Ability getSelectedPlayerAbilitySingle() {
        Ability ability = getSelectedPlayerAbility();
        if (ability != null) {
            ability = new Ability(ability.getAbilityType(), 1);
        }
        return ability;
    }

    public Ability getSelectedItemAbilitySingle() {
        Ability ability = getSelectedItemAbility();
        if (ability != null) {
            ability = new Ability(ability.getAbilityType(), 1);
        }
        return ability;
    }

    public Ability getSelectedPlayerAbility() {
        List<Ability> abilities = getPlayerAbilities();
        if (absoluteSelectedIndexPlayer >= 0 && absoluteSelectedIndexPlayer < abilities.size()) {
            return abilities.get(absoluteSelectedIndexPlayer);
        }
        return null;
    }

    public Ability getSelectedItemAbility() {
        List<Ability> abilities = getItemAbilities();
        if (absoluteSelectedIndexItem >= 0 && absoluteSelectedIndexItem < abilities.size()) {
            return abilities.get(absoluteSelectedIndexItem);
        }
        return null;
    }

    public boolean canMoveFromPlayer(Ability ability, PlayerEntity player, IMutableAbilityStore target) {
        return ability != null && AbilityHelpers.canInsert(ability, target);
    }

    public boolean canMoveToPlayer(Ability ability, PlayerEntity player) {
        return ability != null && AbilityHelpers.canInsertToPlayer(ability, player);
    }

    public boolean canMoveFromPlayerByItem() {
        return getContainer().getItem().canMoveFromPlayer();
    }

    public boolean canMoveFromPlayer() {
        if (!canMoveFromPlayerByItem()) {
            return false;
        }
        Ability playerAbility = getSelectedPlayerAbilitySingle();
        return canMoveFromPlayer(playerAbility, player, getItemAbilityStore());
    }

    public boolean canMoveToPlayer() {
        Ability itemAbility = getSelectedItemAbilitySingle();
        return canMoveToPlayer(itemAbility, player);
    }

    public void moveFromPlayer() {
        getContainer().moveFromPlayer(getSelectedPlayerAbilitySingle());
    }

    public void moveToPlayer() {
        getContainer().moveToPlayer(getSelectedItemAbilitySingle());
    }
}
