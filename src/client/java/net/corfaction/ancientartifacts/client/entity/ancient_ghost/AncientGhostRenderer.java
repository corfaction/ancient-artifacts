package net.corfaction.ancientartifacts.client.entity.ancient_ghost;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.client.entity.ModEntityModelLayers;
import net.corfaction.ancientartifacts.entity.AncientGhost;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public final class AncientGhostRenderer extends MobRenderer<AncientGhost, LivingEntityRenderState, AncientGhostModel> {

    private static final Identifier TEXTURE =
            AncientArtifacts.id("textures/entity/ancient_ghost.png");

    public AncientGhostRenderer(EntityRendererProvider.Context context) {
        super(context, new AncientGhostModel(context.bakeLayer(ModEntityModelLayers.ANCIENT_GHOST)), 0.4F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public @NonNull Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }
}