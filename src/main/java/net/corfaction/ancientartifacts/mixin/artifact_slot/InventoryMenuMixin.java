package net.corfaction.ancientartifacts.mixin.artifact_slot;

import net.corfaction.ancientartifacts.inventory.ExtraPlayerSlot;
import net.corfaction.ancientartifacts.inventory.ExtraSlotContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ancientArtifacts$addExtraSlot(
            Inventory inventory,
            boolean active,
            Player owner,
            CallbackInfo ci
    ) {
        InventoryMenu menu = (InventoryMenu) (Object) this;

        ExtraSlotContainer container =
                new ExtraSlotContainer(inventory);

        ((AbstractContainerMenuInvoker)menu).ancientArtifacts$addArtifactSlot(
                new ExtraPlayerSlot(
                        container,
                        77,
                        8
                )
        );
    }
}