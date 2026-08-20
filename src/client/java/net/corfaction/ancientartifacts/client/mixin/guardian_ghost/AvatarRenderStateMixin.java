package net.corfaction.ancientartifacts.client.mixin.guardian_ghost;

import net.corfaction.ancientartifacts.api.GuardianGhostHolder;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements GuardianGhostHolder {
    @Unique
    private boolean ancientArtifacts$hasGuardianGhost;

    @Override
    public boolean ancientArtifacts$hasGuardianGhost() {
        return ancientArtifacts$hasGuardianGhost;
    }

    @Override
    public void ancientArtifacts$setGuardianGhost(boolean value) {
        ancientArtifacts$hasGuardianGhost = value;
    }
}
