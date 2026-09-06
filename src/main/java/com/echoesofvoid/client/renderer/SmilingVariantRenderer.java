package com.echoesofvoid.client.renderer;

import com.echoesofvoid.EchoesOfTheVoid;
import com.echoesofvoid.client.EchoesClient;
import com.echoesofvoid.client.model.SmilingVariantModel;
import com.echoesofvoid.entity.SmilingVariantEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SmilingVariantRenderer extends MobEntityRenderer<SmilingVariantEntity, SmilingVariantModel> {

    public static final EntityModelLayer MODEL_LAYER = new EntityModelLayer(
            new Identifier(EchoesOfTheVoid.MOD_ID, "smiling_variant"), "main");

    public SmilingVariantRenderer(EntityRendererFactory.Context context) {
        super(context, new SmilingVariantModel(context.getPart(MODEL_LAYER)), 0.5f);
        this.addFeature(new SmilingVariantEyesFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(SmilingVariantEntity entity) {
        return SmilingVariantModel.TEXTURE;
    }

    @Override
    public void render(SmilingVariantEntity entity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        // Scale the model slightly taller and thinner
        matrixStack.push();
        matrixStack.scale(1.0f, 1.3f, 0.9f);
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();

        // Trigger screamer on client if flagged
        if (entity.isScreamerActive() && !EchoesClient.screamerActive) {
            EchoesClient.triggerScreamer(0);
        }

        // Trigger camera shake if being stared at
        if (entity.isStaring() && entity.getStareTicks() > 60) {
            float shake = Math.min(3.0f, (entity.getStareTicks() - 60) / 10.0f);
            EchoesClient.startCameraShake(shake, 5);
            EchoesClient.startStaticNoise(5);
        }
    }
}
