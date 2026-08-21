package net.corfaction.ancientartifacts.api;

import net.minecraft.world.item.ItemStack;

public interface PlayerInventoryAccess {
    ItemStack ancientArtifacts$getExtraSlot();

    void ancientArtifacts$setExtraSlot(ItemStack stack);
}
