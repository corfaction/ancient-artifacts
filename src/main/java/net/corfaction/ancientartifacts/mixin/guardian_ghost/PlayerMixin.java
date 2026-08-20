package net.corfaction.ancientartifacts.mixin.guardian_ghost;

import net.corfaction.ancientartifacts.api.GuardianGhostHolder;
import net.corfaction.ancientartifacts.item.GuardianTalisman;
import net.corfaction.ancientartifacts.network.GuardianGhostStatePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public final class PlayerMixin implements GuardianGhostHolder {

    @Unique
    private boolean ancientArtifacts$hasGuardianGhost;

    @Override
    public boolean ancientArtifacts$hasGuardianGhost() {
        return ancientArtifacts$hasGuardianGhost;
    }

    @Override
    public void ancientArtifacts$setGuardianGhost(boolean value) {
        ancientArtifacts$hasGuardianGhost = value;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void ancientArtifacts$saveGuardianGhost(
            ValueOutput output,
            CallbackInfo ci
    ) {
        output.putBoolean(
                "AncientArtifactsGuardianGhost",
                ancientArtifacts$hasGuardianGhost
        );
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void ancientArtifacts$loadGuardianGhost(
            ValueInput input,
            CallbackInfo ci
    ) {
        ancientArtifacts$hasGuardianGhost =
                input.getBooleanOr("AncientArtifactsGuardianGhost", false);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void ancientArtifacts$cancelDamage(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        Player player = (Player) (Object) this;

        if (!ancientArtifacts$hasGuardianGhost || player.isInvulnerableTo(level, source)) {
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void ancientArtifacts$checkGuardianTalisman(CallbackInfo ci) {
        Player player = (Player) (Object) this;

        if (player.level().isClientSide()) {
            return;
        }

        if (!ancientArtifacts$hasGuardianGhost) {
            return;
        }

        if (ancientArtifacts$hasGuardianTalisman(player)) {
            return;
        }

        ancientArtifacts$hasGuardianGhost = false;

        ServerPlayNetworking.send(
                (ServerPlayer) player,
                new GuardianGhostStatePayload(false)
        );
    }

    @Unique
    private boolean ancientArtifacts$hasGuardianTalisman(Player player) {
        for (ItemStack stack : player.getInventory()) {
            if (stack.getItem() instanceof GuardianTalisman) {
                return true;
            }
        }

        return false;
    }
}