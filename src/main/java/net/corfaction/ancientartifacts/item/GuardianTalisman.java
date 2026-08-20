package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.api.GuardianGhostHolder;
import net.corfaction.ancientartifacts.network.GuardianGhostStatePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class GuardianTalisman extends Item {

    public GuardianTalisman(Properties properties) {
        super(properties.durability(100));
    }

    @Override
    public void inventoryTick(
            ItemStack stack,
            ServerLevel level,
            Entity owner,
            @Nullable EquipmentSlot slot
    ) {
        super.inventoryTick(stack, level, owner, slot);

        if (!(owner instanceof Player player)) {
            return;
        }

        GuardianGhostHolder ghostHolder = (GuardianGhostHolder) player;

        if (!ghostHolder.ancientArtifacts$hasGuardianGhost()) {
            return;
        }

        if (player.tickCount % 1200 != 0) {
            return;
        }

        int damage = stack.getDamageValue() + 1;

        if (damage >= stack.getMaxDamage()) {
            stack.shrink(1);
            ghostHolder.ancientArtifacts$setGuardianGhost(false);

            ServerPlayNetworking.send(
                    (ServerPlayer) player,
                    new GuardianGhostStatePayload(false)
            );

            return;
        }

        stack.setDamageValue(damage);
    }

    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        if (!level.isClientSide()) {
            GuardianGhostHolder ghostHolder = (GuardianGhostHolder) player;
            boolean newState = !ghostHolder.ancientArtifacts$hasGuardianGhost();

            ghostHolder.ancientArtifacts$setGuardianGhost(newState);

            ServerPlayNetworking.send(
                    (ServerPlayer) player,
                    new GuardianGhostStatePayload(newState)
            );
        }

        return InteractionResult.SUCCESS;
    }
}