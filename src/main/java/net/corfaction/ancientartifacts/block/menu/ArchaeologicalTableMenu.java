package net.corfaction.ancientartifacts.block.menu;

import net.corfaction.ancientartifacts.artifact.ArchaeologicalRecipes;
import net.corfaction.ancientartifacts.block.entity.ArchaeologicalTableBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ArchaeologicalTableMenu extends AbstractContainerMenu {

    private static final int BRUSH_SLOT = ArchaeologicalTableBlockEntity.BRUSH_SLOT;
    private static final int WATER_SLOT = ArchaeologicalTableBlockEntity.WATER_SLOT;
    private static final int INPUT_SLOT = ArchaeologicalTableBlockEntity.INPUT_SLOT;
    private static final int RESULT_SLOT = ArchaeologicalTableBlockEntity.RESULT_SLOT;

    private static final int TABLE_SLOT_END = ArchaeologicalTableBlockEntity.SLOT_COUNT;
    private static final int INVENTORY_SLOT_START = TABLE_SLOT_END;
    private static final int HOTBAR_SLOT_END = 40;

    private final Container container;
    private final ContainerData data;

    public ArchaeologicalTableMenu(int containerId, Inventory inventory) {
        this(
                containerId,
                inventory,
                new SimpleContainer(ArchaeologicalTableBlockEntity.SLOT_COUNT),
                new SimpleContainerData(ArchaeologicalTableBlockEntity.DATA_COUNT)
        );
    }

    public ArchaeologicalTableMenu(
            int containerId,
            Inventory inventory,
            Container container,
            ContainerData data
    ) {
        super(ModMenuTypes.ARCHAEOLOGICAL_TABLE, containerId);

        checkContainerSize(container, ArchaeologicalTableBlockEntity.SLOT_COUNT);
        checkContainerDataCount(data, ArchaeologicalTableBlockEntity.DATA_COUNT);

        this.container = container;
        this.data = data;

        addSlot(new BrushSlot(container, BRUSH_SLOT, 8, 18));
        addSlot(new WaterSlot(container, WATER_SLOT, 31, 18));
        addSlot(new InputSlot(container, INPUT_SLOT, 138, 18));
        addSlot(new ResultSlot(container, RESULT_SLOT, 138, 58));

        addDataSlots(data);
        addStandardInventorySlots(inventory, 8, 84);
    }

    public Container getContainer() {
        return container;
    }

    public boolean isPixelCleaned(int x, int y) {
        if (x < 0 || x >= 16 || y < 0 || y >= 16) {
            return false;
        }

        int index = y * 16 + x;
        int dataIndex = index / 16;
        int bitIndex = index % 16;

        return (data.get(dataIndex) & (1 << bitIndex)) != 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack clickedStack = slot.getItem();
        ItemStack result = clickedStack.copy();

        if (slotIndex < TABLE_SLOT_END) {
            if (!moveItemStackTo(
                    clickedStack,
                    INVENTORY_SLOT_START,
                    HOTBAR_SLOT_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(clickedStack, result);
        } else if (BrushSlot.mayPlaceItem(clickedStack)) {
            if (!moveItemStackTo(clickedStack, BRUSH_SLOT, BRUSH_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (WaterSlot.mayPlaceItem(clickedStack)) {
            if (!moveItemStackTo(clickedStack, WATER_SLOT, WATER_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (InputSlot.mayPlaceItem(clickedStack)) {
            if (!moveItemStackTo(clickedStack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (clickedStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (clickedStack.getCount() == result.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, result);
        return result;
    }

    private static class BrushSlot extends Slot {

        public BrushSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return mayPlaceItem(stack);
        }

        public static boolean mayPlaceItem(ItemStack stack) {
            return stack.is(Items.BRUSH);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class WaterSlot extends Slot {

        public WaterSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return mayPlaceItem(stack);
        }

        public static boolean mayPlaceItem(ItemStack stack) {
            return stack.is(Items.WATER_BUCKET);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class InputSlot extends Slot {

        public InputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return mayPlaceItem(stack);
        }

        public static boolean mayPlaceItem(ItemStack stack) {
            return ArchaeologicalRecipes.isValidInput(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }

    private static class ResultSlot extends Slot {

        public ResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}