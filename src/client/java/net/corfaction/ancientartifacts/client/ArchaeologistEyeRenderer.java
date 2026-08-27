package net.corfaction.ancientartifacts.client;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.corfaction.ancientartifacts.AncientArtifacts;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public final class ArchaeologistEyeRenderer {

    private static final RenderPipeline THROUGH_WALLS = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(
                            AncientArtifacts.MOD_ID,
                            "pipeline/archaeologist_eye"
                    ))
                    .withDepthStencilState(Optional.empty())
                    .build()
    );

    private static final StagedVertexBuffer BUFFER = new StagedVertexBuffer(
            () -> "Archaeologist Eye Buffer",
            RenderType.SMALL_BUFFER_SIZE
    );

    private static final Vector4f COLOR_MODULATOR = new Vector4f(1F, 1F, 1F, 1F);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();

    private static List<BlockPos> positions = List.of();
    private static int ticksLeft;

    private ArchaeologistEyeRenderer() {
    }

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(context -> extract());
        LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN.register(
                ArchaeologistEyeRenderer::render
        );
    }

    public static void show(List<BlockPos> positions, int duration) {
        ArchaeologistEyeRenderer.positions = List.copyOf(positions);
        ticksLeft = duration;
    }

    private static void extract() {
        if (ticksLeft > 0) {
            ticksLeft--;
        }

        if (ticksLeft <= 0) {
            positions = List.of();
        }
    }

    private static void render(LevelRenderContext context) {
        if (positions.isEmpty()) {
            return;
        }

        VertexFormat format = THROUGH_WALLS.getVertexFormatBinding(0);

        if (format == null) {
            return;
        }

        PrimitiveTopology primitive = THROUGH_WALLS.getPrimitiveTopology();
        StagedVertexBuffer.Draw draw = BUFFER.appendDraw(
                format,
                primitive,
                primitive == PrimitiveTopology.QUADS
                        ? RenderSystem.getProjectionType().vertexSorting()
                        : null
        );

        renderBoxes(context, draw);
        BUFFER.upload();

        StagedVertexBuffer.ExecuteInfo info = BUFFER.getExecuteInfo(draw);

        if (info != null) {
            draw(Minecraft.getInstance(), info);
        }

        BUFFER.endFrame();
    }

    private static void renderBoxes(
            LevelRenderContext context,
            StagedVertexBuffer.Draw draw
    ) {
        PoseStack poseStack = context.poseStack();
        Vec3 camera = context.levelState().cameraRenderState.pos;

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        VertexConsumer builder = BUFFER.getVertexBuilder(draw);
        float alpha = getAlpha();

        for (BlockPos pos : positions) {
            renderBox(
                    poseStack.last().pose(),
                    builder,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    pos.getX() + 1.0F,
                    pos.getY() + 1.0F,
                    pos.getZ() + 1.0F,
                    0.76F,
                    0.44F,
                    0.18F,
                    alpha
            );
        }

        poseStack.popPose();
    }

    private static float getAlpha() {
        Minecraft client = Minecraft.getInstance();
        long time = client.level != null ? client.level.getGameTime() : 0;
        return 0.35F + (float) Math.sin(time * 0.15F) * 0.10F;
    }

    private static void renderBox(
            Matrix4fc matrix,
            VertexConsumer buffer,
            float minX,
            float minY,
            float minZ,
            float maxX,
            float maxY,
            float maxZ,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        vertex(matrix, buffer, minX, minY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, minY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, maxY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, maxY, maxZ, red, green, blue, alpha);

        vertex(matrix, buffer, maxX, minY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, minY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, maxY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, maxY, minZ, red, green, blue, alpha);

        vertex(matrix, buffer, minX, minY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, minY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, maxY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, maxY, minZ, red, green, blue, alpha);

        vertex(matrix, buffer, maxX, minY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, minY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, maxY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, maxY, maxZ, red, green, blue, alpha);

        vertex(matrix, buffer, minX, maxY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, maxY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, maxY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, maxY, minZ, red, green, blue, alpha);

        vertex(matrix, buffer, minX, minY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, minY, minZ, red, green, blue, alpha);
        vertex(matrix, buffer, maxX, minY, maxZ, red, green, blue, alpha);
        vertex(matrix, buffer, minX, minY, maxZ, red, green, blue, alpha);
    }

    private static void vertex(
            Matrix4fc matrix,
            VertexConsumer buffer,
            float x,
            float y,
            float z,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        buffer.addVertex(matrix, x, y, z).setColor(red, green, blue, alpha);
    }

    private static void draw(
            Minecraft client,
            StagedVertexBuffer.ExecuteInfo info
    ) {
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(
                RenderSystem.getModelViewMatrixCopy(),
                COLOR_MODULATOR,
                MODEL_OFFSET,
                TEXTURE_MATRIX
        );

        RenderTarget target = client.gameRenderer.mainRenderTarget();
        GpuTextureView colorTexture = target.getColorTextureView();

        if (colorTexture == null) {
            return;
        }

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> AncientArtifacts.MOD_ID + " archaeologist eye",
                        colorTexture,
                        Optional.empty(),
                        target.getDepthTextureView(),
                        OptionalDouble.empty()
                )) {
            renderPass.setPipeline(THROUGH_WALLS);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setVertexBuffer(0, info.vertexBuffer().slice());
            renderPass.setIndexBuffer(info.indexBuffer(), info.indexType());
            renderPass.drawIndexed(
                    info.indexCount(),
                    1,
                    info.firstIndex(),
                    info.baseVertex(),
                    0
            );
        }
    }

    public static void close() {
        BUFFER.close();
    }
}