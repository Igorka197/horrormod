package com.echoesofvoid.client.renderer;

import com.echoesofvoid.client.model.SmilingVariantModel;
import com.echoesofvoid.entity.SmilingVariantEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Renders glowing white eyes on the Smiling Variant.
 * Creates an eerie glowing effect visible in the dark.
 */
@Environment(EnvType.CLIENT)
public class SmilingVariantEyesFeatureRenderer extends FeatureRenderer<SmilingVariantEntity, SmilingVariantModel> {

    public SmilingVariantEyesFeatureRenderer(FeatureRendererContext<SmilingVariantEntity, SmilingVariantModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider,
                       int light, SmilingVariantEntity entity, float limbAngle, float limbDistance,
                       float tickDelta, float animationProgress, float headYaw, float headPitch) {
        // Render eyes as bright overlay at full brightness (15)
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEyes(SmilingVariantModel.TEXTURE));
        this.getContextModel().render(matrixStack, vertexConsumer, 15728640, // Fullbright
                0, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}
