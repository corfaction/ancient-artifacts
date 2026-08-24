package net.corfaction.ancientartifacts.inventory;

import net.corfaction.ancientartifacts.component.ModDataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ExtraPlayerSlot extends Slot {

    public ExtraPlayerSlot(Container container, int x, int y) {
        super(container, 0, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ACTIVATED, false);
    }
}