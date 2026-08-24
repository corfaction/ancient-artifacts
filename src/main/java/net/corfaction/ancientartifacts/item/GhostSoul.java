package net.corfaction.ancientartifacts.item;

import net.corfaction.ancientartifacts.api.PlayerInventoryAccess;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GhostSoul extends Item {

    public GhostSoul(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack soul = player.getItemInHand(hand);
        ItemStack artifact = ((PlayerInventoryAccess) player.getInventory())
                .ancientArtifacts$getExtraSlot();

        if (artifact.isEmpty() || !artifact.isDamageableItem()) {
            return InteractionResult.PASS;
        }

        int repairAmount = Math.max(1, artifact.getMaxDamage() / 5);

        if (artifact.getDamageValue() <= 0) {
            return InteractionResult.PASS;
        }

        artifact.setDamageValue(Math.max(0, artifact.getDamageValue() - repairAmount));

        if (!player.isCreative()) {
            soul.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}