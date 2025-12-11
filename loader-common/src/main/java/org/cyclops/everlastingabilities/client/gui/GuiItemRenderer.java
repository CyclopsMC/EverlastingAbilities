package org.cyclops.everlastingabilities.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.cyclops.everlastingabilities.Reference;
import org.joml.Vector3f;

/**
 * Based on GuiEntityRenderer
 * @author rubensworks
 */
public class GuiItemRenderer extends PictureInPictureRenderer<GuiItemRenderState>  {
    private final EntityRenderDispatcher entityRenderDispatcher;

    public GuiItemRenderer(MultiBufferSource.BufferSource bufferSource, EntityRenderDispatcher entityRenderDispatcher) {
        super(bufferSource);
        this.entityRenderDispatcher = entityRenderDispatcher;
    }

    @Override
    public Class<GuiItemRenderState> getRenderStateClass() {
        return GuiItemRenderState.class;
    }

    @Override
    protected void renderToTexture(GuiItemRenderState renderState, PoseStack poseStack) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        Vector3f vector3f = renderState.translation();
        poseStack.translate(vector3f.x, vector3f.y, vector3f.z);
        poseStack.scale(renderState.scale(), renderState.scale(), 1);
        poseStack.mulPose(renderState.rotation());
        FeatureRenderDispatcher featurerenderdispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        CameraRenderState camerarenderstate = new CameraRenderState();

        this.entityRenderDispatcher
                .submit(renderState.renderState(), camerarenderstate, 0.0, 0.0, 0.0, poseStack, featurerenderdispatcher.getSubmitNodeStorage());
        featurerenderdispatcher.renderAllFeatures();
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
