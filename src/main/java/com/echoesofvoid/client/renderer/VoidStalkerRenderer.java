package com.echoesofvoid.client.renderer;

import com.echoesofvoid.EchoesOfTheVoid;
import com.echoesofvoid.client.EchoesClient;
import com.echoesofvoid.entity.VoidStalkerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Renders the Void Stalker as a 2D billboard sprite that always faces the camera.
 * Supports dynamic scaling based on proximity.
 */
@Environment(EnvType.CLIENT)
public class VoidStalkerRenderer extends EntityRenderer<VoidStalkerEntity> {

    public static final Identifier TEXTURE = new Identifier(EchoesOfTheVoid.MOD_ID, "textures/entity/void_stalker.png");

    public VoidStalkerRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.0f; // No shadow
    }

    @Override
    public Identifier getTexture(VoidStalkerEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(VoidStalkerEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Scale based on proximity
        float scale = entity.getProximityScale();
        matrices.scale(scale, scale * 1.5f, scale);

        // Billboard: face the camera
        matrices.multiply(dispatcher.getRotation());

        // If screamer triggered, flash the sprite
        float alpha = 0.85f;
        float red = 1.0f, green = 1.0f, blue = 1.0f;
        if (entity.isScreamerTriggered()) {
            // Glitch effect: flash between colors
            long time = entity.getWorld().getTime();
            if (time % 2 == 0) {
                red = 0.5f;
                blue = 0.5f;
            }
            alpha = 1.0f;
        }

        // Apply slight jitter for uncanny effect
        float jitterX = (float) (Math.sin(entity.age * 0.3) * 0.02);
        float jitterY = (float) (Math.cos(entity.age * 0.4) * 0.02);
        matrices.translate(jitterX, jitterY, 0);

        // Render as a quad (billboard sprite)
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                RenderLayer.getEntityTranslucent(TEXTURE));

        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Matrix3f normalMatrix = matrices.peek().getNormalMatrix();

        float halfWidth = 0.5f;
        float halfHeight = 1.0f;

        // Front face
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                -halfWidth, -halfHeight, 0, 0, 1, red, green, blue, alpha);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                halfWidth, -halfHeight, 0, 1, 1, red, green, blue, alpha);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                halfWidth, halfHeight, 0, 1, 0, red, green, blue, alpha);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                -halfWidth, halfHeight, 0, 0, 0, red, green, blue, alpha);

        // Back face (so it's visible from both sides)
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                halfWidth, -halfHeight, 0, 0, 1, red, green, blue, alpha);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                -halfWidth, -halfHeight, 0, 1, 1, red, green, blue, alpha);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                -halfWidth, halfHeight, 0, 1, 0, red, green, blue, alpha);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, light,
                halfWidth, halfHeight, 0, 0, 0, red, green, blue, alpha);

        matrices.pop();

        // Trigger client-side screamer
        if (entity.isScreamerTriggered() && !EchoesClient.screamerActive) {
            EchoesClient.triggerScreamer(1);
        }

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void addVertex(VertexConsumer consumer, Matrix4f posMatrix, Matrix3f normalMatrix,
                           int light, float x, float y, float z, float u, float v,
                           float red, float green, float blue, float alpha) {
        consumer.vertex(posMatrix, x, y, z)
                .color(red, green, blue, alpha)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(normalMatrix, 0.0f, 1.0f, 0.0f)
                .next();
    }
}
