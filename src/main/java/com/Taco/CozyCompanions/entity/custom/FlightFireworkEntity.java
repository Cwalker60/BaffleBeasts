package com.Taco.CozyCompanions.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class FlightFireworkEntity extends FireworkRocketEntity implements ItemSupplier {
    // TO DO FIX CLASS

    private static final EntityDataAccessor<ItemStack> DATA_ID_FIREWORKS_ITEM = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<OptionalInt> DATA_ATTACHED_TO_TARGET = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final EntityDataAccessor<Boolean> DATA_SHOT_AT_ANGLE = SynchedEntityData.defineId(FireworkRocketEntity.class, EntityDataSerializers.BOOLEAN);
    private int life;
    private int lifetime;
    private ItemStack firework;
    @Nullable
    private LivingEntity attachedToEntity;

    public FlightFireworkEntity(EntityType<? extends FlightFireworkEntity> pEntityType, Level pLevel) {
        super(pEntityType,pLevel);

    }

    public void setFirework(ItemStack is) {
        this.firework = is;
    }

    public void setDataAttachedToTarget(LivingEntity shooter) {
        this.attachedToEntity = shooter;
        this.entityData.set(DATA_ATTACHED_TO_TARGET, OptionalInt.of(shooter.getId()));
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_FIREWORKS_ITEM, ItemStack.EMPTY);
        this.entityData.define(DATA_ATTACHED_TO_TARGET, OptionalInt.empty());
        this.entityData.define(DATA_SHOT_AT_ANGLE, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Life", this.life);
        pCompound.putInt("LifeTime", this.lifetime);
        ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        if (!itemstack.isEmpty()) {
            pCompound.put("FireworksItem", itemstack.save(new CompoundTag()));
        }

        pCompound.putBoolean("ShotAtAngle", this.entityData.get(DATA_SHOT_AT_ANGLE));
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.life = pCompound.getInt("Life");
        this.lifetime = pCompound.getInt("LifeTime");
        ItemStack itemstack = ItemStack.of(pCompound.getCompound("FireworksItem"));
        if (!itemstack.isEmpty()) {
            this.entityData.set(DATA_ID_FIREWORKS_ITEM, itemstack);
        }

        if (pCompound.contains("ShotAtAngle")) {
            this.entityData.set(DATA_SHOT_AT_ANGLE, pCompound.getBoolean("ShotAtAngle"));
        }

    }

    private boolean isAttachedToEntity() {
        return this.entityData.get(DATA_ATTACHED_TO_TARGET).isPresent();
    }

    private void explode() {
        this.level.broadcastEntityEvent(this, (byte)17);
        this.gameEvent(GameEvent.EXPLODE, this.getOwner());
        this.discard();
    }

    private boolean hasExplosion() {
        ItemStack itemstack = this.entityData.get(DATA_ID_FIREWORKS_ITEM);
        CompoundTag compoundtag = itemstack.isEmpty() ? null : itemstack.getTagElement("Fireworks");
        ListTag listtag = compoundtag != null ? compoundtag.getList("Explosions", 10) : null;
        return listtag != null && !listtag.isEmpty();
    }
    @Override
    public void tick() {
        super.tick();
        if (this.isAttachedToEntity()) {
            if (this.attachedToEntity == null) {
                this.entityData.get(DATA_ATTACHED_TO_TARGET).ifPresent((p_37067_) -> {
                    Entity entity = this.level.getEntity(p_37067_);
                    if (entity instanceof LivingEntity) {
                        this.attachedToEntity = (LivingEntity)entity;
                    }

                });
            }

            if (this.attachedToEntity != null) {
                Vec3 vec3;
                if (this.attachedToEntity.isFallFlying()) {
                    Vec3 vec31 = this.attachedToEntity.getLookAngle();
                    double d0 = 1.5D;
                    double d1 = 0.1D;
                    Vec3 vec32 = this.attachedToEntity.getDeltaMovement();
                    this.attachedToEntity.setDeltaMovement(vec32.add(vec31.x * 0.1D + (vec31.x * 1.5D - vec32.x) * 0.5D, vec31.y * 0.1D + (vec31.y * 1.5D - vec32.y) * 0.5D, vec31.z * 0.1D + (vec31.z * 1.5D - vec32.z) * 0.5D));
                    vec3 = this.attachedToEntity.getHandHoldingItemAngle(Items.FIREWORK_ROCKET);
                } else {
                    vec3 = Vec3.ZERO;
                }

                this.setPos(this.attachedToEntity.getX() + vec3.x, this.attachedToEntity.getY() + vec3.y, this.attachedToEntity.getZ() + vec3.z);
                this.setDeltaMovement(this.attachedToEntity.getDeltaMovement());
            }
        } else {
            if (!this.isShotAtAngle()) {
                double d2 = this.horizontalCollision ? 1.0D : 1.15D;
                this.setDeltaMovement(this.getDeltaMovement().multiply(d2, 1.0D, d2).add(0.0D, 0.04D, 0.0D));
            }

            Vec3 vec33 = this.getDeltaMovement();
            this.move(MoverType.SELF, vec33);
            this.setDeltaMovement(vec33);
        }

        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        if (!this.noPhysics) {
            this.onHit(hitresult);
            this.hasImpulse = true;
        }

        this.updateRotation();
        if (this.life == 0 && !this.isSilent()) {
            //this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.AMBIENT, 3.0F, 1.0F);
        }

        ++this.life;
        if (this.level.isClientSide && this.life % 2 < 2) {
            this.level.addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.05D, -this.getDeltaMovement().y * 0.5D, this.random.nextGaussian() * 0.05D);
        }

        if (!this.level.isClientSide && this.life > this.lifetime) {
            this.explode();
        }

    }


}
