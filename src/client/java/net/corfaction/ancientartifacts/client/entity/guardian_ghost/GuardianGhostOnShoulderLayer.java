package net.corfaction.ancientartifacts.client.entity.guardian_ghost;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.api.ArtifactRenderStateHolder;
import net.corfaction.ancientartifacts.client.entity.ModEntityModelLayers;
import net.corfaction.ancientartifacts.item.ModItemIds;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class GuardianGhostOnShoulderLayer
        extends RenderLayer<AvatarRenderState, PlayerModel> {

    private static final Identifier TEXTURE =
            AncientArtifacts.id("textures/entity/guardian_ghost.png");

    private final GuardianGhostModel model;

    public GuardianGhostOnShoulderLayer(
            RenderLayerParent<AvatarRenderState, PlayerModel> renderer,
            EntityModelSet modelSet
    ) {
        super(renderer);

        this.model = new GuardianGhostModel(
                modelSet.bakeLayer(ModEntityModelLayers.GUARDIAN_GHOST)
        );
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            AvatarRenderState state,
            float yRot,
            float xRot
    ) {
        ItemStack artifact =
                ((ArtifactRenderStateHolder) state)
                        .ancientArtifacts$getArtifact();

        if (artifact.isEmpty()) {
            return;
        }

        if (!artifact.is(ModItemIds.GUARDIAN_TALISMAN)) {
            return;
        }

        submitOnShoulder(
                poseStack,
                submitNodeCollector,
                lightCoords,
                state,
                yRot,
                xRot
        );
    }

    private void submitOnShoulder(
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            AvatarRenderState playerState,
            float yRot,
            float xRot
    ) {
        poseStack.pushPose();

        float time = (System.currentTimeMillis() % 3000L) / 1000.0F;
        float hoverOffset =
                (float) Math.sin(time * Math.PI * 2.0D) * 0.05F;

        float rotationOffset =
                (float) Math.sin(time * Math.PI * 2.0D) * 2.0F;

        float baseY =
                (playerState.isCrouching ? -1.3F : -1.5F) - 0.2F;

        poseStack.translate(
                0.6F,
                baseY + hoverOffset,
                -0.0625F
        );

        poseStack.rotateAround(
                Axis.XP.rotationDegrees(rotationOffset),
                0.0F,
                0.0F,
                0.0F
        );

        LivingEntityRenderState ghostState =
                new LivingEntityRenderState();

        ghostState.yRot = yRot;
        ghostState.xRot = xRot;

        submitNodeCollector.submitModel(
                model,
                ghostState,
                poseStack,
                TEXTURE,
                lightCoords,
                OverlayTexture.NO_OVERLAY,
                playerState.outlineColor,
                null
        );

        poseStack.popPose();
    }
}