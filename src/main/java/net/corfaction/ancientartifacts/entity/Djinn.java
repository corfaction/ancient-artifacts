package net.corfaction.ancientartifacts.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

public class Djinn extends PathfinderMob {
    private BlockPos targetBlockPos;

    public Djinn(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
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
        this.goalSelector.addGoal(1, new FindSuspiciousBlockGoal(this));
        this.goalSelector.addGoal(2, new FlyToTargetBlockGoal(this));
    }

    public BlockPos getTargetBlockPos() {
        return targetBlockPos;
    }

    public void setTargetBlockPos(BlockPos pos) {
        this.targetBlockPos = pos;
    }

    private boolean isSuspiciousBlock(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block == Blocks.SUSPICIOUS_SAND || block == Blocks.SUSPICIOUS_GRAVEL;
    }

    /**
     * Определяет высоту поверхности под заданными координатами
     * @param x координата X
     * @param z координата Z
     * @return Y координата поверхности (первый не-воздушный блок + 1)
     */
    private double getSurfaceY(double x, double z) {
        int blockX = Mth.floor(x);
        int blockZ = Mth.floor(z);

        BlockPos heightPos = level().getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                new BlockPos(blockX, 0, blockZ)
        );

        return heightPos.getY();
    }

    // Goal для поиска ближайшего подозрительного блока
    private static class FindSuspiciousBlockGoal extends Goal {
        private final Djinn djinn;

        private static final ResourceKey<Structure> TRAIL_RUINS =
                ResourceKey.create(
                        Registries.STRUCTURE,
                        Identifier.withDefaultNamespace("trail_ruins")
                );

        private @Nullable Pair<BlockPos, Holder<Structure>> findNearestArchaeologicalStructure(
                ServerLevel level
        ) {

            var structures = level.registryAccess()
                    .lookupOrThrow(Registries.STRUCTURE);

            HolderSet<Structure> targets = HolderSet.direct(
                    structures.getOrThrow(TRAIL_RUINS)
            );

            return level.getChunkSource()
                    .getGenerator()
                    .findNearestMapStructure(
                            level,
                            targets,
                            this.djinn.blockPosition(),
                            64,
                            false
                    );
        }

        private @Nullable BlockPos findSuspiciousBlockInNearestStructure(ServerLevel level) {
            Pair<BlockPos, Holder<Structure>> structure =
                    findNearestArchaeologicalStructure(level);

            if (structure == null) {
                return null;
            }

            BlockPos structurePos = structure.getFirst();

            System.out.println("Djinn found Trail Ruins at " + structurePos);

            int radius = 32;

            BlockPos nearestBlock = null;
            double nearestDistance = Double.MAX_VALUE;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= level.getMaxY(); y++) {
                    for (int z = -radius; z <= radius; z++) {

                        BlockPos pos = structurePos.offset(x, y, z);

                        if (pos.getY() < level.getMinY() ||
                                pos.getY() >= level.getMaxY()) {
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

        public FindSuspiciousBlockGoal(Djinn djinn) {
            this.djinn = djinn;
            this.setFlags(EnumSet.of(Flag.MOVE));
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
            System.out.println("Djinn searching at " + djinn.blockPosition());

            BlockPos suspiciousBlock =
                    findSuspiciousBlockInNearestStructure((ServerLevel) djinn.level());

            if (suspiciousBlock == null) {
                System.out.println("Djinn found NO suspicious blocks in nearby Trail Ruins");
                djinn.discard();
                return;
            }

            djinn.setTargetBlockPos(suspiciousBlock);

            System.out.println(
                    "Djinn found suspicious block in Trail Ruins: " + suspiciousBlock
            );
        }
    }

    private static class FlyToTargetBlockGoal extends Goal {
        private final Djinn djinn;
        private final double speed;

        private static final double SURFACE_OFFSET = 2.0D;
        private static final double DESCEND_RADIUS = 2.0D;

        public FlyToTargetBlockGoal(Djinn djinn) {
            this.djinn = djinn;
            this.speed = 0.6D;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return djinn.getTargetBlockPos() != null &&
                    djinn.isSuspiciousBlock(
                            djinn.level(),
                            djinn.getTargetBlockPos()
                    );
        }

        @Override
        public void tick() {
            BlockPos targetPos = djinn.getTargetBlockPos();

            if (targetPos == null) {
                return;
            }

            double targetX = targetPos.getX() + 0.5D;
            double targetZ = targetPos.getZ() + 0.5D;

            double dx = targetX - djinn.getX();
            double dz = targetZ - djinn.getZ();

            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

            /*
             * ============================================================
             * ФАЗА 1
             *
             * Летим к X/Z подозрительного блока.
             * При этом ВСЕГДА держимся на 2 блока выше поверхности.
             * ============================================================
             */
            if (horizontalDistance > DESCEND_RADIUS) {

                double surfaceY = djinn.getSurfaceY(
                        djinn.getX(),
                        djinn.getZ()
                );

                double wantedY = surfaceY + SURFACE_OFFSET;

                djinn.getMoveControl().setWantedPosition(
                        targetX,
                        wantedY,
                        targetZ,
                        speed
                );

                return;
            }

            /*
             * ============================================================
             * ФАЗА 2
             *
             * Джинн уже находится над нужным X/Z.
             *
             * Теперь определяем поверхность ИМЕННО ПОД ЦЕЛЬЮ.
             *
             * Пока джинн находится на этой высоте, он подлетает
             * непосредственно к нужному X/Z.
             * ============================================================
             */

            double targetSurfaceY = djinn.getSurfaceY(
                    targetX,
                    targetZ
            );

            double surfaceFlightY = targetSurfaceY + SURFACE_OFFSET;

            /*
             * Если мы ещё не достигли высоты поверхности + 2,
             * сначала занимаем правильную высоту.
             */
            if (djinn.getY() > surfaceFlightY + 0.2D) {

                djinn.getMoveControl().setWantedPosition(
                        targetX,
                        surfaceFlightY,
                        targetZ,
                        speed
                );

                return;
            }

            /*
             * ============================================================
             * ФАЗА 3
             *
             * Мы находимся прямо над целью и на высоте 2 блока
             * над поверхностью.
             *
             * Теперь начинаем спускаться к подозрительному блоку.
             * ============================================================
             */

            double targetY = targetPos.getY() + 0.5D;

            double distanceToTarget = djinn.distanceToSqr(
                    targetX,
                    targetY,
                    targetZ
            );

            if (distanceToTarget < 1.0D) {
                System.out.println("Djinn reached target at " + targetPos);
                djinn.discard();
                return;
            }

            djinn.getMoveControl().setWantedPosition(
                    targetX,
                    targetY,
                    targetZ,
                    speed
            );
        }

        @Override
        public boolean canContinueToUse() {
            BlockPos targetPos = djinn.getTargetBlockPos();

            return targetPos != null &&
                    djinn.isSuspiciousBlock(
                            djinn.level(),
                            targetPos
                    );
        }
    }

    @Override
    public void tick() {
        super.tick();
    }
}