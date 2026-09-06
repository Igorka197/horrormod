package com.echoesofvoid.entity;

import com.echoesofvoid.entity.ai.SmilingVariantLookGoal;
import com.echoesofvoid.registry.ModEntities;
import com.echoesofvoid.registry.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.LightType;

/**
 * Smiling Variant — the 3D horror entity.
 *
 * Mechanics:
 * - Spawns behind the player in dark areas (light < 4)
 * - Freezes when the player looks at it (raycast check)
 * - If stared at >3 sec: camera shake + static noise
 * - If player approaches <5 blocks: screamer overlay + 12 damage + Blindness 10s
 * - Teleports away after screamer
 */
public class SmilingVariantEntity extends HostileEntity {

    private static final TrackedData<Boolean> IS_STARING = DataTracker.registerData(
            SmilingVariantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> STARE_TICKS = DataTracker.registerData(
            SmilingVariantEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SCREAMER_ACTIVE = DataTracker.registerData(
            SmilingVariantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int despawnTimer = 0;
    private boolean hasDoneScreamer = false;

    public SmilingVariantEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 0;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    /**
     * Spawn condition: only in dark areas (light < 4), on solid ground.
     */
    public static boolean canSpawnInDark(EntityType<SmilingVariantEntity> type,
            net.minecraft.world.ServerWorldAccess world, SpawnReason spawnReason,
            BlockPos pos, java.util.Random random) {
        return world.getLightLevel(pos) < 4 && HostileEntity.canSpawnInDark(type, world, spawnReason, pos, random);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_STARING, false);
        this.dataTracker.startTracking(STARE_TICKS, 0);
        this.dataTracker.startTracking(SCREAMER_ACTIVE, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SmilingVariantLookGoal(this));
    }

    public boolean isStaring() {
        return this.dataTracker.get(IS_STARING);
    }

    public void setStaring(boolean staring) {
        this.dataTracker.set(IS_STARING, staring);
    }

    public int getStareTicks() {
        return this.dataTracker.get(STARE_TICKS);
    }

    public void setStareTicks(int ticks) {
        this.dataTracker.set(STARE_TICKS, ticks);
    }

    public boolean isScreamerActive() {
        return this.dataTracker.get(SCREAMER_ACTIVE);
    }

    public void setScreamerActive(boolean active) {
        this.dataTracker.set(SCREAMER_ACTIVE, active);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) return;

        despawnTimer++;
        if (despawnTimer > 600) {
            this.discard();
            return;
        }

        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 25.0);
        if (nearestPlayer == null) return;

        double distance = this.distanceTo(nearestPlayer);

        // Screamer: player gets too close (< 5 blocks)
        if (distance < 5.0 && !hasDoneScreamer) {
            triggerScreamer(nearestPlayer);
            return;
        }

        // Check if player is looking at the entity
        if (isPlayerLookingAt(nearestPlayer)) {
            if (!isStaring()) {
                setStaring(true);
                setStareTicks(0);
            }
            int ticks = getStareTicks() + 1;
            setStareTicks(ticks);

            // After 3 seconds (60 ticks) — static noise + particles
            if (ticks == 60) {
                nearestPlayer.playSound(ModSounds.STATIC_NOISE, SoundCategory.HOSTILE, 2.0f, 0.8f);
                if (this.getWorld() instanceof ServerWorld sw) {
                    sw.spawnParticles(ParticleTypes.ASH,
                            this.getX(), this.getY() + 1.5, this.getZ(),
                            30, 0.5, 0.5, 0.5, 0.02);
                }
            }
        } else {
            if (isStaring()) {
                setStaring(false);
                setStareTicks(0);
            }
        }
    }

    /**
     * Check if the player is looking directly at this entity.
     */
    private boolean isPlayerLookingAt(PlayerEntity player) {
        Vec3d lookVec = player.getRotationVec(1.0f).normalize();
        Vec3d toEntity = new Vec3d(
                this.getX() - player.getX(),
                (this.getY() + this.getHeight() / 2.0) - (player.getY() + player.getEyeHeight(player.getPose())),
                this.getZ() - player.getZ()).normalize();

        double dot = lookVec.dotProduct(toEntity);
        return dot > 0.965;
    }

    /**
     * Trigger the screamer sequence.
     */
    private void triggerScreamer(PlayerEntity player) {
        if (hasDoneScreamer) return;
        hasDoneScreamer = true;

        this.getWorld().playSound(null, this.getBlockPos(),
                ModSounds.SCREAMER, SoundCategory.HOSTILE, 3.0f, 0.7f);

        // Deal 12 damage (6 hearts)
        player.damage(this.getDamageSources().mobAttack(this), 12.0f);

        // Apply Blindness for 10 seconds (200 ticks)
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 0));

        setScreamerActive(true);

        this.getWorld().playSound(null, this.getBlockPos(),
                ModSounds.VOID_SCREAM, SoundCategory.HOSTILE, 2.0f, 0.5f);

        // Will despawn in ~20 more ticks
        this.despawnTimer = 580;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("StareTicks", getStareTicks());
        nbt.putBoolean("HasDoneScreamer", hasDoneScreamer);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setStareTicks(nbt.getInt("StareTicks"));
        hasDoneScreamer = nbt.getBoolean("HasDoneScreamer");
    }

    @Override
    protected boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }
}
