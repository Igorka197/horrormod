package com.echoesofvoid.client.screen;

import com.echoesofvoid.EchoesOfTheVoid;
import com.echoesofvoid.client.EchoesClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Full-screen screamer overlay that shows a jumpscare image.
 * Rendered on the HUD after the screamer is triggered.
 */
@Environment(EnvType.CLIENT)
public class ScreamerOverlay {

    private static final Identifier SCREAMER_SMILE_TEXTURE =
            new Identifier(EchoesOfTheVoid.MOD_ID, "textures/entity/screamer_smile.png");
    private static final Identifier SCREAMER_STALKER_TEXTURE =
            new Identifier(EchoesOfTheVoid.MOD_ID, "textures/entity/screamer_stalker.png");
    private static final Identifier STATIC_OVERLAY_TEXTURE =
            new Identifier(EchoesOfTheVoid.MOD_ID, "textures/entity/static_overlay.png");

    private static final Identifier VIGNETTE_TEXTURE =
            new Identifier("textures/misc/vignette.png");

    public static void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // Render screamer overlay
        if (EchoesClient.screamerActive && EchoesClient.screamerTimer > 0) {
            renderScreamer(context, screenWidth, screenHeight);
            EchoesClient.screamerTimer--;
            if (EchoesClient.screamerTimer <= 0) {
                EchoesClient.screamerActive = false;
            }
        }

        // Render static noise overlay
        if (EchoesClient.staticNoiseActive && EchoesClient.staticNoiseTicks > 0) {
            renderStaticOverlay(context, screenWidth, screenHeight);
            EchoesClient.staticNoiseTicks--;
            if (EchoesClient.staticNoiseTicks <= 0) {
                EchoesClient.staticNoiseActive = false;
            }
        }
    }

    private static void renderScreamer(DrawContext context, int width, int height) {
        // Flash white-red background
        float progress = EchoesClient.screamerTimer / 10.0f;
        int alpha = (int) (200 * progress);

        // Red flash background
        context.fill(0, 0, width, height,
                (alpha << 24) | 0x1a0000);

        // Draw the screamer face texture
        Identifier screamerTex = EchoesClient.screamerType == 0
                ? SCREAMER_SMILE_TEXTURE
                : SCREAMER_STALKER_TEXTURE;

        try {
            // Render the screamer image centered and slightly zoomed
            int imgW = (int) (width * 1.2f);
            int imgH = (int) (height * 1.2f);
            int offsetX = (width - imgW) / 2;
            int offsetY = (height - imgH) / 2;

            // Add screen shake offset
            int shakeX = (int) (Math.random() * 20 - 10);
            int shakeY = (int) (Math.random() * 20 - 10);

            context.drawTexture(screamerTex,
                    offsetX + shakeX, offsetY + shakeY,
                    0, 0, imgW, imgH, imgW, imgH);
        } catch (Exception e) {
            // If texture not found, just draw red overlay with white smile
            context.fill(width / 4, height / 4, width * 3 / 4, height * 3 / 4, 0xFF000000);
            // Draw a simple smile indicator
            int centerX = width / 2;
            int centerY = height / 2;
            context.fill(centerX - 40, centerY + 10, centerX + 40, centerY + 14, 0xFFFFFFFF); // smile line
            context.fill(centerX - 50, centerY - 30, centerX - 30, centerY - 10, 0xFFFFFFFF); // left eye
            context.fill(centerX + 30, centerY - 30, centerX + 50, centerY - 10, 0xFFFFFFFF); // right eye
        }

        // Red vignette overlay for intensity
        context.fill(0, 0, width, 30, (150 << 24) | 0x330000);
        context.fill(0, height - 30, width, height, (150 << 24) | 0x330000);
    }

    private static void renderStaticOverlay(DrawContext context, int width, int height) {
        // Render static noise effect with flickering
        long time = System.currentTimeMillis();
        boolean flicker = (time / 50) % 3 == 0;

        if (flicker) {
            int staticAlpha = (int) (60 + Math.random() * 40);
            context.fill(0, 0, width, height, (staticAlpha << 24) | 0xFFFFFF);

            // Random horizontal lines (TV static effect)
            for (int i = 0; i < 15; i++) {
                int y = (int) (Math.random() * height);
                int lineH = (int) (Math.random() * 3 + 1);
                int lineAlpha = (int) (Math.random() * 100 + 50);
                context.fill(0, y, width, y + lineH, (lineAlpha << 24) | 0xAAAAAA);
            }
        }
    }
}
