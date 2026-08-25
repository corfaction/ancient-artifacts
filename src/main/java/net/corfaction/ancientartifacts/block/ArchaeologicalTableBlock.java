package net.corfaction.ancientartifacts.block;

import com.mojang.serialization.MapCodec;
import net.corfaction.ancientartifacts.block.entity.ArchaeologicalTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public final class ArchaeologicalTableBlock extends BaseEntityBlock {

    public static final MapCodec<ArchaeologicalTableBlock> CODEC =
            simpleCodec(ArchaeologicalTableBlock::new);

    public ArchaeologicalTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NonNull BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new ArchaeologicalTableBlockEntity(pos, state);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(
            @NonNull BlockState state,
            Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull BlockHitResult hitResult
    ) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ArchaeologicalTableBlockEntity table) {
            player.openMenu(table);
        }

        return InteractionResult.SUCCESS;
    }
}