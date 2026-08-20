package net.corfaction.ancientartifacts.client.mixin.guardian_ghost;

import net.corfaction.ancientartifacts.api.GuardianGhostHolder;
import net.corfaction.ancientartifacts.client.entity.guardian_ghost.GuardianGhostOnShoulderLayer;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>, AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("HEAD"))
    public void ancientArtifacts$extractHasGuardian(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        
        if(entity instanceof Player player) {

            GuardianGhostHolder state1 = (GuardianGhostHolder) state;

            state1.ancientArtifacts$setGuardianGhost(((GuardianGhostHolder) player).ancientArtifacts$hasGuardianGhost());

        }
    }

    @SuppressWarnings({"unchecked"})
    @Inject(method = "<init>", at = @At("TAIL"))
    private void ancientArtifacts$addGuardianGhostOnShoulderLayer(
            EntityRendererProvider.Context context,
            boolean slimSteve,
            CallbackInfo ci
    ) {
        ((LivingEntityRendererInvoker<S, M>) this)
                .ancientArtifacts$addLayer(
                        (RenderLayer<S, M>) new GuardianGhostOnShoulderLayer(
                                (AvatarRenderer) (Object) this,
                                context.getModelSet()
                        )
                );
    }
}
