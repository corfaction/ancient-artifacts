package net.corfaction.ancientartifacts.mixin.artifact_slot;

import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryLoadMixin {

    @Inject(
            method = "load",
            at = @At("HEAD")
    )
    private void ancientArtifacts$loadExtraSlot(
            ValueInput.TypedInputList<ItemStackWithSlot> input,
            CallbackInfo ci
    ) {
        // Нельзя повторно итерировать input:
        // его обработает сам vanilla Inventory.load().
    }
}