package com.echoesofvoid.mixin;

import com.echoesofvoid.client.EchoesClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into GameRenderer for camera shake effects.
 * Uses safe injection point that works on all platforms including Android.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void echoes$onRender(CallbackInfo ci) {
        // Tick down camera shake counter each frame
        if (EchoesClient.cameraShakeTicks > 0) {
            EchoesClient.cameraShakeTicks--;
            if (EchoesClient.cameraShakeTicks <= 0) {
                EchoesClient.cameraShakeIntensity = 0;
            }
        }
    }
}
