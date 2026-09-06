package com.echoesofvoid.entity.ai;

import com.echoesofvoid.entity.VoidStalkerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * Void Stalker follow AI: slowly follows the player while staying in their peripheral vision.
 * Tries to position itself at the edges of the player's view for maximum creepiness.
 */
public class VoidStalkerFollowGoal extends Goal {

    private final VoidStalkerEntity entity;
    private PlayerEntity targetPlayer;
    private int tickCounter = 0;

    public VoidStalkerFollowGoal(VoidStalkerEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.targetPlayer = entity.getWorld().getClosestPlayer(entity, 25.0);
        return this.targetPlayer != null;
    }

    @Override
    public boolean shouldContinue() {
        return this.targetPlayer != null && this.targetPlayer.isAlive()
                && !this.targetPlayer.isRemoved()
                && entity.distanceTo(targetPlayer) < 25.0;
    }

    @Override
    public void tick() {
        if (targetPlayer == null) return;
        tickCounter++;

        double distance = entity.distanceTo(targetPlayer);

        // Target distance: 6-10 blocks (lurking distance)
        double targetDist = 8.0;

        // Calculate position in the player's peripheral vision
        // Get the player's look direction and rotate it ~70-90 degrees to the side
        Vec3d playerLook = targetPlayer.getRotationVec(1.0f).normalize();

        // Alternate sides every 5 seconds for creepiness
        double angle = (tickCounter / 100) % 2 == 0 ? Math.toRadians(80) : Math.toRadians(-80);

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        // Rotate the look vector
        double sideX = playerLook.x * cos - playerLook.z * sin;
        double sideZ = playerLook.x * sin + playerLook.z * cos;

        // Target position: beside/behind the player at target distance
        double targetX = targetPlayer.getX() + sideX * targetDist;
        double targetY = targetPlayer.getY() + 1.0; // Hover slightly above ground
        double targetZ = targetPlayer.getZ() + sideZ * targetDist;

        // Slow movement toward target position
        double dx = targetX - entity.getX();
        double dy = targetY - entity.getY();
        double dz = targetZ - entity.getZ();

        double speed = 0.06;
        entity.setVelocity(
                entity.getVelocity().x * 0.8 + dx * 0.02,
                entity.getVelocity().y * 0.8 + dy * 0.02,
                entity.getVelocity().z * 0.8 + dz * 0.02
        );

        // Face the player
        entity.getLookControl().lookAt(
                targetPlayer.getX(),
                targetPlayer.getY() + targetPlayer.getEyeHeight(targetPlayer.getPose()),
                targetPlayer.getZ(),
                40.0f, 40.0f
        );
    }
}
