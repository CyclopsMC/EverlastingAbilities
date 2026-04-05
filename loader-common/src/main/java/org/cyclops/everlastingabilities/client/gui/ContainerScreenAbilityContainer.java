package org.cyclops.everlastingabilities.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.cyclopscore.client.gui.component.button.ButtonArrow;
import org.cyclops.cyclopscore.client.gui.container.ContainerScreenExtended;
import org.cyclops.cyclopscore.helper.IModHelpers;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.inventory.container.ContainerAbilityContainer;
import org.cyclops.everlastingabilities.network.packet.MoveAbilityPacket;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Gui for the ability container.
 * @author rubensworks
 */
public class ContainerScreenAbilityContainer extends ContainerScreenExtended<ContainerAbilityContainer> {

    private static final Identifier RES_ITEM_GLINT = Identifier.withDefaultNamespace("textures/misc/enchanted_glint_item.png");
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

    private final IModHelpers modHelpers;

    public ContainerScreenAbilityContainer(ContainerAbilityContainer container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.player = inventory.player;
        this.modHelpers = IModHelpers.get();
    }

    @Override
    protected Identifier constructGuiTexture() {
        return Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/ability_totem.png");
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

        Registry<IAbilityType> registry = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getRegistry(player.level().registryAccess());
        addRenderableWidget(buttonLeft = new ButtonArrow(this.leftPos + 76,  this.topPos + 130, Component.translatable("gui.cyclopscore.left"), button -> {
            if (canMoveToPlayer()) {
                EverlastingAbilitiesInstance.MOD.getPacketHandler().sendToServer(
                        new MoveAbilityPacket(registry, getSelectedItemAbilitySingle(), MoveAbilityPacket.Movement.TO_PLAYER));
                moveToPlayer();
            }
        }, ButtonArrow.Direction.WEST));
        addRenderableWidget(buttonRight = new ButtonArrow(this.leftPos + 90,  this.topPos + 130, Component.translatable("gui.cyclopscore.right"), button -> {
            if (canMoveFromPlayer()) {
                EverlastingAbilitiesInstance.MOD.getPacketHandler().sendToServer(
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
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (getMenu().getItemStack(player) == null) {
            return;
        }

        guiGraphics.text(this.font, player.getDisplayName().getString(), 8, 6, -1);
        guiGraphics.text(this.font, getMenu().getItemStack(player).getHoverName().getVisualOrderText(), 102, 6, -1);

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
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (getMenu().getItemStack(player) == null) {
            return;
        }

        if (buttonUp1 != null) {
            buttonUp1.active = startIndexPlayer > 0;
            buttonDown1.active = startIndexPlayer + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getPlayerAbilitiesCount());
            buttonUp2.active = startIndexItem > 0;
            buttonDown2.active = startIndexItem + ABILITY_LIST_SIZE < Math.max(ABILITY_LIST_SIZE, getItemAbilitiesCount());

            buttonLeft.active = canMoveToPlayer();
            buttonRight.active = canMoveFromPlayer();
            buttonRight.active = canMoveFromPlayerByItem();
        }

        super.extractBackground(guiGraphics, mouseX, mouseY, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;
        drawFancyBackground(guiGraphics, i + 8, j + 17, 66, 61, getPlayerAbilityStore());
        // i + 41, j + 75, i + 41 + 66, j + 75 + 61
        InventoryScreen.extractEntityInInventoryFollowsMouse(guiGraphics, i + 26 - 8, j + 8 + 3, i + 75 - 8, j + 78 + 3, 30, 0.0625F, mouseX, mouseY, this.minecraft.player);
        drawXp(guiGraphics, i + 67, j + 70, false);
        IModHelpers.get().getRenderHelpers().drawScaledCenteredString(guiGraphics, font, "" + player.totalExperience, i + 62, j + 73, 0, 0.5F, IModHelpers.get().getBaseHelpers().RGBAToInt(40, 215, 40, 255), false, Font.DisplayMode.NORMAL);
        drawFancyBackground(guiGraphics, i + 102, j + 17, 66, 61, getItemAbilityStore());
        drawItemOnScreen(guiGraphics, i + 98, j + 17, 70, 61, 10, mouseX - 16, mouseY, getMenu().getItemStack(this.minecraft.player));

        // Draw abilities
        drawAbilities(guiGraphics, this.leftPos + 8, this.topPos + 83, getPlayerAbilities(), startIndexPlayer, Integer.MAX_VALUE, absoluteSelectedIndexPlayer, mouseX, mouseY, canMoveFromPlayerByItem());
        drawAbilities(guiGraphics, this.leftPos + 105, this.topPos + 83, getItemAbilities(), startIndexItem, player.totalExperience, absoluteSelectedIndexItem, mouseX, mouseY, true);
    }

    public void drawFancyBackground(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, IAbilityStore abilityStore) {
        int r = 140;
        int g = 140;
        int b = 140;
        if (abilityStore != null) {
            if (abilityStore.getAbilityTypes().isEmpty()) return;
            Triple<Integer, Integer, Integer> color = EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getAverageRarityColor(abilityStore);
            r = color.getLeft();
            g = color.getMiddle();
            b = color.getRight();
        }

        float f = (float)(Util.getMillis() % 9000L) / 9000.0F;

        drawTexturedModalRectColor(guiGraphics, x, y, (int) (0 + f * 256), 0, width, height, ((float) r) / 255, ((float) g) / 255, ((float) b) / 255, ((float) 255) / 255);
        drawTexturedModalRectColor(guiGraphics, x, y, (int) (-0 + f * 150), (int) (0 + f * 256), width, height, ((float) r) / 255, ((float) g) / 255, ((float) b) / 255, ((float) 255) / 255);
    }

    protected void drawXp(GuiGraphicsExtractor guiGraphics, int x, int y, boolean reducedIntensity) {
        modHelpers.getRenderHelpers().blitColored(guiGraphics, texture, x, y, 0, 219, 5, 5, reducedIntensity ? 0.3F : 1F, reducedIntensity ? 0.3F : 1F, reducedIntensity ? 0.3F : 1F, 1F);
    }

    private void drawAbilities(GuiGraphicsExtractor guiGraphics, int x, int y, List<Ability> abilities, int startIndex, int playerXp,
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
            modHelpers.getRenderHelpers().drawScaledCenteredString(guiGraphics, font,
                    Component.translatable(ability.getAbilityType().getTranslationKey())
                            .setStyle(Style.EMPTY.withColor(ability.getAbilityType().getRarity().color()))
                            .getString(),
                    x + 27, boxY + 7, 0, 1.0F, 50, ARGB.opaque(ability.getAbilityType().getRarity().color().getColor()), false, Font.DisplayMode.NORMAL);

            // Level
            modHelpers.getRenderHelpers().drawScaledCenteredString(guiGraphics, font,
                    "" + ability.getLevel(),
                    x + 58, boxY + 5, 0, 0.8F, -1, false, Font.DisplayMode.NORMAL);

            // XP
            int requiredXp = ability.getAbilityType().getXpPerLevelScaled();
            drawXp(guiGraphics, x + 57, boxY + 10, playerXp < requiredXp);
            modHelpers.getRenderHelpers().drawScaledCenteredString(guiGraphics, font,
                    "" + requiredXp,
                    x + 53, boxY + 13, 0, 0.5F, modHelpers.getBaseHelpers().RGBAToInt(40, 215, 40, 255), false, Font.DisplayMode.NORMAL);
        }
    }

    private void drawAbilitiesTooltip(GuiGraphicsExtractor guiGraphics, int x, int y, List<Ability> abilities, int startIndex, int mouseX, int mouseY) {
        int maxI = Math.min(ABILITY_LIST_SIZE, abilities.size() - startIndex);
        for (int i = 0; i < maxI; i++) {
            int boxY = y + i * ABILITY_BOX_HEIGHT;
            if(isPointInRegion(new Rectangle(x, boxY, ABILITY_BOX_WIDTH, ABILITY_BOX_HEIGHT), new Point(mouseX, mouseY))) {
                Ability ability = abilities.get(i + startIndex);
                List<Component> lines = Lists.newLinkedList();

                // Name
                lines.add(Component.translatable(ability.getAbilityType().getTranslationKey())
                        .setStyle(Style.EMPTY.withColor(ability.getAbilityType().getRarity().color().getColor())));

                // Level
                lines.add(Component.translatable("general.everlastingabilities.level", ability.getLevel(),
                        ability.getAbilityType().getMaxLevel() == -1 ? "Inf" : ability.getAbilityType().getMaxLevel())
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

                // Description
                lines.add(Component.translatable(ability.getAbilityType().getUnlocalizedDescription())
                        .setStyle(Style.EMPTY.applyFormats(ChatFormatting.RED)));

                // Xp
                lines.add(Component.translatable("general.everlastingabilities.xp",
                        ability.getAbilityType().getXpPerLevelScaled(),
                        EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getLevelForExperience(ability.getAbilityType().getXpPerLevelScaled()))
                        .setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN))));

                if (!EverlastingAbilitiesInstance.MOD.getAbilityHelpers().getPredicateAbilityEnabled().test(ability.getAbilityTypeHolder())) {
                    lines.add(Component.translatable("general.everlastingabilities.disabled")
                            .setStyle(Style.EMPTY
                                    .withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_RED))
                                    .withBold(true)));
                }

                guiGraphics.setTooltipForNextFrame(font, lines, Optional.empty(), mouseX - this.leftPos, mouseY - this.topPos);
            }
        }
    }

