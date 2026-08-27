package net.corfaction.ancientartifacts.mixin.skybound;

import net.corfaction.ancientartifacts.api.PlayerInventoryAccess;
import net.corfaction.ancientartifacts.component.ModDataComponents;
import net.corfaction.ancientartifacts.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private boolean ancientArtifacts$extraJump = false;

    @Unique
    private boolean ancientArtifacts$previousJumping = false;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void ancientArtifacts$resetExtraJump(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            if (player.onGround()) {
                ancientArtifacts$extraJump = false;
            }
        }
    }

    @Inject(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;applyInput()V",
                    shift = At.Shift.AFTER
            )
    )
    private void ancientArtifacts$extraJump(CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            boolean jumping = player.isJumping();
            boolean jumpPressed = jumping && !ancientArtifacts$previousJumping;

            ancientArtifacts$previousJumping = jumping;

            if (!jumpPressed) {
                return;
            }

            if (player.onGround()) {
                return;
            }

            ItemStack artifact = ((PlayerInventoryAccess) player.getInventory())
                    .ancientArtifacts$getExtraSlot();

            if (!artifact.is(ModItems.SKYBOUND_TALISMAN)) {
                return;
            }

            if (!artifact.getOrDefault(ModDataComponents.ACTIVATED, false)) {
                return;
            }

            if (ancientArtifacts$extraJump) {
                return;
            }

            player.jumpFromGround();

            for (int i = 0; i < 10; i++) {
                player.level().addParticle(
                        ParticleTypes.CLOUD,
                        player.getX() + (player.getRandom().nextDouble() - 0.5) * 0.8,
                        player.getY() + 0.1,
                        player.getZ() + (player.getRandom().nextDouble() - 0.5) * 0.8,
                        (player.getRandom().nextDouble() - 0.5) * 0.1,
                        player.getRandom().nextDouble() * 0.1,
                        (player.getRandom().nextDouble() - 0.5) * 0.1
                );
            }

            player.playSound(
                    SoundEvents.BREEZE_JUMP,
                    1.0F,
                    1.2F
            );

            ancientArtifacts$extraJump = true;
        }
    }
}