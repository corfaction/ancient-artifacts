package net.corfaction.ancientartifacts.mixin.phantom_sweep;

import net.corfaction.ancientartifacts.api.PlayerInventoryAccess;
import net.corfaction.ancientartifacts.item.ModItems;
import net.corfaction.ancientartifacts.network.PhantomSweepManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void ancientArtifacts$captureHealth(
            Entity target,
            CallbackInfo ci
    ) {
        Player player = (Player) (Object) this;

        if (player.level().isClientSide()
                || !(target instanceof LivingEntity livingTarget)) {
            return;
        }

        PhantomSweepManager.captureDamage(player, livingTarget);
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void ancientArtifacts$phantomSweep(
            Entity target,
            CallbackInfo ci
    ) {
        Player player = (Player) (Object) this;

        if (player.level().isClientSide()
                || !(target instanceof LivingEntity livingTarget)
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        ItemStack artifact = ((PlayerInventoryAccess) player.getInventory())
                .ancientArtifacts$getExtraSlot();

        if (!artifact.is(ModItems.ECHO_BLADE)) {
            return;
        }

        float damage = PhantomSweepManager.getCapturedDamage(
                player,
                livingTarget
        );

        if (damage <= 0.0F) {
            return;
        }

        PhantomSweepManager.schedule(
                level,
                player,
                livingTarget,
                damage
        );
    }
}