    public void drawTexturedModalRectColor(GuiGraphicsExtractor guiGraphics, int x, int y, int textureX, int textureY, int width, int height, float r, float g, float b, float a) {
        guiGraphics.blit(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, RES_ITEM_GLINT, x, y, textureX, textureY, width, height, 256, 256, ARGB.colorFromFloat(a, r, g, b));
    }

    public static void drawItemOnScreen(GuiGraphicsExtractor guiGraphics, int x1, int y1, int width, int height, int scale, float mouseX, float mouseY, ItemStack itemStack) {
        int x2 = x1 + width;
        int y2 = y1 + height;

        float averageX = (float)(x1 + x2) / 2.0F;
        float averageY = (float)(y1 + y2) / 2.0F;
        guiGraphics.enableScissor(x1, y1, x2, y2);
        float rotationY = (float)Math.atan((averageX - mouseX) / 40.0F);
        float rotationX = (float)Math.atan((averageY - mouseY) / 40.0F);
        Quaternionf rotation = (new Quaternionf())
                .rotateZ((float)Math.PI)
                .rotateY(-182.0F + rotationY * -40.0F * ((float)Math.PI / 180F))
                .rotateX((rotationX) * 25.0F * ((float)Math.PI / 180F));

        ItemEntity itemEntity = new ItemEntity(Minecraft.getInstance().level, 0, 0, 0, itemStack);
        EntityRenderer<? super ItemEntity, ?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(itemEntity);
        ItemEntityRenderState entityRenderState = (ItemEntityRenderState) renderer.createRenderState(itemEntity, 0);
        entityRenderState.bobOffset = 0;
        guiGraphics.guiRenderState.addPicturesInPictureState(
                new GuiItemRenderState(
                        entityRenderState,
                        itemStack,
                        new Vector3f(0.5F, 4.0F, 0.0F),
                        rotation,
                        x1,
                        y1,
                        x2,
                        y2,
                        scale,
                        guiGraphics.scissorStack.peek()
                )
        );

        guiGraphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent evt, boolean isDoubleClick) {
        int newSelectedPlayer = canMoveFromPlayerByItem() ? clickAbilities(8, 83, getPlayerAbilities(), startIndexPlayer, absoluteSelectedIndexPlayer, evt.x(), evt.y()) : -2;
        int newSelectedItem = clickAbilities(105, 83, getItemAbilities(), startIndexItem, absoluteSelectedIndexItem, evt.x(), evt.y());

        if (newSelectedPlayer >= -1) {
            absoluteSelectedIndexPlayer = newSelectedPlayer;
        }
        if (newSelectedItem >= -1) {
            absoluteSelectedIndexItem = newSelectedItem;
        }

        if (newSelectedPlayer < 0 && newSelectedItem < 0) {
            super.mouseClicked(evt, isDoubleClick);
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double mouseZ, double scrollAmount) {
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
            ability = new Ability(ability.getAbilityTypeHolder(), 1);
        }
        return ability;
    }

    public Ability getSelectedItemAbilitySingle() {
        Ability ability = getSelectedItemAbility();
        if (!ability.isEmpty()) {
            ability = new Ability(ability.getAbilityTypeHolder(), 1);
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
        return !ability.isEmpty() && EverlastingAbilitiesInstance.MOD.getAbilityHelpers().canInsert(ability, target);
    }

    public boolean canMoveToPlayer(Ability ability, Player player) {
        return !ability.isEmpty() && EverlastingAbilitiesInstance.MOD.getAbilityHelpers().canInsertToPlayer(ability, player);
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
