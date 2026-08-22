package net.corfaction.ancientartifacts.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class PhantomSweepManager {

    /*
     * Задержка между обычным ударом
     * и фантомным.
     */
    private static final int DELAY = 20;

    /*
     * Phantom Sweep наносит:
     *
     * фактический урон обычного удара * 0.5
     */
    private static final float DAMAGE_MULTIPLIER = 0.5F;

    /*
     * Здоровье цели перед обычным ударом.
     */
    private static final Map<PlayerTargetKey, Float> PREVIOUS_HEALTH =
            new HashMap<>();

    private static final List<PendingSweep> PENDING =
            new java.util.ArrayList<>();

    private PhantomSweepManager() {
    }

    /**
     * Сохраняем здоровье цели непосредственно
     * перед выполнением Player.attack().
     */
    public static void captureDamage(
            Player player,
            LivingEntity target
    ) {
        PREVIOUS_HEALTH.put(
                new PlayerTargetKey(player, target),
                target.getHealth()
        );
    }

    /**
     * Получаем фактический урон, нанесённый
     * обычной атакой.
     */
    public static float getCapturedDamage(
            Player player,
            LivingEntity target
    ) {
        PlayerTargetKey key =
                new PlayerTargetKey(player, target);

        Float previousHealth =
                PREVIOUS_HEALTH.remove(key);

        if (previousHealth == null) {
            return 0.0F;
        }

        float currentHealth =
                target.getHealth();

        float damage =
                previousHealth - currentHealth;

        return Math.max(damage, 0.0F);
    }

    /**
     * Запланировать фантомный удар.
     */
    public static void schedule(
            ServerLevel level,
            Player player,
            LivingEntity target,
            float damage
    ) {
        PENDING.add(
                new PendingSweep(
                        level,
                        player,
                        target,
                        damage,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        player.getYRot(),
                        DELAY
                )
        );
    }

    /**
     * Вызывается один раз за серверный тик.
     */
    public static void tick() {

        Iterator<PendingSweep> iterator =
                PENDING.iterator();

        while (iterator.hasNext()) {

            PendingSweep sweep =
                    iterator.next();

            if (sweep.tick()) {
                iterator.remove();
            }
        }
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

        /*
         * Реальный урон обычной атаки.
         */
        private final float damage;

        private final double x;
        private final double y;
        private final double z;

        private final float yRot;

        private int ticks;

        private PendingSweep(
                ServerLevel level,
                Player player,
                LivingEntity target,
                float damage,
                double x,
                double y,
                double z,
                float yRot,
                int ticks
        ) {
            this.level = level;
            this.player = player;
            this.target = target;

            this.damage = damage;

            this.x = x;
            this.y = y;
            this.z = z;

            this.yRot = yRot;

            this.ticks = ticks;
        }

        private boolean tick() {

            ticks--;

            if (ticks > 0) {
                return false;
            }

            /*
             * Игрок или цель больше не существуют.
             */
            if (player.isRemoved()
                    || target.isRemoved()
                    || !target.isAlive()) {

                return true;
            }

            perform();

            return true;
        }

        private void perform() {

            /*
             * =====================================
             * DAMAGE
             * =====================================
             */

            DamageSource damageSource =
                    player.createDamageSource();

            /*
             * Phantom Sweep =
             *
             * фактический урон обычного удара
             * × DAMAGE_MULTIPLIER
             */
            float phantomDamage =
                    damage * DAMAGE_MULTIPLIER;

            target.hurtServer(
                    level,
                    damageSource,
                    phantomDamage
            );

            /*
             * =====================================
             * SWEEP
             * =====================================
             */

            sendSweepToTrackingPlayers();

            /*
             * =====================================
             * SOUND
             * =====================================
             */

            level.playSound(
                    null,
                    x,
                    y,
                    z,
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    1.0F,
                    0.65F
            );

            /*
             * =====================================
             * PARTICLES
             * =====================================
             */

            spawnBlueParticles();
        }

        private void sendSweepToTrackingPlayers() {

            PhantomSweepPayload payload =
                    new PhantomSweepPayload(
                            x,
                            y,
                            z,
                            yRot
                    );

            /*
             * Сам атакующий игрок.
             */
            if (player instanceof ServerPlayer serverPlayer) {

                ServerPlayNetworking.send(
                        serverPlayer,
                        payload
                );
            }

            /*
             * Остальные игроки поблизости.
             */
            for (ServerPlayer other :
                    level.players()) {

                if (other == player) {
                    continue;
                }

                if (other.distanceToSqr(
                        x,
                        y,
                        z
                ) <= 64.0D * 64.0D) {

                    ServerPlayNetworking.send(
                            other,
                            payload
                    );
                }
            }
        }

        private void spawnBlueParticles() {

            Vec3 center =
                    target.position()
                            .add(
                                    0.0D,
                                    target.getBbHeight() * 0.5D,
                                    0.0D
                            );

            level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.ELECTRIC_SPARK,
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