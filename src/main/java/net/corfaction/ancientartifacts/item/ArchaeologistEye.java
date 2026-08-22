package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.network.ArchaeologistEyePayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class ArchaeologistEye extends Item {

    public static final int SEARCH_RADIUS = 32;
    public static final int SEARCH_VERTICAL_RADIUS = 8;
    public static final int DISPLAY_DURATION = 200;
    public static final int MAX_BLOCKS = 100;

    public ArchaeologistEye(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
            net.minecraft.world.level.Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        serverPlayer.addEffect(new MobEffectInstance(
                MobEffects.BLINDNESS,
                DISPLAY_DURATION,
                0,
                false,
                false,
                false
        ));

        serverLevel.playSound(
                null,
                serverPlayer.blockPosition(),
                SoundEvents.AMBIENT_CAVE.value(),
                SoundSource.PLAYERS,
                1.0F,
                0.6F
        );

        List<BlockPos> positions = findSuspiciousBlocks(
                serverLevel,
                player.blockPosition()
        );

        if (!positions.isEmpty()) {
            ArchaeologistEyePayload.send(
                    serverPlayer,
                    positions,
                    DISPLAY_DURATION
            );
        }

        player.getCooldowns().addCooldown(stack, 20 * 15);
        return InteractionResult.SUCCESS;
    }

    private static List<BlockPos> findSuspiciousBlocks(
            ServerLevel level,
            BlockPos center
    ) {
        List<BlockPos> result = new ArrayList<>();

        BlockPos min = center.offset(
                -SEARCH_RADIUS,
                -SEARCH_VERTICAL_RADIUS,
                -SEARCH_RADIUS
        );

        BlockPos max = center.offset(
                SEARCH_RADIUS,
                SEARCH_VERTICAL_RADIUS,
                SEARCH_RADIUS
        );

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (result.size() >= MAX_BLOCKS) {
                break;
            }

            if (isArchaeologicalBlock(level, pos)) {
                result.add(pos.immutable());
            }
        }

        return result;
    }

    private static boolean isArchaeologicalBlock(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.SUSPICIOUS_SAND)
                || level.getBlockState(pos).is(Blocks.SUSPICIOUS_GRAVEL);
    }
}