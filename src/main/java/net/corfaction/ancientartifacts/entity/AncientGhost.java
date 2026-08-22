package net.corfaction.ancientartifacts.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AncientGhost extends Monster {

    private static final double FOLLOW_DISTANCE = 64.0D;
    private static final double ORBIT_SPEED = 0.20D;
    private static final double WANDER_SPEED = 0.12D;
    private static final double DASH_SPEED = 0.9D;
    private static final float BOB_SPEED = 0.08F;
    private static final float BOB_AMPLITUDE = 0.08F;

    private float bobTime;
    private float bobOffset;
    private boolean spawnEffectsPlayed;

    public AncientGhost(EntityType<? extends Monster> type, Level level) {
        super(type, level);

        moveControl = new FlyingMoveControl<>(this, 20, true);
        setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_DISTANCE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.3D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new GhostAttackGoal(this));
        goalSelector.addGoal(1, new GhostWanderGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setRequiredPathLength(48.0F);
        return navigation;
    }

    @Override
    public void travel(Vec3 input) {
        travelFlying(input, getSpeed());
    }

    @Override
    public void tick() {
        noPhysics = true;
        super.tick();

        if (!level().isClientSide() && !spawnEffectsPlayed) {
            spawnEffectsPlayed = true;

            if (level() instanceof ServerLevel serverLevel) {
                playSpawnEffects(serverLevel);
            }
        }

        if (level().isClientSide()) {
            bobTime += BOB_SPEED;
            bobOffset = Mth.sin(bobTime) * BOB_AMPLITUDE;
        }
    }

    private void playSpawnEffects(ServerLevel level) {
        level.playSound(
                null,
                getX(),
                getY(),
                getZ(),
                SoundEvents.SOUL_ESCAPE,
                SoundSource.HOSTILE,
                4.5F,
                0.7F + random.nextFloat() * 0.2F
        );

        level.sendParticles(
                ParticleTypes.CLOUD,
                getX(),
                getY() + getBbHeight() * 0.5D,
                getZ(),
                20,
                getBbWidth() * 0.6D,
                getBbHeight() * 0.6D,
                getBbWidth() * 0.6D,
                0.04D
        );

        level.sendParticles(
                ParticleTypes.WHITE_ASH,
                getX(),
                getY() + getBbHeight() * 0.5D,
                getZ(),
                25,
                getBbWidth() * 0.5D,
                getBbHeight() * 0.6D,
                getBbWidth() * 0.5D,
                0.02D
        );
    }

    public void playDashEffects() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.playSound(
                null,
                getX(),
                getY(),
                getZ(),
                SoundEvents.SOUL_ESCAPE,
                SoundSource.HOSTILE,
                4.0F,
                1.1F + random.nextFloat() * 0.2F
        );

        serverLevel.sendParticles(
                ParticleTypes.CLOUD,
                getX(),
                getY() + getBbHeight() * 0.5D,
                getZ(),
                10,
                0.3D,
                0.4D,
                0.3D,
                0.08D
        );

        serverLevel.sendParticles(
                ParticleTypes.WHITE_ASH,
                getX(),
                getY() + getBbHeight() * 0.5D,
                getZ(),
                12,
                0.25D,
                0.35D,
                0.25D,
                0.04D
        );
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (level() instanceof ServerLevel serverLevel) {
            Player player = serverLevel.getNearestPlayer(this, FOLLOW_DISTANCE);

            if (player != null && player.isAlive() && !player.isSpectator()) {
                setTarget(player);
            } else {
                setTarget(null);
            }
        }
    }

    private void lookTowards(Vec3 position) {
        double dx = position.x - getX();
        double dy = position.y - getEyeY();
        double dz = position.z - getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        if (horizontal < 0.0001D) {
            return;
        }

        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-Math.atan2(dy, horizontal) * 180.0D / Math.PI);

        setYRot(yaw);
        setXRot(pitch);
        setYHeadRot(yaw);

        yRotO = yaw;
        xRotO = pitch;
        yHeadRotO = yaw;
    }

    public float getBobOffset() {
        return bobOffset;
    }

    private static class GhostAttackGoal extends Goal {

        private enum Phase {
            ORBIT,
            DASH
        }

        private static final int MIN_DASH_COOLDOWN = 50;
        private static final int MAX_DASH_COOLDOWN = 100;
        private static final int DASH_DURATION = 12;

        private final AncientGhost ghost;

        private Phase phase = Phase.ORBIT;
        private double targetX;
        private double targetY;
        private double targetZ;
        private double dashStartX;
        private double dashStartY;
        private double dashStartZ;
        private double dashTargetX;
        private double dashTargetY;
        private double dashTargetZ;
        private double orbitAngle;
        private int changePositionCooldown;
        private int dashCooldown;
        private int dashTicks;
        private int attackCooldown;

        public GhostAttackGoal(AncientGhost ghost) {
            this.ghost = ghost;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return ghost.getTarget() instanceof Player player
                    && player.isAlive()
                    && !player.isSpectator();
        }

        @Override
        public boolean canContinueToUse() {
            if (!(ghost.getTarget() instanceof Player player)) {
                return false;
            }

            return player.isAlive()
                    && !player.isSpectator()
                    && ghost.distanceToSqr(player) < FOLLOW_DISTANCE * FOLLOW_DISTANCE;
        }

        @Override
        public void start() {
            phase = Phase.ORBIT;
            attackCooldown = 0;
            dashCooldown = MIN_DASH_COOLDOWN
                    + ghost.getRandom().nextInt(MAX_DASH_COOLDOWN - MIN_DASH_COOLDOWN);
            orbitAngle = ghost.getRandom().nextDouble() * Math.PI * 2.0D;
            chooseOrbitPosition();
            ghost.setDeltaMovement(Vec3.ZERO);
        }

        @Override
        public void tick() {
            if (!(ghost.getTarget() instanceof Player player)) {
                return;
            }

            ghost.lookTowards(new Vec3(player.getX(), player.getEyeY(), player.getZ()));
            attackCooldown--;

            if (phase == Phase.ORBIT) {
                tickOrbit(player);
            } else {
                tickDash(player);
            }
        }

        private void tickOrbit(Player player) {
            changePositionCooldown--;
            dashCooldown--;

            if (changePositionCooldown <= 0) {
                chooseOrbitPosition();
            }

            moveTowardsOrbitPosition();

            if (dashCooldown <= 0) {
                startDash(player);
            }
        }

        private void chooseOrbitPosition() {
            if (!(ghost.getTarget() instanceof Player player)) {
                return;
            }

            double direction = ghost.getRandom().nextBoolean() ? 1.0D : -1.0D;
            orbitAngle += direction * (0.7D + ghost.getRandom().nextDouble() * 1.2D);

            double radius = 3.0D + ghost.getRandom().nextDouble() * 4.0D;

            targetX = player.getX() + Math.cos(orbitAngle) * radius;
            targetZ = player.getZ() + Math.sin(orbitAngle) * radius;
            targetY = player.getY() + 1.0D + ghost.getRandom().nextDouble() * 3.0D;

            changePositionCooldown = 15 + ghost.getRandom().nextInt(25);
        }

        private void moveTowardsOrbitPosition() {
            double dx = targetX - ghost.getX();
            double dy = targetY - ghost.getY();
            double dz = targetZ - ghost.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance < 0.5D) {
                ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.9D));
                return;
            }

            Vec3 direction = new Vec3(dx / distance, dy / distance, dz / distance);
            Vec3 desiredVelocity = direction.scale(ORBIT_SPEED);

            ghost.setDeltaMovement(ghost.getDeltaMovement().lerp(desiredVelocity, 0.25D));
        }

        private void startDash(Player player) {
            phase = Phase.DASH;
            dashTicks = 0;

            dashStartX = ghost.getX();
            dashStartY = ghost.getY();
            dashStartZ = ghost.getZ();

            Vec3 direction = new Vec3(
                    player.getX() - ghost.getX(),
                    player.getEyeY() - ghost.getY(),
                    player.getZ() - ghost.getZ()
            );

            if (direction.lengthSqr() < 0.001D) {
                direction = new Vec3(0.0D, 0.0D, 1.0D);
            } else {
                direction = direction.normalize();
            }

            double distance = 6.0D + ghost.getRandom().nextDouble() * 2.0D;

            dashTargetX = player.getX() + direction.x * distance;
            dashTargetY = player.getEyeY() + direction.y * distance;
            dashTargetZ = player.getZ() + direction.z * distance;

            ghost.setDeltaMovement(direction.scale(DASH_SPEED));
            ghost.playDashEffects();
        }

        private void tickDash(Player player) {
            dashTicks++;

            double dx = dashTargetX - ghost.getX();
            double dy = dashTargetY - ghost.getY();
            double dz = dashTargetZ - ghost.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.1D) {
                Vec3 direction = new Vec3(dx / distance, dy / distance, dz / distance);
                ghost.setDeltaMovement(direction.scale(DASH_SPEED));
            }

            if (attackCooldown <= 0 && crossedPlayer(player)) {
                if (ghost.level() instanceof ServerLevel serverLevel) {
                    ghost.doHurtTarget(serverLevel, player);
                }

                attackCooldown = 20;
            }

            if (dashTicks >= DASH_DURATION || distance < 1.0D) {
                finishDash();
            }
        }

        private boolean crossedPlayer(Player player) {
            Vec3 start = new Vec3(dashStartX, dashStartY, dashStartZ);
            Vec3 end = new Vec3(ghost.getX(), ghost.getY(), ghost.getZ());

            Vec3 playerPosition = new Vec3(
                    player.getX(),
                    player.getY() + player.getBbHeight() * 0.5D,
                    player.getZ()
            );

            Vec3 line = end.subtract(start);
            double lengthSqr = line.lengthSqr();

            if (lengthSqr < 0.001D) {
                return false;
            }

            double t = playerPosition.subtract(start).dot(line) / lengthSqr;
            t = Math.max(0.0D, Math.min(1.0D, t));

            Vec3 closestPoint = start.add(line.scale(t));
            return closestPoint.distanceToSqr(playerPosition) < 1.5D * 1.5D;
        }

        private void finishDash() {
            phase = Phase.ORBIT;
            dashCooldown = MIN_DASH_COOLDOWN
                    + ghost.getRandom().nextInt(MAX_DASH_COOLDOWN - MIN_DASH_COOLDOWN);

            chooseOrbitPosition();
            ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.35D));
        }

        @Override
        public void stop() {
            phase = Phase.ORBIT;
            ghost.setDeltaMovement(Vec3.ZERO);
        }
    }

    private static class GhostWanderGoal extends Goal {

        private final AncientGhost ghost;

        private double targetX;
        private double targetY;
        private double targetZ;
        private int cooldown;

        public GhostWanderGoal(AncientGhost ghost) {
            this.ghost = ghost;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (ghost.getTarget() instanceof Player player && player.isAlive()) {
                return false;
            }

            if (cooldown > 0) {
                cooldown--;
                return false;
            }

            return findNewPosition();
        }

        @Override
        public boolean canContinueToUse() {
            if (ghost.getTarget() instanceof Player player && player.isAlive()) {
                return false;
            }

            return ghost.distanceToSqr(targetX, targetY, targetZ) > 1.5D * 1.5D;
        }

        @Override
        public void start() {
            moveTowardsTarget();
        }

        @Override
        public void tick() {
            moveTowardsTarget();
        }

        @Override
        public void stop() {
            cooldown = 60 + ghost.getRandom().nextInt(100);
            ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.8D));
        }

        private boolean findNewPosition() {
            Level level = ghost.level();

            int x = ghost.blockPosition().getX() + ghost.getRandom().nextInt(41) - 20;
            int z = ghost.blockPosition().getZ() + ghost.getRandom().nextInt(41) - 20;

            int surfaceY = level.getHeight(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    x,
                    z
            );

            int height = 2 + ghost.getRandom().nextInt(4);

            targetX = x + 0.5D;
            targetY = surfaceY + height;
            targetZ = z + 0.5D;

            return true;
        }

        private void moveTowardsTarget() {
            double dx = targetX - ghost.getX();
            double dy = targetY - ghost.getY();
            double dz = targetZ - ghost.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance < 0.5D) {
                ghost.setDeltaMovement(ghost.getDeltaMovement().scale(0.9D));
                return;
            }

            Vec3 direction = new Vec3(dx / distance, dy / distance, dz / distance);
            Vec3 desiredVelocity = direction.scale(WANDER_SPEED);

            ghost.setDeltaMovement(ghost.getDeltaMovement().lerp(desiredVelocity, 0.30D));
            ghost.lookTowards(new Vec3(targetX, targetY, targetZ));
        }
    }
}