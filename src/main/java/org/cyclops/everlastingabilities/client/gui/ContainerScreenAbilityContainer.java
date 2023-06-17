package org.cyclops.everlastingabilities.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.client.gui.component.button.ButtonArrow;
import org.cyclops.cyclopscore.client.gui.container.ContainerScreenExtended;
import org.cyclops.cyclopscore.helper.Helpers;
import org.cyclops.cyclopscore.helper.RenderHelpers;
import org.cyclops.cyclopscore.item.IInformationProvider;
import org.cyclops.everlastingabilities.EverlastingAbilities;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;
import org.cyclops.everlastingabilities.network.packet.MoveAbilityPacket;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Gui for the labeller.
 * @author rubensworks
 */
public class ContainerScreenAbilityContainer extends ContainerScreenExtended<ContainerAbilityContainer> {

    private static final ResourceLocation RES_ITEM_GLINT = ItemRenderer.ENCHANTED_GLINT_ITEM;
    protected static final int ABILITY_LIST_SIZE = 6;
    protected static final int ABILITY_BOX_HEIGHT = 18;
    protected static final int ABILITY_BOX_WIDTH = 63;

    private final Player player;

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

    public ContainerScreenAbilityContainer(ContainerAbilityContainer container, Inventory inventory, Component title) {
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

        addRenderableWidget(buttonUp1 = new ButtonArrow(this.leftPos + 73,  this.topPos + 83, Component.translatable("gui.cyclopscore.up"), button -> {
            if (startIndexPlayer > 0) startIndexPlayer--;
        }, ButtonArrow.Direction.NORTH));
        addRenderableWidget(buttonDown1 = new ButtonArrow(this.leftPos + 73,  this.topPos + 174, Component.translatable("gui.cyclopscore.down"), button -> {
            if (startIndexPlayer + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getPlayerAbilitiesCount())) startIndexPlayer++;
        }, ButtonArrow.Direction.SOUTH));
        addRenderableWidget(buttonUp2 = new ButtonArrow(this.leftPos + 88,  this.topPos + 83, Component.translatable("gui.cyclopscore.up"), button -> {
            if (startIndexItem > 0) startIndexItem--;
        }, ButtonArrow.Direction.NORTH));
        addRenderableWidget(buttonDown2 = new ButtonArrow(this.leftPos + 88,  this.topPos + 174, Component.translatable("gui.cyclopscore.down"), button -> {
            if (startIndexItem + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getItemAbilitiesCount())) startIndexItem++;
        }, ButtonArrow.Direction.SOUTH));

        Registry<IAbilityType> registry = AbilityHelpers.getRegistry(player.level().registryAccess());
        addRenderableWidget(buttonLeft = new ButtonArrow(this.leftPos + 76,  this.topPos + 130, Component.translatable("gui.cyclopscore.left"), button -> {
            if (canMoveToPlayer()) {
                EverlastingAbilities._instance.getPacketHandler().sendToServer(
                        new MoveAbilityPacket(registry, getSelectedItemAbilitySingle(), MoveAbilityPacket.Movement.TO_PLAYER));
                moveToPlayer();
            }
        }, ButtonArrow.Direction.WEST));
        addRenderableWidget(buttonRight = new ButtonArrow(this.leftPos + 90,  this.topPos + 130, Component.translatable("gui.cyclopscore.right"), button -> {
            if (canMoveFromPlayer()) {
                EverlastingAbilities._instance.getPacketHandler().sendToServer(
                        new MoveAbilityPacket(registry, getSelectedPlayerAbilitySingle(), MoveAbilityPacket.Movement.FROM_PLAYER));
                moveFromPlayer();
            }
        }, ButtonArrow.Direction.EAST));
    }

    @Override
    protected int getBaseYSize() {
        return 219;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (getMenu().getItemStack(player) == null) {
            return;
        }

        guiGraphics.drawString(this.font, player.getDisplayName().getString(), 8, 6, -1);
        guiGraphics.drawString(this.font, getMenu().getItemStack(player).getHoverName().getVisualOrderText(), 102, 6, -1);

        // Draw abilities
        drawAbilitiesTooltip(guiGraphics, 8, 83, getPlayerAbilities(), startIndexPlayer, mouseX, mouseY);
        drawAbilitiesTooltip(guiGraphics, 105, 83, getItemAbilities(), startIndexItem, mouseX, mouseY);
    }

    protected List<Ability> getPlayerAbilities() {
        List<Ability> abilities = getMenu().getPlayerAbilities();
        Collections.sort(abilities);
        return abilities;
    }

    protected List<Ability> getItemAbilities() {
        List<Ability> abilities = getMenu().getItemAbilities();
        Collections.sort(abilities);
        return abilities;
    }

    protected IMutableAbilityStore getPlayerAbilityStore() {
        return getMenu().getPlayerAbilityStore().orElse(null);
    }

    protected IMutableAbilityStore getItemAbilityStore() {
        return getMenu().getItemAbilityStore().orElse(null);
    }

    protected int getPlayerAbilitiesCount() {
        return getPlayerAbilities().size();
    }

    protected int getItemAbilitiesCount() {
        return getItemAbilities().size();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        if (getMenu().getItemStack(player) == null) {
            return;
        }

        buttonUp1.active = startIndexPlayer > 0;
        buttonDown1.active = startIndexPlayer + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getPlayerAbilitiesCount());
        buttonUp2.active = startIndexItem > 0;
        buttonDown2.active = startIndexItem + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getItemAbilitiesCount());

        buttonLeft.active = canMoveToPlayer();
        buttonRight.active = canMoveFromPlayer();
        buttonRight.active = canMoveFromPlayerByItem();

        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);

        int i = this.leftPos;
        int j = this.topPos;
        drawFancyBackground(guiGraphics, i + 8, j + 17, 66, 61, getPlayerAbilityStore());
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, i + 41, j + 75, 30, (float)(i + 41) - mouseX, (float)(j + 76 - 50) - mouseY, this.getMinecraft().player);
        drawXp(guiGraphics, i + 67, j + 70);
        RenderHelpers.drawScaledCenteredString(guiGraphics.pose(), guiGraphics.bufferSource(), font, "" + player.totalExperience, i + 62, j + 73, 0, 0.5F, Helpers.RGBToInt(40, 215, 40), false, Font.DisplayMode.NORMAL);
        drawFancyBackground(guiGraphics, i + 102, j + 17, 66, 61, getItemAbilityStore());
        drawItemOnScreen(i + 134, j + 46, 50, (float)(i + 134) - mouseX, (float)(j + 46 - 30) - mouseY, getMenu().getItemStack(this.getMinecraft().player));

        // Draw abilities
        drawAbilities(guiGraphics, this.leftPos + 8, this.topPos + 83, getPlayerAbilities(), startIndexPlayer, Integer.MAX_VALUE, absoluteSelectedIndexPlayer, mouseX, mouseY, canMoveFromPlayerByItem());
        drawAbilities(guiGraphics, this.leftPos + 105, this.topPos + 83, getItemAbilities(), startIndexItem, player.totalExperience, absoluteSelectedIndexItem, mouseX, mouseY, true);
    }

    public void drawFancyBackground(GuiGraphics guiGraphics, int x, int y, int width, int height, IAbilityStore abilityStore) {
        RenderType rendertype = Sheets.translucentItemSheet();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBuffer(bufferSource, rendertype, true, true);

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

        float f = (float)(Util.getMillis() % 9000L) / 9000.0F;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_DST_COLOR);
        drawTexturedModalRectColor(guiGraphics, vertexconsumer, x, y, (int) (0 + f * 256), 0, width, height, ((float) r) / 255, ((float) g) / 255, ((float) b) / 255, ((float) 255) / 255);
        drawTexturedModalRectColor(guiGraphics, vertexconsumer, x, y, (int) (-0 + f * 150), (int) (0 + f * 256), width, height, ((float) r) / 255, ((float) g) / 255, ((float) b) / 255, ((float) 255) / 255);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    protected void drawXp(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(texture, x, y, 0, 219, 5, 5);
    }

    private void drawAbilities(GuiGraphics guiGraphics, int x, int y, List<Ability> abilities, int startIndex, int playerXp,
                               int currentSelectedIndex, int mouseX, int mouseY, boolean canEdit) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            Ability ability = abilities.get(i + startIndex);

            // select box (+hover)
            if (canEdit) {
                boolean active = currentSelectedIndex == i + startIndex;
                boolean showActive = active || isPointInRegion(new Rectangle(x - this.leftPos, boxY - this.topPos, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT), new Point(mouseX, mouseY));
                if (showActive) {
                    drawFancyBackground(guiGraphics, x, boxY - 1, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT, null);
                }
            }

            // Name
            RenderHelpers.drawScaledCenteredString(guiGraphics.pose(), guiGraphics.bufferSource(), font,
                    Component.translatable(ability.getAbilityType().getTranslationKey())
                            .setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ability.getAbilityType().getRarity().color)))
                            .getString(),
                    x + 27, boxY + 7, 0, 1.0F, 50, -1, false, Font.DisplayMode.NORMAL);

            // Level
            RenderHelpers.drawScaledCenteredString(guiGraphics.pose(), guiGraphics.bufferSource(), font,
                    "" + ability.getLevel(),
                    x + 58, boxY + 5, 0, 0.8F, -1, false, Font.DisplayMode.NORMAL);

            // XP
            int requiredXp = ability.getAbilityType().getXpPerLevelScaled();
            if (playerXp < requiredXp) {
                RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1);
            } else {
                RenderSystem.setShaderColor(1, 1, 1, 1);
            }
            drawXp(guiGraphics, x + 57, boxY + 10);
            RenderHelpers.drawScaledCenteredString(guiGraphics.pose(), guiGraphics.bufferSource(), font,
                    "" + requiredXp,
                    x + 53, boxY + 13, 0, 0.5F, Helpers.RGBToInt(40, 215, 40), false, Font.DisplayMode.NORMAL);
        }
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private void drawAbilitiesTooltip(GuiGraphics guiGraphics, int x, int y, List<Ability> abilities, int startIndex, int mouseX, int mouseY) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            if(isPointInRegion(new Rectangle(x, boxY, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT), new Point(mouseX, mouseY))) {
                Ability ability = abilities.get(i + startIndex);
                List<Component> lines = Lists.newLinkedList();

                // Name
                lines.add(Component.translatable(ability.getAbilityType().getTranslationKey())
                        .setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ability.getAbilityType().getRarity().color))));

                // Level
                lines.add(Component.translatable("general.everlastingabilities.level", ability.getLevel(),
                        ability.getAbilityType().getMaxLevel() == -1 ? "Inf" : ability.getAbilityType().getMaxLevel()));

                // Description
                lines.add(Component.translatable(ability.getAbilityType().getUnlocalizedDescription())
                        .setStyle(Style.EMPTY.applyFormats(IInformationProvider.INFO_PREFIX_STYLES)));

                // Xp
                lines.add(Component.translatable("general.everlastingabilities.xp",
                        ability.getAbilityType().getXpPerLevelScaled(),
                        AbilityHelpers.getLevelForExperience(ability.getAbilityType().getXpPerLevelScaled()))
                        .setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN))));

                if (!AbilityHelpers.PREDICATE_ABILITY_ENABLED.test(ability.getAbilityType())) {
                    lines.add(Component.translatable("general.everlastingabilities.disabled")
                            .setStyle(Style.EMPTY
                                    .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))
                                    .withBold(true)));
                }

                drawTooltip(lines, guiGraphics.pose(), mouseX - this.leftPos, mouseY - this.topPos);
            }
        }
    }

    public void drawTexturedModalRectColor(GuiGraphics guiGraphics, VertexConsumer vertexbuffer, int x, int y, int textureX, int textureY, int width, int height, float r, float g, float b, float a) {
        RenderSystem.setShaderColor(r, g, b, a);
        guiGraphics.blit(RES_ITEM_GLINT, x, y, textureX, textureY, width, height);
    }

    public static void drawItemOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, ItemStack itemStack) {
        float f = (float)Math.atan((double)(mouseX / 40.0F));
        float f1 = (float)Math.atan((double)(mouseY / 40.0F));
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate((double)posX, (double)posY, 1050.0D);
        posestack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        posestack1.translate(0.0D, 0.0D, 1000.0D);
        posestack1.scale((float)scale, (float)scale, (float)scale);
        Quaternionf rotation = Axis.ZP.rotationDegrees(180.0F);
        Quaternionf cameraOrientationY = Axis.YP.rotationDegrees(-f * 40.0F);
        Quaternionf cameraOrientationX = Axis.XP.rotationDegrees(f1 * 20.0F);
        rotation.mul(cameraOrientationY);
        rotation.mul(cameraOrientationX);
        posestack1.mulPose(rotation);

        MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Lighting.setupFor3DItems();
        Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, posestack1, renderTypeBuffer, null, 0);
        Lighting.setupForFlatItems();
        renderTypeBuffer.endBatch();

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (isHovering(8, 83, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT * ABILITY_LIST_SIZE, mouseX, mouseY)) {
            if (scrollAmount > 0) {
                if (startIndexPlayer > 0)
                    startIndexPlayer--;
            } else if (scrollAmount < 0) {
                if (startIndexPlayer + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getPlayerAbilitiesCount()))
                    startIndexPlayer++;
            }
            return true;
        } else if (isHovering(105, 83, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT * ABILITY_LIST_SIZE, mouseX, mouseY)) {
            if (scrollAmount > 0) {
                if (startIndexItem > 0)
                    startIndexItem--;
            } else if (scrollAmount < 0) {
                if (startIndexItem + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getItemAbilitiesCount()))
                    startIndexItem++;
            }
            return true;
        }

        return false;
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
        if (!ability.isEmpty()) {
            ability = new Ability(ability.getAbilityType(), 1);
        }
        return ability;
    }

    public Ability getSelectedItemAbilitySingle() {
        Ability ability = getSelectedItemAbility();
        if (!ability.isEmpty()) {
            ability = new Ability(ability.getAbilityType(), 1);
        }
        return ability;
    }

    public Ability getSelectedPlayerAbility() {
        List<Ability> abilities = getPlayerAbilities();
        if (absoluteSelectedIndexPlayer >= 0 && absoluteSelectedIndexPlayer < abilities.size()) {
            return abilities.get(absoluteSelectedIndexPlayer);
        }
        return Ability.EMPTY;
    }

    public Ability getSelectedItemAbility() {
        List<Ability> abilities = getItemAbilities();
        if (absoluteSelectedIndexItem >= 0 && absoluteSelectedIndexItem < abilities.size()) {
            return abilities.get(absoluteSelectedIndexItem);
        }
        return Ability.EMPTY;
    }

    public boolean canMoveFromPlayer(Ability ability, Player player, IMutableAbilityStore target) {
        return !ability.isEmpty() && AbilityHelpers.canInsert(ability, target);
    }

    public boolean canMoveToPlayer(Ability ability, Player player) {
        return !ability.isEmpty() && AbilityHelpers.canInsertToPlayer(ability, player);
    }

    public boolean canMoveFromPlayerByItem() {
        return getMenu().getItem().canMoveFromPlayer();
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
        getMenu().moveFromPlayer(getSelectedPlayerAbilitySingle());
    }

    public void moveToPlayer() {
        getMenu().moveToPlayer(getSelectedItemAbilitySingle());
    }
}
