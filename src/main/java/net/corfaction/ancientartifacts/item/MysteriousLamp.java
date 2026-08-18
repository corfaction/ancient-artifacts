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

public class MysteriousLamp extends Item {

    public MysteriousLamp(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            BlockPos spawnPos = findSpawnPosition(level, player);

            if (spawnPos != null) {
                // Создаем джинна
                Djinn djinn = new Djinn(ModEntityTypes.DJINN, level);
                djinn.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                djinn.setYRot(player.getYRot());
                djinn.setXRot(0);

                // Добавляем джинна в мир
                level.addFreshEntity(djinn);

                // Звуковой эффект
                level.playSound(null, player.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM,
                        SoundSource.PLAYERS, 1.0f, 1.0f);

                // Эффект частиц
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            ParticleTypes.LARGE_SMOKE,
                            spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                            10, 0.5, 0.5, 0.5, 0.1
                    );
                }

                // Уменьшаем количество предметов
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private BlockPos findSpawnPosition(Level level, Player player) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 playerPos = player.position();

        // Проверяем позиции на расстоянии 2-3 блока от игрока
        for (int distance = 2; distance <= 3; distance++) {
            double x = playerPos.x + lookVec.x * distance;
            double y = playerPos.y + player.getEyeHeight() + lookVec.y * distance;
            double z = playerPos.z + lookVec.z * distance;

            BlockPos checkPos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));

            if (level.getBlockState(checkPos).isAir() &&
                    level.getBlockState(checkPos.above()).isAir()) {
                return checkPos;
            }

            BlockPos abovePos = checkPos.above();
            if (level.getBlockState(abovePos).isAir() &&
                    level.getBlockState(abovePos.above()).isAir()) {
                return abovePos;
            }

            BlockPos belowPos = checkPos.below();
            if (level.getBlockState(belowPos).isAir() &&
                    level.getBlockState(belowPos.above()).isAir()) {
                return belowPos;
            }
        }

        // Если не нашли, проверяем вокруг игрока
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos checkPos = player.blockPosition().offset(dx, 1, dz);
                if (level.getBlockState(checkPos).isAir() &&
                        level.getBlockState(checkPos.above()).isAir()) {
                    return checkPos;
                }
            }
        }

        return player.blockPosition().above();
    }
}