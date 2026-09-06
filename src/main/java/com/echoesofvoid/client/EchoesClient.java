package com.echoesofvoid.client;

import com.echoesofvoid.EchoesOfTheVoid;
import com.echoesofvoid.client.renderer.SmilingVariantRenderer;
import com.echoesofvoid.client.renderer.VoidStalkerRenderer;
import com.echoesofvoid.client.screen.ScreamerOverlay;
import com.echoesofvoid.entity.SmilingVariantEntity;
import com.echoesofvoid.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class EchoesClient implements ClientModInitializer {

    // Static fields for client-side effects
    public static int screamerTimer = 0;
    public static boolean screamerActive = false;
    public static int screamerType = 0; // 0 = smiling, 1 = void stalker
    public static float cameraShakeIntensity = 0f;
    public static int cameraShakeTicks = 0;
    public static boolean staticNoiseActive = false;
    public static int staticNoiseTicks = 0;

    @Override
    public void onInitializeClient() {
        // Register entity renderers
        EntityRendererRegistry.register(ModEntities.SMILING_VARIANT, SmilingVariantRenderer::new);
        EntityRendererRegistry.register(ModEntities.VOID_STALKER, VoidStalkerRenderer::new);

        // Register HUD overlay for screamer effects
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            ScreamerOverlay.render(drawContext, tickDelta);
        });

        EchoesOfTheVoid.LOGGER.info("Client initialized — close your eyes.");
    }

    /**
     * Called from entity logic to trigger the screamer overlay on the client.
     */
    public static void triggerScreamer(int type) {
        screamerActive = true;
        screamerType = type;
        screamerTimer = 10; // 0.5 seconds at 20 tps
    }

    /**
     * Called to start camera shake effect.
     */
    public static void startCameraShake(float intensity, int ticks) {
        cameraShakeIntensity = intensity;
        cameraShakeTicks = ticks;
    }

    /**
     * Called to activate static noise effect.
     */
    public static void startStaticNoise(int ticks) {
        staticNoiseActive = true;
        staticNoiseTicks = ticks;
    }
}
