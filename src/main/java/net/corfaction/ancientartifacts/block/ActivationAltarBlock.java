package net.corfaction.ancientartifacts.block;

import com.mojang.serialization.MapCodec;

import net.corfaction.ancientartifacts.block.entity.ActivationAltarBlockEntity;
import net.corfaction.ancientartifacts.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ActivationAltarBlock extends BaseEntityBlock {

    public static final MapCodec<ActivationAltarBlock> CODEC =
            simpleCodec(ActivationAltarBlock::new);

    public ActivationAltarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(
            @NonNull ItemStack stack,
            @NonNull BlockState state,
            Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull InteractionHand hand,
            @NonNull BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof ActivationAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (!altar.isEmpty()
                || stack.isEmpty()
                || !altar.canAcceptItem(stack)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            altar.setItem(stack.copyWithCount(1));

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(
            @NonNull BlockState state,
            Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof ActivationAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (altar.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            ItemStack stack = altar.removeItem();

            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new ActivationAltarBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            @NonNull BlockState state,
            @NonNull BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlockEntityTypes.ACTIVATION_ALTAR,
                ActivationAltarBlockEntity::serverTick
        );
    }

    @Override
    public @NonNull BlockState playerWillDestroy(
            Level level,
            @NonNull BlockPos pos,
            @NonNull BlockState state,
            @NonNull Player player
    ) {
        if (!level.isClientSide()
                && level.getBlockEntity(pos) instanceof ActivationAltarBlockEntity altar) {
            Block.popResource(level, pos, altar.removeItem());
        }

        return super.playerWillDestroy(level, pos, state, player);
    }
}