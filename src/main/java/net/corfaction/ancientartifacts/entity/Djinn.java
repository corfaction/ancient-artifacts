package net.corfaction.ancientartifacts.entity;

import com.llamalad7.mixinextras.expression.impl.ast.expressions.IdentifierAssignmentExpression;
import com.mojang.datafixers.util.Pair;
import net.corfaction.ancientartifacts.AncientArtifacts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class Djinn extends PathfinderMob {

    private BlockPos targetBlockPos;

    public Djinn(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);

        moveControl = new FlyingMoveControl(this, 10, true);
        setNoGravity(true);
    }

    @Override
    public boolean causeFallDamage(
            final double fallDistance,
            final float damageModifier,
            final DamageSource damageSource
    ) {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.0D)
                .add(Attributes.FLYING_SPEED, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);

        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);

        return navigation;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FindSuspiciousBlockGoal(this));
        goalSelector.addGoal(2, new FlyToTargetBlockGoal(this));
    }

    public BlockPos getTargetBlockPos() {
        return targetBlockPos;
    }

    public void setTargetBlockPos(BlockPos pos) {
        targetBlockPos = pos;
    }

    private boolean isSuspiciousBlock(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() == Blocks.SUSPICIOUS_SAND
                || level.getBlockState(pos).getBlock() == Blocks.SUSPICIOUS_GRAVEL;
    }

    private static final class FindSuspiciousBlockGoal extends Goal {

        private static final int STRUCTURE_SEARCH_RADIUS = 1000;
        private static final int STRUCTURE_SEARCH_STEP = 500;

        private static final ResourceKey<Structure> TRAIL_RUINS =
                ResourceKey.create(
                        Registries.STRUCTURE,
                        Identifier.withDefaultNamespace("trail_ruins")
                );

        private static final ResourceKey<Structure> OCEAN_RUINS_COLD =
                ResourceKey.create(
                        Registries.STRUCTURE,
                        Identifier.withDefaultNamespace("trail_ruins")
                );

        private static final ResourceKey<Structure> OCEAN_RUINS_WARM =
                ResourceKey.create(
                        Registries.STRUCTURE,
                        Identifier.withDefaultNamespace("trail_ruins")
                );

        private static final ResourceKey<Structure> DESERT_PYRAMID =
                ResourceKey.create(
                        Registries.STRUCTURE,
                        Identifier.withDefaultNamespace("trail_ruins")
                );

        private final Djinn djinn;
        private final Set<BlockPos> checkedStructures = new HashSet<>();

        private FindSuspiciousBlockGoal(Djinn djinn) {
            this.djinn = djinn;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        private @Nullable Pair<BlockPos, Holder<Structure>> findNearestUncheckedArchaeologicalStructure(
                ServerLevel level
        ) {
            var structures = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);

            HolderSet<Structure> targets = HolderSet.direct(
                    structures.getOrThrow(TRAIL_RUINS),
                    structures.getOrThrow(OCEAN_RUINS_COLD),
                    structures.getOrThrow(OCEAN_RUINS_WARM),
                    structures.getOrThrow(DESERT_PYRAMID)
            );

            BlockPos searchPos = djinn.blockPosition();
            int searchRadius = STRUCTURE_SEARCH_RADIUS;

            for (int attempt = 0; attempt < 1000; attempt++) {
                Pair<BlockPos, Holder<Structure>> structure = level.getChunkSource()
                        .getGenerator()
                        .findNearestMapStructure(
                                level,
                                targets,
                                searchPos,
                                searchRadius,
                                false
                        );

                if (structure == null) {
                    continue;
                }

                BlockPos structurePos = structure.getFirst();

                if (!checkedStructures.contains(structurePos)) {
                    return structure;
                }

                searchPos = BlockPos.containing(
                        searchPos.getX() + STRUCTURE_SEARCH_STEP,
                        searchPos.getY(),
                        searchPos.getZ() + STRUCTURE_SEARCH_STEP
                );
            }

            return null;
        }

        private @Nullable BlockPos findSuspiciousBlockInStructure(
                ServerLevel level,
                BlockPos structurePos
        ) {
            int radius = 32;
            BlockPos nearestBlock = null;
            double nearestDistance = Double.MAX_VALUE;

            for (int x = -radius; x <= radius; x++) {
                for (int y = level.getMinY(); y <= level.getMaxY(); y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = structurePos.offset(x, y, z);

                        if (pos.getY() < level.getMinY() || pos.getY() >= level.getMaxY()) {
                            continue;
                        }

                        if (!djinn.isSuspiciousBlock(level, pos)) {
                            continue;
                        }

                        double distance = structurePos.distSqr(pos);

                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestBlock = pos;
                        }
                    }
                }
            }

            return nearestBlock;
        }

        @Override
        public boolean canUse() {
            return djinn.getTargetBlockPos() == null;
        }

        @Override
        public void start() {
            findNearestSuspiciousBlock();
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        private void findNearestSuspiciousBlock() {
            if (!(djinn.level() instanceof ServerLevel serverLevel)) {
                return;
            }

            while (true) {
                Pair<BlockPos, Holder<Structure>> structure =
                        findNearestUncheckedArchaeologicalStructure(serverLevel);

                if (structure == null) {
                    djinn.spawnDisappearParticles();
                    djinn.discard();
                    return;
                }

                BlockPos structurePos = structure.getFirst();
                checkedStructures.add(structurePos);

                BlockPos suspiciousBlock =
                        findSuspiciousBlockInStructure(serverLevel, structurePos);

                if (suspiciousBlock == null) {
                    continue;
                }

                djinn.setTargetBlockPos(suspiciousBlock);
                return;
            }
        }
    }

    private static final class FlyToTargetBlockGoal extends Goal {

        private static final double REACH_DISTANCE = 1.0D;
        private static final double PATH_STEP_DISTANCE = 24.0D;
        private static final int MAX_SEARCH_HEIGHT = 32;

        private final Djinn djinn;
        private final double speed;

        private @Nullable BlockPos reachablePos;
        private @Nullable BlockPos currentPathTarget;

        private FlyToTargetBlockGoal(Djinn djinn) {
            this.djinn = djinn;
            speed = 0.6D;

            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return djinn.getTargetBlockPos() != null
                    && djinn.isSuspiciousBlock(djinn.level(), djinn.getTargetBlockPos());
        }

        @Override
        public void start() {
            reachablePos = findClosestReachablePosition();

            if (reachablePos == null) {
                djinn.spawnDisappearParticles();
                djinn.discard();
                return;
            }

            currentPathTarget = null;
            updatePath();
        }

        @Override
        public void tick() {
            if (reachablePos == null) {
                return;
            }

            BlockPos suspiciousBlock = djinn.getTargetBlockPos();

            if (suspiciousBlock == null
                    || !djinn.isSuspiciousBlock(djinn.level(), suspiciousBlock)) {
                djinn.getNavigation().stop();
                return;
            }

            double targetX = reachablePos.getX() + 0.5D;
            double targetY = reachablePos.getY() + 0.5D;
            double targetZ = reachablePos.getZ() + 0.5D;

            double distanceToTarget = djinn.distanceToSqr(
                    targetX,
                    targetY,
                    targetZ
            );

            if (distanceToTarget <= REACH_DISTANCE) {
                djinn.getNavigation().stop();
                djinn.spawnDisappearParticles();
                djinn.discard();
                return;
            }

            if (currentPathTarget != null) {
                double distanceToPathTarget = djinn.distanceToSqr(
                        currentPathTarget.getX() + 0.5D,
                        currentPathTarget.getY() + 0.5D,
                        currentPathTarget.getZ() + 0.5D
                );

                if (distanceToPathTarget <= REACH_DISTANCE) {
                    currentPathTarget = null;
                    updatePath();
                    return;
                }
            }

            if (djinn.getNavigation().isDone()) {
                updatePath();
            }
        }

        private void updatePath() {
            if (reachablePos == null) {
                return;
            }

            Vec3 current = djinn.position();
            Vec3 target = new Vec3(
                    reachablePos.getX() + 0.5D,
                    reachablePos.getY() + 0.5D,
                    reachablePos.getZ() + 0.5D
            );

            double dx = target.x - current.x;
            double dy = target.y - current.y;
            double dz = target.z - current.z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance <= PATH_STEP_DISTANCE) {
                currentPathTarget = reachablePos;
                djinn.getNavigation().moveTo(target.x, target.y, target.z, speed);
                return;
            }

            double directionX = dx / distance;
            double directionY = dy / distance;
            double directionZ = dz / distance;

            double intermediateX = current.x + directionX * PATH_STEP_DISTANCE;
            double intermediateY = current.y + directionY * PATH_STEP_DISTANCE;
            double intermediateZ = current.z + directionZ * PATH_STEP_DISTANCE;

            BlockPos intermediatePos = BlockPos.containing(
                    intermediateX,
                    intermediateY,
                    intermediateZ
            );

            if (!canDjinnStandAt(intermediatePos)) {
                BlockPos higherPos = findFreePositionAbove(intermediatePos, 8);

                if (higherPos != null) {
                    intermediatePos = higherPos;
                } else {
                    currentPathTarget = reachablePos;
                    djinn.getNavigation().moveTo(target.x, target.y, target.z, speed);
                    return;
                }
            }

            currentPathTarget = intermediatePos;

            djinn.getNavigation().moveTo(
                    intermediatePos.getX() + 0.5D,
                    intermediatePos.getY() + 0.5D,
                    intermediatePos.getZ() + 0.5D,
                    speed
            );
        }

        private @Nullable BlockPos findClosestReachablePosition() {
            BlockPos target = djinn.getTargetBlockPos();

            if (target == null) {
                return null;
            }

            int minY = Math.max(target.getY(), djinn.level().getMinY());
            int maxY = Math.min(
                    target.getY() + MAX_SEARCH_HEIGHT,
                    djinn.level().getMaxY() - 2
            );

            for (int y = minY; y <= maxY; y++) {
                BlockPos candidate = new BlockPos(
                        target.getX(),
                        y,
                        target.getZ()
                );

                if (canDjinnStandAt(candidate)) {
                    return candidate;
                }
            }

            return null;
        }

        private boolean canDjinnStandAt(BlockPos pos) {
            Level level = djinn.level();

            double x = pos.getX() + 0.5D;
            double y = pos.getY();
            double z = pos.getZ() + 0.5D;

            return level.noCollision(
                    djinn,
                    djinn.getBoundingBox().move(
                            x - djinn.getX(),
                            y - djinn.getY(),
                            z - djinn.getZ()
                    )
            );
        }

        private @Nullable BlockPos findFreePositionAbove(
                BlockPos start,
                int maxDistance
        ) {
            for (int i = 1; i <= maxDistance; i++) {
                BlockPos candidate = start.above(i);

                if (canDjinnStandAt(candidate)) {
                    return candidate;
                }
            }

            return null;
        }

        @Override
        public boolean canContinueToUse() {
            return reachablePos != null
                    && djinn.getTargetBlockPos() != null
                    && djinn.isSuspiciousBlock(
                    djinn.level(),
                    djinn.getTargetBlockPos()
            );
        }

        @Override
        public void stop() {
            djinn.getNavigation().stop();

            reachablePos = null;
            currentPathTarget = null;
        }
    }

    private void spawnDisappearParticles() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        DustColorTransitionOptions particle = new DustColorTransitionOptions(
                0xFFFF00,
                0xFFAA00,
                1.5F
        );

        serverLevel.sendParticles(
                particle,
                getX(),
                getY() + getBbHeight() * 0.5D,
                getZ(),
                30,
                getBbWidth() * 0.5D,
                getBbHeight() * 0.5D,
                getBbWidth() * 0.5D,
                0.05D
        );
    }

    private void spawnTrailParticles() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        DustColorTransitionOptions particle = new DustColorTransitionOptions(
                0xFFFF00,
                0xFFAA00,
                0.7F
        );

        Vec3 movement = getDeltaMovement();
        double length = movement.length();

        if (length < 0.001D) {
            return;
        }

        Vec3 direction = movement.normalize();
        double backDistance = 0.6D;

        double x = getX() - direction.x * backDistance;
        double y = getY() + getBbHeight() * 0.5D - direction.y * backDistance;
        double z = getZ() - direction.z * backDistance;

        serverLevel.sendParticles(
                particle,
                x,
                y,
                z,
                2,
                0.08D,
                0.08D,
                0.08D,
                0.0D
        );
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            spawnTrailParticles();
        }
    }
}