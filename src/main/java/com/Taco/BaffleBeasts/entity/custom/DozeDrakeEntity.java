package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.sound.SoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DozeDrakeEntity extends RideableFlightEntity implements GeoEntity, FlyingAnimal, IForgeShearable {


    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(DozeDrakeEntity.class, EntityDataSerializers.BOOLEAN);

    public DozeDrakeEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, 7, 150);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ATTACK_DAMAGE, 5.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SADDLE, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("DozeDrakeHasSaddle", this.isSaddled());
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddle(tag.getBoolean("DozeDrakeHasSaddle"));
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.00));
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(pOtherAnimal instanceof AmaroEntity)) {
            return false;
        } else {
            DozeDrakeEntity otherDrake = (DozeDrakeEntity) pOtherAnimal;
            if (!otherDrake.isTame()) {
                return false;
            } else if (otherDrake.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && otherDrake.isInLove();
            }
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        DozeDrakeEntity offSpring = ModEntityTypes.DozeDrake.get().create(pLevel);

        return offSpring;
    }

    private <E extends GeoAnimatable> PlayState movementPredicate(AnimationState<E> event) {
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState blinkPredicate(AnimationState<E> event) {
        return PlayState.CONTINUE;
    }

    private <ENTITY extends GeoAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 3.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<DozeDrakeEntity> movementController = new AnimationController<DozeDrakeEntity>(this, "movement", this::movementPredicate);
        movementController.setSoundKeyframeHandler(this::soundListener);
        controllers.add(movementController);

        controllers.add(new AnimationController<DozeDrakeEntity>(this, "idle", this::idlePredicate));
        controllers.add(new AnimationController<DozeDrakeEntity>(this, "blink", this::blinkPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    // TODO change sounds here
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.AMARO_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundRegistry.AMARO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.AMARO_DEATH.get();
    }

    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.SUCCESS;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction pCallback) {
        super.positionRider(passenger, pCallback);

        Entity rider = getControllingPassenger();
        if (rider != null) {

            passenger.setPos(this.getX(), this.getY() + (this.getBbHeight() - 1.75),
                    this.getZ());

            if (rider instanceof LivingEntity) {
                ((LivingEntity) rider).yBodyRot = this.yBodyRot;
            }
        }

    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public void travel(Vec3 vec3) {
        super.travel(vec3);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vec = this.getDeltaMovement();
        if (!this.onGround() && vec.y < 0.0D && !this.isElytraFlying()) {
            this.setDeltaMovement(vec.multiply(1.0D, 0.6D, 1.0D)); // lower the gravity to 0.6
            this.flying = true;
        }
    }

    @Override
    public boolean isSaddleable() {
        return (this.isAlive() && this.isTame());
    }

    private void setSaddle(boolean b) {
        entityData.set(HAS_SADDLE, b);
    }

    @Override
    public void equipSaddle(@Nullable SoundSource pSource) {
        entityData.set(HAS_SADDLE, true);
        if (pSource != null) {
            this.level().playSound((Player)null, this, SoundEvents.HORSE_SADDLE, pSource, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return false;
    }

}


