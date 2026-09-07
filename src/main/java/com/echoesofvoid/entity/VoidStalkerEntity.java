package com.echoesofvoid.entity;

import com.echoesofvoid.entity.ai.VoidStalkerFollowGoal;
import com.echoesofvoid.effect.ModEffects;
import com.echoesofvoid.registry.ModEntities;
import com.echoesofvoid.registry.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Void Stalker — the 2D sprite horror entity.
 *
 * Mechanics:
 * - Spawns only in caves (below Y=50) and during thunder/fog
 * - Does not attack directly; slowly follows the player from corners of vision
 * - As it gets closer, applies Darkness effect and plays footsteps behind the player
 * - If the player looks directly at it: glitch screamer → disappears and applies Void Curse
 */
public class VoidStalkerEntity extends HostileEntity {

    private static final TrackedData<Boolean> SCREAMER_TRIGGERED = DataTracker.registerData(
            VoidStalkerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> PROXIMITY_SCALE = DataTracker.registerData(
            VoidStalkerEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private int lifetime = 0;
    private int footstepCooldown = 0;
    private boolean triggered = false;

    public VoidStalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 0;
        this.noClip = true; // Floats through blocks
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.06)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 30.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    /**
     * Spawn condition: only in caves (below Y=50) or during thunder.
     */
    public static boolean canSpawnInCaves(EntityType<VoidStalkerEntity> type,
            net.minecraft.world.ServerWorldAccess world, SpawnReason spawnReason,
            BlockPos pos, net.minecraft.util.math.random.Random random) {
        boolean isCave = pos.getY() < 50;
        boolean isThunder = world.isThundering();
        return (isCave || isThunder) && HostileEntity.canSpawnInDark(type, world, spawnReason, pos, random);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SCREAMER_TRIGGERED, false);
        this.dataTracker.startTracking(PROXIMITY_SCALE, 1.0f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new VoidStalkerFollowGoal(this));
    }

    public boolean isScreamerTriggered() {
        return this.dataTracker.get(SCREAMER_TRIGGERED);
    }

    public void setScreamerTriggered(boolean triggered) {
        this.dataTracker.set(SCREAMER_TRIGGERED, triggered);
    }

    public float getProximityScale() {
        return this.dataTracker.get(PROXIMITY_SCALE);
    }

    public void setProximityScale(float scale) {
        this.dataTracker.set(PROXIMITY_SCALE, scale);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) return;

        lifetime++;

        // Despawn after 5 minutes if not triggered
        if (lifetime > 6000 && !triggered) {
            this.discard();
            return;
        }

        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 20.0);
        if (nearestPlayer == null) return;

        double distance = this.distanceTo(nearestPlayer);

        // Update proximity scale (closer = bigger sprite for renderer)
        float scale = (float) Math.max(0.5, 3.0 - (distance / 7.0));
        setProximityScale(scale);

        // Play footsteps from behind when close
        if (distance < 12.0) {
            footstepCooldown--;
            if (footstepCooldown <= 0) {
                // Play footstep sound slightly behind the player
                Vec3d behind = nearestPlayer.getRotationVec(1.0f).multiply(-2.0);
                this.getWorld().playSound(null,
                        nearestPlayer.getBlockPos().add((int) behind.x, 0, (int) behind.z),
                        ModSounds.WHISPER_FOOTSTEPS, SoundCategory.AMBIENT, 1.5f, 0.6f);
                footstepCooldown = 40; // Every 2 seconds
            }
        }

        // Apply darkness effect when close (< 10 blocks)
        if (distance < 10.0) {
            nearestPlayer.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.DARKNESS, 60, 0, false, false));
        }

        // Check if player is looking at the stalker
        if (isPlayerLookingAt(nearestPlayer) && distance < 15.0 && !triggered) {
            triggerScreamer(nearestPlayer);
        }
    }

    private boolean isPlayerLookingAt(PlayerEntity player) {
        Vec3d lookVec = player.getRotationVec(1.0f).normalize();
        Vec3d toEntity = new Vec3d(
                this.getX() - player.getX(),
                (this.getY() + 1.0) - (player.getY() + player.getEyeHeight(player.getPose())),
                this.getZ() - player.getZ()).normalize();

        double dot = lookVec.dotProduct(toEntity);
        return dot > 0.97; // Narrow cone for 2D sprite
    }

    private void triggerScreamer(PlayerEntity player) {
        if (triggered) return;
        triggered = true;
        setScreamerTriggered(true);

        // Play void scream
        this.getWorld().playSound(null, this.getBlockPos(),
                ModSounds.VOID_SCREAM, SoundCategory.HOSTILE, 3.0f, 0.4f);

        // Play static
        this.getWorld().playSound(null, this.getBlockPos(),
                ModSounds.STATIC_NOISE, SoundCategory.HOSTILE, 2.0f, 1.0f);

        // Apply Void Curse effect for 60 seconds (1200 ticks)
        player.addStatusEffect(new StatusEffectInstance(ModEffects.VOID_CURSE, 1200, 0));

        // Apply brief darkness
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 1));

        // Despawn after short delay (screamer overlay plays on client)
        this.discard();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }

    @Override
    protected boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Triggered", triggered);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        triggered = nbt.getBoolean("Triggered");
    }
}
