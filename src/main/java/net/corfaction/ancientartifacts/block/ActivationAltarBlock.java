package net.corfaction.ancientartifacts.block;

import com.mojang.serialization.MapCodec;

import net.corfaction.ancientartifacts.block.entity.ActivationAltarBlockEntity;
import net.corfaction.ancientartifacts.block.entity.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ActivationAltarBlock extends BaseEntityBlock {

    public static final MapCodec<ActivationAltarBlock> CODEC =
            simpleCodec(ActivationAltarBlock::new);

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(5, 4, 0, 11, 7, 16),
            Block.box(0, 4, 5, 16, 7, 11),
            Block.box(5, 7, 5, 11, 14, 11),
            Block.box(0, 14, 0, 16, 16, 16)
    );

    public ActivationAltarBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull VoxelShape getShape(
            @NonNull BlockState state,
            @NonNull BlockGetter level,
            @NonNull BlockPos pos,
            @NonNull CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected @NonNull VoxelShape getCollisionShape(
            @NonNull BlockState state,
            @NonNull BlockGetter level,
            @NonNull BlockPos pos,
            @NonNull CollisionContext context
    ) {
        return SHAPE;
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
        if (!(level.getBlockEntity(pos)
                instanceof ActivationAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (!altar.isEmpty() && !altar.isActivating()) {
            if (!level.isClientSide()) {
                ItemStack altarItem = altar.removeItem();

                if (!altarItem.isEmpty()) {
                    if (!player.getInventory().add(altarItem)) {
                        player.drop(altarItem, false);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (altar.isEmpty()
                && !stack.isEmpty()
                && altar.canAcceptItem(stack)) {

            if (!level.isClientSide()) {
                altar.setItem(stack.copyWithCount(1));

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(
            @NonNull BlockState state,
            Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos)
                instanceof ActivationAltarBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (!altar.isEmpty() && !altar.isActivating()) {
            if (!level.isClientSide()) {
                ItemStack stack = altar.removeItem();

                if (!stack.isEmpty()) {
                    if (!player.getInventory().add(stack)) {
                        player.drop(stack, false);
                    }
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(
            @NonNull BlockPos pos,
            @NonNull BlockState state
    ) {
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
                && level.getBlockEntity(pos)
                instanceof ActivationAltarBlockEntity altar) {

            if (altar.isActivating()) {
                altar.spawnFailureEffects(
                        (ServerLevel) level
                );
            } else {
                ItemStack stack = altar.removeItem();

                if (!stack.isEmpty()) {
                    Block.popResource(
                            level,
                            pos,
                            stack
                    );
                }
            }
        }

        return super.playerWillDestroy(
                level,
                pos,
                state,
                player
        );
    }
}