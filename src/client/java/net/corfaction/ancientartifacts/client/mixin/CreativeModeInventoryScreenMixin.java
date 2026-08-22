package net.corfaction.ancientartifacts.client.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @ModifyArgs(
            method = "selectTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$SlotWrapper;<init>(Lnet/minecraft/world/inventory/Slot;III)V"
            )
    )
    private void ancientArtifacts$moveExtraSlot(Args args) {
        if (args.get(1) instanceof Integer index && index == 46) {
            args.set(2, 127);
            args.set(3, 20);
        }
    }
}