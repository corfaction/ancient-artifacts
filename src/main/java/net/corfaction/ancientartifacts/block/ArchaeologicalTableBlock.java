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
import org.jspecify.annotations.Nullable;

public final class ArchaeologicalTableBlock extends BaseEntityBlock {

    public static final MapCodec<ArchaeologicalTableBlock> CODEC =
            simpleCodec(ArchaeologicalTableBlock::new);

    public ArchaeologicalTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArchaeologicalTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof ArchaeologicalTableBlockEntity table) {
            player.openMenu(table);
        }

        return InteractionResult.SUCCESS;
    }
}