package net.corfaction.ancientartifacts.client.gui;

import net.corfaction.ancientartifacts.AncientArtifacts;
import net.corfaction.ancientartifacts.artifact.ArtifactPixelMask;
import net.corfaction.ancientartifacts.block.entity.ArchaeologicalTableBlockEntity;
import net.corfaction.ancientartifacts.block.menu.ArchaeologicalTableMenu;
import net.corfaction.ancientartifacts.item.ModItems;
import net.corfaction.ancientartifacts.network.CleanArchaeologicalPixelPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class ArchaeologicalTableScreen
        extends AbstractContainerScreen<ArchaeologicalTableMenu> {

    private static final Identifier ARCHAEOLOGICAL_TABLE_TEXTURE =
            AncientArtifacts.id("textures/gui/container/archaeological_table.png");

    private static final Identifier PETRIFIED_TALISMAN_TEXTURE =
            AncientArtifacts.id("textures/item/petrified_talisman.png");

    private static final Identifier GUARDIAN_TALISMAN_TEXTURE =
            AncientArtifacts.id("textures/item/guardian_talisman.png");

    private static final int ARTIFACT_X = 60;
    private static final int ARTIFACT_Y = 12;
    private static final int ARTIFACT_SIZE = 64;
    private static final int TEXTURE_SIZE = 16;
    private static final int PIXEL_SIZE = 4;

    private boolean cleaning;
    private ArtifactPixelMask pixelMask;

    public ArchaeologicalTableScreen(
            ArchaeologicalTableMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    private ArtifactPixelMask getPixelMask() {
        if (pixelMask == null) {
            pixelMask = ArtifactPixelMask.fromTexture(PETRIFIED_TALISMAN_TEXTURE);
        }

        return pixelMask;
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ARCHAEOLOGICAL_TABLE_TEXTURE,
                guiX,
                guiY,
                0.0F,
                0.0F,
                imageWidth,
                imageHeight,
                256,
                256
        );

        renderArtifact(graphics, guiX, guiY);
    }

    private void renderArtifact(
            GuiGraphicsExtractor graphics,
            int guiX,
            int guiY
    ) {
        ItemStack input = menu.getContainer().getItem(
                ArchaeologicalTableBlockEntity.INPUT_SLOT
        );

        if (input.isEmpty() || !input.is(ModItems.PETRIFIED_TALISMAN)) {
            return;
        }

        int artifactX = guiX + ARTIFACT_X;
        int artifactY = guiY + ARTIFACT_Y;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                GUARDIAN_TALISMAN_TEXTURE,
                artifactX,
                artifactY,
                0,
                0,
                ARTIFACT_SIZE,
                ARTIFACT_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                TEXTURE_SIZE
        );

        for (int pixelY = 0; pixelY < TEXTURE_SIZE; pixelY++) {
            for (int pixelX = 0; pixelX < TEXTURE_SIZE; pixelX++) {
                if (!getPixelMask().isPixelPresent(pixelX, pixelY)
                        || menu.isPixelCleaned(pixelX, pixelY)) {
                    continue;
                }

                int drawX = artifactX + pixelX * PIXEL_SIZE;
                int drawY = artifactY + pixelY * PIXEL_SIZE;

                graphics.blit(
                        RenderPipelines.GUI_TEXTURED,
                        PETRIFIED_TALISMAN_TEXTURE,
                        drawX,
                        drawY,
                        pixelX,
                        pixelY,
                        PIXEL_SIZE,
                        PIXEL_SIZE,
                        1,
                        1,
                        TEXTURE_SIZE,
                        TEXTURE_SIZE
                );
            }
        }
    }

    private int[] getArtifactPixel(double mouseX, double mouseY) {
        int guiX = (width - imageWidth) / 2;
        int guiY = (height - imageHeight) / 2;
        int artifactX = guiX + ARTIFACT_X;
        int artifactY = guiY + ARTIFACT_Y;

        int localX = (int) mouseX - artifactX;
        int localY = (int) mouseY - artifactY;

        if (localX < 0 || localY < 0
                || localX >= ARTIFACT_SIZE || localY >= ARTIFACT_SIZE) {
            return null;
        }

        return new int[]{
                localX / PIXEL_SIZE,
                localY / PIXEL_SIZE
        };
    }

    private boolean tryCleanPixel(double mouseX, double mouseY) {
        int[] pixel = getArtifactPixel(mouseX, mouseY);

        if (pixel == null) {
            return false;
        }

        if (!getPixelMask().isPixelPresent(pixel[0], pixel[1])) {
            return false;
        }

        if (menu.isPixelCleaned(pixel[0], pixel[1])) {
            return true;
        }

        ClientPlayNetworking.send(
                new CleanArchaeologicalPixelPayload(pixel[0], pixel[1])
        );

        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && tryCleanPixel(event.x(), event.y())) {
            cleaning = true;
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(
            MouseButtonEvent event,
            double deltaX,
            double deltaY
    ) {
        if (event.button() == 0 && cleaning
                && tryCleanPixel(event.x(), event.y())) {
            return true;
        }

        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            cleaning = false;
        }

        return super.mouseReleased(event);
    }
}