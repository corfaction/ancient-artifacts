package net.corfaction.ancientartifacts.api;

import net.minecraft.world.item.ItemStack;

public interface ArtifactRenderStateHolder {

    ItemStack ancientArtifacts$getArtifact();

    void ancientArtifacts$setArtifact(ItemStack stack);
}