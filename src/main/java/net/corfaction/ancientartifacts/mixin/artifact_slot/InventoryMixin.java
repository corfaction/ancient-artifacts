package net.corfaction.ancientartifacts.mixin.artifact_slot;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.corfaction.ancientartifacts.api.PlayerInventoryAccess;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public class InventoryMixin implements PlayerInventoryAccess {

    @Unique
    private static final int ANCIENT_ARTIFACTS_EXTRA_SLOT = 36;

    @Unique
    private ItemStack ancientArtifacts$extraSlot = ItemStack.EMPTY;

    @Override
    public ItemStack ancientArtifacts$getExtraSlot() {
        return ancientArtifacts$extraSlot;
    }

    @Override
    public void ancientArtifacts$setExtraSlot(ItemStack stack) {
        ancientArtifacts$extraSlot = stack;
        ((Inventory) (Object) this).setChanged();
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void ancientArtifacts$saveExtraSlot(
            ValueOutput.TypedOutputList<ItemStackWithSlot> output,
            CallbackInfo ci
    ) {
        if (!ancientArtifacts$extraSlot.isEmpty()) {
            output.add(new ItemStackWithSlot(
                    ANCIENT_ARTIFACTS_EXTRA_SLOT,
                    ancientArtifacts$extraSlot
            ));
        }
    }

    @WrapOperation(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/ItemStackWithSlot;isValidInContainer(I)Z"
            )
    )
    private boolean ancientArtifacts$allowExtraSlot(
            ItemStackWithSlot item,
            int size,
            Operation<Boolean> original
    ) {
        return item.slot() == ANCIENT_ARTIFACTS_EXTRA_SLOT
                || original.call(item, size);
    }

    @WrapOperation(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;setItem(ILnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void ancientArtifacts$loadExtraSlot(
            Inventory inventory,
            int slot,
            ItemStack stack,
            Operation<Void> original
    ) {
        if (slot == ANCIENT_ARTIFACTS_EXTRA_SLOT) {
            ancientArtifacts$extraSlot = stack;
        } else {
            original.call(inventory, slot, stack);
        }
    }

    @Inject(method = "dropAll", at = @At("TAIL"))
    private void ancientArtifacts$dropExtraSlot(CallbackInfo ci) {
        Inventory inventory = (Inventory) (Object) this;

        if (!ancientArtifacts$extraSlot.isEmpty()) {
            inventory.player.drop(ancientArtifacts$extraSlot, true, false);
            ancientArtifacts$extraSlot = ItemStack.EMPTY;
        }
    }

    @Inject(method = "clearContent", at = @At("TAIL"))
    private void ancientArtifacts$clearExtraSlot(CallbackInfo ci) {
        ancientArtifacts$extraSlot = ItemStack.EMPTY;
    }
}