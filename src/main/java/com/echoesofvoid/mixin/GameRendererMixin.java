package com.echoesofvoid.mixin;

import com.echoesofvoid.client.EchoesClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into GameRenderer to apply camera shake effects
 * when the player is staring at the Smiling Variant.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V"))
    private void echoes$onRenderHand(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (EchoesClient.cameraShakeTicks > 0) {
            float intensity = EchoesClient.cameraShakeIntensity;
            float shakeX = (float) ((Math.random() - 0.5) * intensity * 0.02);
            float shakeY = (float) ((Math.random() - 0.5) * intensity * 0.02);
            float shakeZ = (float) ((Math.random() - 0.5) * intensity * 0.01);
            matrices.translate(shakeX, shakeY, shakeZ);

            EchoesClient.cameraShakeTicks--;
            if (EchoesClient.cameraShakeTicks <= 0) {
                EchoesClient.cameraShakeIntensity = 0;
            }
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    private void echoes$beforeRenderHand(CallbackInfo ci) {
        // Additional camera manipulation can go here if needed
    }
}
