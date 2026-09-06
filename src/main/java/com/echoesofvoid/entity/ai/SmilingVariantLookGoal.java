package com.echoesofvoid.entity.ai;

import com.echoesofvoid.entity.SmilingVariantEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * Custom AI goal for the Smiling Variant.
 * Handles spawning behavior: appears behind the player in dark areas
 * and faces the player always.
 */
public class SmilingVariantLookGoal extends Goal {

    private final SmilingVariantEntity entity;
    private PlayerEntity targetPlayer;

    public SmilingVariantLookGoal(SmilingVariantEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.targetPlayer = entity.getWorld().getClosestPlayer(entity, 25.0);
        return this.targetPlayer != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.targetPlayer != null && this.targetPlayer.isAlive()
                && !this.targetPlayer.isRemoved();
    }

    @Override
    public void tick() {
        if (targetPlayer == null) return;

        // Always face the player (slow, creepy head turn)
        this.entity.getLookControl().lookAt(
                targetPlayer.getX(),
                targetPlayer.getY() + targetPlayer.getEyeHeight(targetPlayer.getPose()),
                targetPlayer.getZ(),
                30.0f, // slow rotation speed for creepiness
                30.0f
        );
    }
}
