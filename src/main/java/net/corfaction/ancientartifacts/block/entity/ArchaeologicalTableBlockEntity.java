package net.corfaction.ancientartifacts.block.entity;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.artifact.ArchaeologicalRecipe;
import net.corfaction.ancientartifacts.artifact.ArchaeologicalRecipes;
import net.corfaction.ancientartifacts.artifact.ArtifactPixelMask;
import net.corfaction.ancientartifacts.block.menu.ArchaeologicalTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ArchaeologicalTableBlockEntity extends BaseContainerBlockEntity {

    public static final int BRUSH_SLOT = 0;
    public static final int WATER_SLOT = 1;
    public static final int INPUT_SLOT = 2;
    public static final int RESULT_SLOT = 3;

    public static final int SLOT_COUNT = 4;

    public static final int PIXEL_DATA_START = 0;
    public static final int PIXEL_DATA_COUNT = 16;
    public static final int DATA_COUNT = PIXEL_DATA_COUNT;

    private static final int PIXEL_SIZE = 16;

    private static final Component DEFAULT_NAME =
            Component.translatable(
                    "container.ancient-artifacts.archaeological_table"
            );

    private final RandomSource random = RandomSource.create();

    private final int[] cleanedPixels =
            new int[PIXEL_DATA_COUNT];

    private NonNullList<ItemStack> items =
            NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    private ArtifactPixelMask pixelMask;

    private Identifier currentTexture;

    protected final ContainerData dataAccess = new ContainerData() {

        @Override
        public int get(int dataId) {
            if (dataId >= PIXEL_DATA_START
                    && dataId < PIXEL_DATA_START + PIXEL_DATA_COUNT) {

                return cleanedPixels[dataId - PIXEL_DATA_START];
            }

            return 0;
        }

        @Override
        public void set(int dataId, int value) {
            if (dataId >= PIXEL_DATA_START
                    && dataId < PIXEL_DATA_START + PIXEL_DATA_COUNT) {

                cleanedPixels[dataId - PIXEL_DATA_START] = value;
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public ArchaeologicalTableBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                ModBlockEntityTypes.ARCHAEOLOGICAL_TABLE,
                pos,
                state
        );
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    /**
     * Returns the recipe for the current input item.
     */
    public ArchaeologicalRecipe getCurrentRecipe() {
        ItemStack input = items.get(INPUT_SLOT);

        if (input.isEmpty()) {
            return null;
        }

        return ArchaeologicalRecipes.getRecipe(input);
    }

    /**
     * Returns the pixel mask for the currently inserted artifact.
     */
    public ArtifactPixelMask getPixelMask() {
        ArchaeologicalRecipe recipe = getCurrentRecipe();

        if (recipe == null) {
            return null;
        }

        Identifier texture = recipe.texture();

        if (pixelMask == null
                || !texture.equals(currentTexture)) {

            pixelMask = ArtifactPixelMask.fromTexture(texture);
            currentTexture = texture;
        }

        return pixelMask;
    }

    /**
     * Resets all cleaned pixels.
     */
    public void resetCleaning() {
        for (int i = 0; i < cleanedPixels.length; i++) {
            cleanedPixels[i] = 0;
        }
    }

    /**
     * Checks whether a specific pixel has already been cleaned.
     */
    public boolean isPixelCleaned(int x, int y) {
        if (x < 0
                || x >= PIXEL_SIZE
                || y < 0
                || y >= PIXEL_SIZE) {

            return false;
        }

        int index = y * PIXEL_SIZE + x;

        int dataIndex = index / 16;
        int bitIndex = index % 16;

        return (cleanedPixels[dataIndex]
                & (1 << bitIndex)) != 0;
    }

    /**
     * Checks whether every present pixel has been cleaned.
     */
    public boolean isFullyCleaned() {
        ArtifactPixelMask mask = getPixelMask();

        if (mask == null) {
            return false;
        }

        return mask.isFullyCleaned(cleanedPixels);
    }

    /**
     * Damages the brush with a 50% chance when cleaning a pixel.
     */
    private void damageBrush() {
        ItemStack brush = items.get(BRUSH_SLOT);

        if (brush.isEmpty()) {
            return;
        }

        int damage = brush.getDamageValue() + 1;

        if (damage >= brush.getMaxDamage()) {
            items.set(BRUSH_SLOT, ItemStack.EMPTY);
        } else {
            brush.setDamageValue(damage);
        }

        setChanged();
    }

    /**
     * Cleans one artifact pixel.
     */
    public boolean cleanPixel(
            Player player,
            int x,
            int y
    ) {
        if (level == null || level.isClientSide()) {
            return false;
        }

        if (x < 0
                || x >= PIXEL_SIZE
                || y < 0
                || y >= PIXEL_SIZE) {

            return false;
        }

        ArchaeologicalRecipe recipe = getCurrentRecipe();

        if (recipe == null) {
            return false;
        }

        ArtifactPixelMask mask = getPixelMask();

        if (mask == null || !mask.isPixelPresent(x, y)) {
            return false;
        }

        ItemStack brush = items.get(BRUSH_SLOT);
        ItemStack water = items.get(WATER_SLOT);
        ItemStack input = items.get(INPUT_SLOT);
        ItemStack result = items.get(RESULT_SLOT);

        /*
         * Required tools/materials.
         */

        if (brush.isEmpty() || !brush.is(Items.BRUSH)) {
            return false;
        }

        if (water.isEmpty()
                || !water.is(Items.WATER_BUCKET)) {

            return false;
        }

        if (!recipe.matches(input)) {
            return false;
        }

        if (!result.isEmpty()) {
            return false;
        }

        if (isPixelCleaned(x, y)) {
            return false;
        }

        /*
         * Mark pixel as cleaned.
         */

        int index = y * PIXEL_SIZE + x;

        int dataIndex = index / 16;
        int bitIndex = index % 16;

        cleanedPixels[dataIndex] |= 1 << bitIndex;

        /*
         * Randomly damage the brush.
         */

        if (player.getRandom().nextFloat() <= 0.2F) {
            damageBrush();
        }

        setChanged();

        /*
         * Finish the artifact if every pixel
         * has been cleaned.
         */

        if (isFullyCleaned()) {
            finishCleaning(recipe);
        }

        return true;
    }

    /**
     * Finishes the archaeological process.
     */
    private void finishCleaning(
            ArchaeologicalRecipe recipe
    ) {
        ItemStack input = items.get(INPUT_SLOT);

        if (!recipe.matches(input)) {
            return;
        }

        /*
         * Remove the dirty artifact.
         */

        items.set(
                INPUT_SLOT,
                ItemStack.EMPTY
        );

        /*
         * Create the clean artifact.
         */

        items.set(
                RESULT_SLOT,
                new ItemStack(recipe.output())
        );

        /*
         * Return an empty bucket.
         */

        items.set(
                WATER_SLOT,
                new ItemStack(Items.BUCKET)
        );

        /*
         * Reset the cleaning state.
         */

        resetCleaning();

        pixelMask = null;
        currentTexture = null;

        setChanged();
    }

    @Override
    public boolean canPlaceItem(
            int slot,
            ItemStack stack
    ) {
        return switch (slot) {
            case BRUSH_SLOT ->
                    stack.is(Items.BRUSH);

            case WATER_SLOT ->
                    stack.is(Items.WATER_BUCKET);

            case INPUT_SLOT ->
                    ArchaeologicalRecipes.isValidInput(stack);

            case RESULT_SLOT ->
                    false;

            default ->
                    false;
        };
    }

    @Override
    protected void loadAdditional(
            ValueInput input
    ) {
        super.loadAdditional(input);

        items = NonNullList.withSize(
                SLOT_COUNT,
                ItemStack.EMPTY
        );

        ContainerHelper.loadAllItems(
                input,
                items
        );

        for (int i = 0; i < PIXEL_DATA_COUNT; i++) {
            cleanedPixels[i] =
                    input.getIntOr(
                            "CleanedPixels" + i,
                            0
                    );
        }

        /*
         * The mask is reconstructed lazily from
         * the current input item.
         */

        pixelMask = null;
        currentTexture = null;
    }

    @Override
    protected void saveAdditional(
            ValueOutput output
    ) {
        super.saveAdditional(output);

        ContainerHelper.saveAllItems(
                output,
                items
        );

        for (int i = 0; i < PIXEL_DATA_COUNT; i++) {
            output.putInt(
                    "CleanedPixels" + i,
                    cleanedPixels[i]
            );
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory
    ) {
        return new ArchaeologicalTableMenu(
                containerId,
                inventory,
                this,
                dataAccess
        );
    }
}