package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.entity.Djinn;
import net.corfaction.ancientartifacts.entity.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class MysteriousLamp extends Item {

    public MysteriousLamp(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        BlockPos spawnPos = findSpawnPosition(level, player);

        if (spawnPos == null) {
            return InteractionResult.PASS;
        }

        Djinn djinn = new Djinn(ModEntityTypes.DJINN, level);
        djinn.setPos(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D
        );
        djinn.setYRot(player.getYRot());
        djinn.setXRot(0.0F);

        level.addFreshEntity(djinn);

        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    spawnPos.getX() + 0.5D,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5D,
                    10,
                    0.5D,
                    0.5D,
                    0.5D,
                    0.1D
            );
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    private BlockPos findSpawnPosition(Level level, Player player) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.position();

        for (int distance = 2; distance <= 3; distance++) {
            BlockPos checkPos = BlockPos.containing(
                    playerPos.x + lookVec.x * distance,
                    playerPos.y + player.getEyeHeight() + lookVec.y * distance,
                    playerPos.z + lookVec.z * distance
            );

            if (level.getBlockState(checkPos).isAir()
                    && level.getBlockState(checkPos.above()).isAir()) {
                return checkPos;
            }

            BlockPos abovePos = checkPos.above();

            if (level.getBlockState(abovePos).isAir()
                    && level.getBlockState(abovePos.above()).isAir()) {
                return abovePos;
            }

            BlockPos belowPos = checkPos.below();

            if (level.getBlockState(belowPos).isAir()
                    && level.getBlockState(belowPos.above()).isAir()) {
                return belowPos;
            }
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos checkPos = player.blockPosition().offset(dx, 1, dz);

                if (level.getBlockState(checkPos).isAir()
                        && level.getBlockState(checkPos.above()).isAir()) {
                    return checkPos;
                }
            }
        }

        return player.blockPosition().above();
    }
}