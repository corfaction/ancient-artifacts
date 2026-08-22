package net.corfaction.ancientartifacts.client.mixin.guardian_ghost;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererInvoker<
        S extends LivingEntityRenderState,
        M extends EntityModel<? super S>> {

    @Invoker("addLayer")
    boolean ancientArtifacts$addLayer(RenderLayer<S, M> layer);
}