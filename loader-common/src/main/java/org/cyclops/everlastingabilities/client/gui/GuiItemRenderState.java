package org.cyclops.everlastingabilities.client.gui;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

/**
 * Derived from {@link net.minecraft.client.gui.render.state.pip.GuiEntityRenderState}.
 * @author rubensworks
 */
public record GuiItemRenderState(
        ItemStack itemStack,
        Vector3f translation,
        Quaternionf rotation,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {

    public GuiItemRenderState(
            ItemStack itemStack,
            Vector3f translation,
            Quaternionf rotation,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            @Nullable ScreenRectangle scissorArea
    ) {
        this(
                itemStack,
                translation,
                rotation,
                x0,
                y0,
                x1,
                y1,
                scale,
                scissorArea,
                PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea)
        );
    }
}
