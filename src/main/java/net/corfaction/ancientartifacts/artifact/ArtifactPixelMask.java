package net.corfaction.ancientartifacts.artifact;

import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class ArtifactPixelMask {

    private static final int WIDTH = 16;
    private static final int HEIGHT = 16;

    private final boolean[][] mask;

    private ArtifactPixelMask(boolean[][] mask) {
        this.mask = mask;
    }

    public static ArtifactPixelMask fromTexture(Identifier texture) {
        String resourcePath = "assets/" + texture.getNamespace() + "/" + texture.getPath();

        try (InputStream stream = ArtifactPixelMask.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (stream == null) {
                throw new IllegalStateException(
                        "Could not find artifact texture: " + resourcePath
                );
            }

            BufferedImage image = ImageIO.read(stream);

            if (image == null) {
                throw new IllegalStateException(
                        "Could not read artifact texture: " + resourcePath
                );
            }

            if (image.getWidth() != WIDTH || image.getHeight() != HEIGHT) {
                throw new IllegalArgumentException(
                        "Artifact texture must be 16x16: " + resourcePath +
                                " (" + image.getWidth() + "x" + image.getHeight() + ")"
                );
            }

            boolean[][] mask = new boolean[HEIGHT][WIDTH];

            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    int argb = image.getRGB(x, y);
                    int alpha = (argb >> 24) & 0xFF;
                    mask[y][x] = alpha > 0;
                }
            }

            return new ArtifactPixelMask(mask);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load artifact texture: " + resourcePath,
                    e
            );
        }
    }

    public boolean isPixelPresent(int x, int y) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return false;
        }

        return mask[y][x];
    }

    public boolean isFullyCleaned(int[] cleanedPixels) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (!isPixelPresent(x, y)) {
                    continue;
                }

                int index = y * WIDTH + x;
                int dataIndex = index / 16;
                int bitIndex = index % 16;

                if ((cleanedPixels[dataIndex] & (1 << bitIndex)) == 0) {
                    return false;
                }
            }
        }

        return true;
    }
}