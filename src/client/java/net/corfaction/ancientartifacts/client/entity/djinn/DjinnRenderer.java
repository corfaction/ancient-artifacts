package net.corfaction.ancientartifacts.client.entity.djinn;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.entity.Djinn;
import net.minecraft.client.model.animal.allay.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.AllayRenderState;
import net.minecraft.resources.Identifier;

public final class DjinnRenderer extends MobRenderer<Djinn, AllayRenderState, AllayModel> {

    private static final Identifier TEXTURE = AncientArtifacts.id("textures/entity/djinn.png");

    public DjinnRenderer(EntityRendererProvider.Context context) {
        super(context, new AllayModel(context.bakeLayer(ModelLayers.ALLAY)), 0.4F);
    }

    @Override
    public AllayRenderState createRenderState() {
        return new AllayRenderState();
    }

    @Override
    public Identifier getTextureLocation(AllayRenderState state) {
        return TEXTURE;
    }
}