package net.corfaction.ancientartifacts.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class ExtraPlayerSlot extends Slot {

    public ExtraPlayerSlot(Container container, int x, int y) {
        super(container, 0, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}