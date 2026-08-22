package net.corfaction.ancientartifacts.mixin.artifact_slot;

import net.corfaction.ancientartifacts.api.PlayerInventoryAccess;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    @Unique
    private static final int ANCIENT_ARTIFACTS_EXTRA_SLOT = 46;

    @Inject(
            method = "handleSetCreativeModeSlot",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ancientArtifacts$handleExtraSlot(
            ServerboundSetCreativeModeSlotPacket packet,
            CallbackInfo ci
    ) {
        ServerGamePacketListenerImpl listener = (ServerGamePacketListenerImpl) (Object) this;

        if (packet.slotNum() != ANCIENT_ARTIFACTS_EXTRA_SLOT) {
            return;
        }

        ItemStack stack = packet.itemStack();

        if (!stack.isEmpty() && stack.getCount() > stack.getMaxStackSize()) {
            return;
        }

        PlayerInventoryAccess inventory = (PlayerInventoryAccess) listener.player.getInventory();
        inventory.ancientArtifacts$setExtraSlot(stack.copy());

        ci.cancel();
    }
}