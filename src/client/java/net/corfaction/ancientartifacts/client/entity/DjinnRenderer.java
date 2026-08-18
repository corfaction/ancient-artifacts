package net.corfaction.ancientartifacts.client.entity;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.entity.Djinn;
import net.minecraft.client.model.animal.allay.AllayModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class DjinnRenderer extends MobRenderer<Djinn, DjinnRenderState, AllayModel> {

    private static final Identifier ALLAY_TEXTURE = AncientArtifacts.id("textures/entity/djinn.png");

    public DjinnRenderer(EntityRendererProvider.Context context) {
        super(context, new AllayModel(context.bakeLayer(ModelLayers.ALLAY)), 0.4F);
    }

    @Override
    public DjinnRenderState createRenderState() {
        return new DjinnRenderState();
    }

    @Override
    public Identifier getTextureLocation(DjinnRenderState state) {
        return ALLAY_TEXTURE;
    }
}