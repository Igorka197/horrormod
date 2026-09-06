package com.echoesofvoid.client.model;

import com.echoesofvoid.EchoesOfTheVoid;
import com.echoesofvoid.entity.SmilingVariantEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Custom 3D model for the Smiling Variant.
 * Based on the Steve model but with unnaturally elongated limbs.
 */
public class SmilingVariantModel extends EntityModel<SmilingVariantEntity> {

    public static final Identifier TEXTURE = new Identifier(EchoesOfTheVoid.MOD_ID, "textures/entity/smiling_variant.png");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public SmilingVariantModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    /**
     * Create the textured model data with elongated limbs.
     */
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Head — slightly larger than normal
        root.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.5f, -9.0f, -4.5f, 9.0f, 9.0f, 9.0f),
                ModelTransform.pivot(0.0f, 1.0f, 0.0f));

        // Body — normal proportions but tall
        root.addChild("body",
                ModelPartBuilder.create()
                        .uv(16, 16).cuboid(-4.0f, 0.0f, -2.5f, 8.0f, 13.0f, 5.0f),
                ModelTransform.pivot(0.0f, 1.0f, 0.0f));

        // Right arm — ELONGATED (50% longer)
        root.addChild("right_arm",
                ModelPartBuilder.create()
                        .uv(40, 16).cuboid(-3.0f, -1.0f, -2.0f, 4.0f, 20.0f, 4.0f),
                ModelTransform.pivot(-7.0f, 2.0f, 0.0f));

        // Left arm — ELONGATED
        root.addChild("left_arm",
                ModelPartBuilder.create()
                        .uv(40, 16).mirrored()
                        .cuboid(-1.0f, -1.0f, -2.0f, 4.0f, 20.0f, 4.0f),
                ModelTransform.pivot(7.0f, 2.0f, 0.0f));

        // Right leg — ELONGATED
        root.addChild("right_leg",
                ModelPartBuilder.create()
                        .uv(0, 16).cuboid(-2.0f, 0.0f, -2.5f, 4.0f, 20.0f, 5.0f),
                ModelTransform.pivot(-2.0f, 14.0f, 0.0f));

        // Left leg — ELONGATED
        root.addChild("left_leg",
                ModelPartBuilder.create()
                        .uv(0, 16).mirrored()
                        .cuboid(-2.0f, 0.0f, -2.5f, 4.0f, 20.0f, 5.0f),
                ModelTransform.pivot(2.0f, 14.0f, 0.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(SmilingVariantEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Subtle idle animation — slight arm sway
        this.rightArm.pitch = (float) Math.sin(animationProgress * 0.05f) * 0.1f;
        this.leftArm.pitch = (float) Math.sin(animationProgress * 0.05f + Math.PI) * 0.1f;

        // Head tracks toward target
        this.head.yaw = headYaw * 0.017453292f;
        this.head.pitch = headPitch * 0.017453292f;

        // Legs stay mostly still (it doesn't really walk)
        this.rightLeg.pitch = 0;
        this.leftLeg.pitch = 0;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        body.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        rightArm.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        leftArm.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        rightLeg.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        leftLeg.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
