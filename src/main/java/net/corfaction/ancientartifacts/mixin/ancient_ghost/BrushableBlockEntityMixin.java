package net.corfaction.ancientartifacts.mixin.ancient_ghost;

import net.corfaction.ancientartifacts.entity.AncientGhost;
import net.corfaction.ancientartifacts.entity.ModEntityTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushableBlockEntity.class)
public class BrushableBlockEntityMixin {

    @Inject(method = "dropContent", at = @At("HEAD"))
    private void ancientArtifacts$spawnGhosts(
            ServerLevel level,
            LivingEntity user,
            ItemStack brush,
            CallbackInfo ci
    ) {
        BrushableBlockEntity entity = (BrushableBlockEntity) (Object) this;
        ItemStack result = entity.getItem();

        if (result.isEmpty() || level.getRandom().nextFloat() >= 0.1F) {
            return;
        }

        int count = 1 + level.getRandom().nextInt(4);

        for (int i = 0; i < count; i++) {
            AncientGhost ghost = new AncientGhost(ModEntityTypes.ANCIENT_GHOST, level);

            ghost.setPos(
                    entity.getBlockPos().getX() + 0.5
                            + (level.getRandom().nextDouble() - 0.5) * 2.0,
                    entity.getBlockPos().getY() + 1.0
                            + level.getRandom().nextDouble(),
                    entity.getBlockPos().getZ() + 0.5
                            + (level.getRandom().nextDouble() - 0.5) * 2.0
            );

            level.addFreshEntity(ghost);
        }
    }
}