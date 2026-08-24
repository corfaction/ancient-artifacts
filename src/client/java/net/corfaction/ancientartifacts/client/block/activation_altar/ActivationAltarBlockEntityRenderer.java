package net.corfaction.ancientartifacts.client.block.activation_altar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.corfaction.ancientartifacts.block.entity.ActivationAltarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class ActivationAltarBlockEntityRenderer
        implements BlockEntityRenderer<
        ActivationAltarBlockEntity,
        ActivationAltarRenderState> {

    private final ItemModelResolver itemModelResolver;

    public ActivationAltarBlockEntityRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        itemModelResolver = Minecraft.getInstance().getItemModelResolver();
    }

    @Override
    public ActivationAltarRenderState createRenderState() {
        return new ActivationAltarRenderState();
    }

    @Override
    public void extractRenderState(
            ActivationAltarBlockEntity altar,
            ActivationAltarRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                altar,
                state,
                partialTicks,
                cameraPosition,
                breakProgress
        );

        if (altar.getLevel() == null) {
            state.item.clear();
            return;
        }

        itemModelResolver.updateForTopItem(
                state.item,
                altar.getItem(),
                ItemDisplayContext.FIXED,
                altar.getLevel(),
                null,
                0
        );

        state.lightCoords = LightCoordsUtil.getLightCoords(
                altar.getLevel(),
                altar.getBlockPos().above()
        );

        state.time = altar.getLevel().getGameTime() + partialTicks;
    }

    @Override
    public void submit(
            ActivationAltarRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (state.item.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        float bob = (float) Math.sin(state.time * 0.08F) * 0.04F;
        float rotation = state.time * 0.04F;

        poseStack.translate(
                0.5F,
                1.5F + bob,
                0.5F
        );

        poseStack.mulPose(Axis.YP.rotation(rotation));

        state.item.submit(
                poseStack,
                submitNodeCollector,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );

        poseStack.popPose();
    }
}