package com.echoesofvoid.mixin;

import com.echoesofvoid.client.EchoesClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to detect when the client player encounters entities
 * and trigger client-side effects accordingly.
 */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void echoes$onTick(CallbackInfo ci) {
        // Client-side tick effects are handled in entity renderers and overlay
    }
}
