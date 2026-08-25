package net.corfaction.ancientartifacts.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PhantomSweepManager {

    private static final int DELAY = 20;
    private static final float DAMAGE_MULTIPLIER = 0.5F;

    private static final Map<PlayerTargetKey, Float> PREVIOUS_HEALTH =
            new HashMap<>();

    private static final List<PendingSweep> PENDING = new ArrayList<>();

    private PhantomSweepManager() {
    }

    public static void captureDamage(
            Player player,
            LivingEntity target
    ) {
        PREVIOUS_HEALTH.put(
                new PlayerTargetKey(player, target),
                target.getHealth()
        );
    }

    public static float getCapturedDamage(
            Player player,
            LivingEntity target
    ) {
        Float previousHealth = PREVIOUS_HEALTH.remove(
                new PlayerTargetKey(player, target)
        );

        if (previousHealth == null) {
            return 0.0F;
        }

        return Math.max(
                previousHealth - target.getHealth(),
                0.0F
        );
    }

    public static void schedule(
            ServerLevel level,
            Player player,
            LivingEntity target,
            float damage
    ) {
        PENDING.add(new PendingSweep(
                level,
                player,
                target,
                damage,
                DELAY
        ));
    }

    public static void tick() {
        PENDING.removeIf(PendingSweep::tick);
    }

    private record PlayerTargetKey(
            Player player,
            LivingEntity target
    ) {
    }

    private static final class PendingSweep {

        private final ServerLevel level;
        private final Player player;
        private final LivingEntity target;
        private final float damage;
        private int ticks;

        private PendingSweep(
                ServerLevel level,
                Player player,
                LivingEntity target,
                float damage,
                int ticks
        ) {
            this.level = level;
            this.player = player;
            this.target = target;
            this.damage = damage;
            this.ticks = ticks;
        }

        private boolean tick() {
            if (--ticks > 0) {
                return false;
            }

            if (player.isRemoved()
                    || target.isRemoved()
                    || !target.isAlive()) {
                return true;
            }

            perform();
            return true;
        }

        private void perform() {
            DamageSource damageSource = player.createDamageSource();

            target.hurtServer(
                    level,
                    damageSource,
                    damage * DAMAGE_MULTIPLIER
            );

            Vec3 center = getTargetCenter();

            sendSweepToTrackingPlayers(center);

            level.playSound(
                    null,
                    center.x,
                    center.y,
                    center.z,
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    1.0F,
                    0.65F
            );

            spawnBlueParticles(center);
        }

        private Vec3 getTargetCenter() {
            return target.position().add(
                    0.0D,
                    target.getBbHeight() * 0.5D,
                    0.0D
            );
        }

        private void sendSweepToTrackingPlayers(Vec3 center) {
            Vec3 direction = center.subtract(
                    player.getEyePosition()
            );

            float yRot = (float) Math.toDegrees(
                    Math.atan2(
                            -direction.x,
                            direction.z
                    )
            );

            PhantomSweepPayload payload = new PhantomSweepPayload(
                    center.x,
                    center.y,
                    center.z,
                    yRot
            );

            if (player instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(
                        serverPlayer,
                        payload
                );
            }

            for (ServerPlayer other : level.players()) {
                if (other != player
                        && other.distanceToSqr(center)
                        <= 64.0D * 64.0D) {
                    ServerPlayNetworking.send(
                            other,
                            payload
                    );
                }
            }
        }

        private void spawnBlueParticles(Vec3 center) {
            level.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK,
                    center.x,
                    center.y,
                    center.z,
                    10,
                    0.35D,
                    0.35D,
                    0.35D,
                    0.02D
            );
        }
    }
}