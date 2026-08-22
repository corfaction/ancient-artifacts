package net.corfaction.ancientartifacts.mixin.guardian_ghost;

import net.corfaction.ancientartifacts.api.PlayerInventoryAccess;
import net.corfaction.ancientartifacts.item.GuardianTalisman;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public final class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void ancientArtifacts$tickArtifact(CallbackInfo ci) {
        Player player = (Player) (Object) this;

        if (player.level().isClientSide()) {
            return;
        }

        ItemStack artifact = ((PlayerInventoryAccess) player.getInventory())
                .ancientArtifacts$getExtraSlot();

        if (!(artifact.getItem() instanceof GuardianTalisman)) {
            return;
        }

        if (player.tickCount % 1200 != 0) {
            return;
        }

        int damage = artifact.getDamageValue() + 1;

        if (damage >= artifact.getMaxDamage()) {
            artifact.setCount(0);
            return;
        }

        artifact.setDamageValue(damage);
    }

    @Inject(
            method = "hurtServer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ancientArtifacts$guardianTalismanProtection(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        Player player = (Player) (Object) this;
        ItemStack artifact = ((PlayerInventoryAccess) player.getInventory())
                .ancientArtifacts$getExtraSlot();

        if (!(artifact.getItem() instanceof GuardianTalisman)
                || player.isInvulnerableTo(level, source)) {
            return;
        }

        if (player.getRandom().nextFloat() >= 0.2F) {
            return;
        }

        cir.setReturnValue(false);

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.SHIELD_BLOCK.value(),
                SoundSource.PLAYERS,
                1.5F,
                0.55F
        );

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ELDER_GUARDIAN_CURSE,
                SoundSource.PLAYERS,
                0.7F,
                1.15F
        );

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS,
                1.2F,
                0.6F
        );

        double x = player.getX();
        double y = player.getY() + player.getEyeHeight() * 0.5D;
        double z = player.getZ();

        level.sendParticles(
                ParticleTypes.SOUL,
                x,
                y,
                z,
                30,
                0.8D,
                0.8D,
                0.8D,
                0.1D
        );

        level.sendParticles(
                ParticleTypes.ELECTRIC_SPARK,
                x,
                y,
                z,
                15,
                1.0D,
                1.0D,
                1.0D,
                0.2D
        );
    }
}