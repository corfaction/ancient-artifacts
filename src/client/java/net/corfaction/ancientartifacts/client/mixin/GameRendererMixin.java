package net.corfaction.ancientartifacts.client.mixin;

import net.corfaction.ancientartifacts.client.ArchaeologistEyeRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "close", at = @At("RETURN"))
    private void ancientArtifacts$close(CallbackInfo ci) {
        ArchaeologistEyeRenderer.close();
    }
}