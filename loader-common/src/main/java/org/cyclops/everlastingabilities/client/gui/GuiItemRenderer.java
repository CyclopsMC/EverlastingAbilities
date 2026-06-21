package org.cyclops.everlastingabilities.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.cyclops.everlastingabilities.Reference;
import org.joml.Vector3f;

/**
 * Based on GuiEntityRenderer
 * @author rubensworks
 */
public class GuiItemRenderer extends PictureInPictureRenderer<GuiItemRenderState>  {
    private final EntityRenderDispatcher entityRenderDispatcher;

    public GuiItemRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super();
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    @Override
    public Class<GuiItemRenderState> getRenderStateClass() {
        return GuiItemRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiItemRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        Minecraft.getInstance().gameRenderer.lighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        Vector3f vector3f = renderState.translation();
        poseStack.translate(vector3f.x, vector3f.y, vector3f.z);
        poseStack.scale(renderState.scale(), renderState.scale(), 1);
        poseStack.mulPose(renderState.rotation());
        CameraRenderState camerarenderstate = new CameraRenderState();

        this.entityRenderDispatcher
                .submit(renderState.renderState(), camerarenderstate, 0.0, 0.0, 0.0, poseStack, submitNodeCollector);
    }

    @Override
    protected float getTranslateY(int p_415687_, int p_415953_) {
        return (float)p_415687_ / 2.0F;
    }

    @Override
    protected String getTextureLabel() {
        return Reference.MOD_ID +  ":item";
    }

}
