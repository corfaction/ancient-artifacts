package net.corfaction.ancientartifacts.client.mixin.guardian_ghost;

import net.corfaction.ancientartifacts.api.ArtifactRenderStateHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public final class AvatarRenderStateMixin implements ArtifactRenderStateHolder {

    @Unique
    private ItemStack ancientArtifacts$artifact = ItemStack.EMPTY;

    @Override
    public ItemStack ancientArtifacts$getArtifact() {
        return ancientArtifacts$artifact;
    }

    @Override
    public void ancientArtifacts$setArtifact(ItemStack stack) {
        ancientArtifacts$artifact = stack;
    }
}