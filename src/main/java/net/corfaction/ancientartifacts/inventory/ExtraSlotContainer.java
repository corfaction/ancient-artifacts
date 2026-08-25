package net.corfaction.ancientartifacts.inventory;

import net.corfaction.ancientartifacts.api.PlayerInventoryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class ExtraSlotContainer implements Container {

    private final Inventory inventory;

    public ExtraSlotContainer(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getItem(0).isEmpty();
    }

    @Override
    public @NonNull ItemStack getItem(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }

        return ((PlayerInventoryAccess) inventory).ancientArtifacts$getExtraSlot();
    }

    @Override
    public @NonNull ItemStack removeItem(int slot, int amount) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = getItem(0);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = stack.split(amount);

        if (stack.isEmpty()) {
            setItem(0, ItemStack.EMPTY);
        }

        setChanged();
        return result;
    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = getItem(0);
        setItem(0, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @NonNull ItemStack stack) {
        if (slot != 0) {
            return;
        }

        ((PlayerInventoryAccess) inventory).ancientArtifacts$setExtraSlot(stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        inventory.setChanged();
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.@NonNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        setItem(0, ItemStack.EMPTY);
    }
}