package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.goal.FlyEntityLookAtPlayer;
import com.taco.bafflebeasts.entity.goal.IdleAnimationGoal;
import com.taco.bafflebeasts.sound.SoundRegistry;
import com.taco.bafflebeasts.util.ElytraGlideCalculation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.keyframe.event.SoundKeyframeEvent;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class DozeDrakeEntity extends RideableFlightEntity implements GeoEntity, PlayerRideable, FlyingAnimal, NeutralMob, PlayerRideableJumping {


    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(15, 25);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    protected static final RawAnimation DOZEDRAKE_NEUTRAL = RawAnimation.begin().thenLoop("animation.doze_drake.neutral");
    protected static final RawAnimation DOZEDRAKE_WALK = RawAnimation.begin().thenLoop("animation.doze_drake.walk");
    protected static final RawAnimation DOZEDRAKE_RUN = RawAnimation.begin().thenLoop("animation.doze_drake.sprint");
    protected static final RawAnimation DOZEDRAKE_FLY = RawAnimation.begin().thenLoop("animation.doze_drake.fly");
    protected static final RawAnimation DOZEDRAKE_GLIDE = RawAnimation.begin().thenLoop("animation.doze_drake.glide");
    protected static final RawAnimation DOZEDRAKE_FLY_DASH = RawAnimation.begin().thenPlay("animation.doze_drake.fly_dash");
    protected static final RawAnimation DOZEDRAKE_SIT = RawAnimation.begin().thenPlayAndHold("animation.doze_drake.sitdown");
    protected static final RawAnimation DOZEDRAKE_SLEEP = RawAnimation.begin().thenPlay("animation.doze_drake.gotosleep").thenPlay("animation.doze_drake.sleep");
    protected static final RawAnimation DOZEDRAKE_BLINK = RawAnimation.begin().thenLoop("animation.doze_drake.blink");
    protected static final RawAnimation DOZEDRAKE_ATTACK = RawAnimation.begin().thenPlay("animation.doze_drake.attack");
    protected static final RawAnimation DOZEDRAKE_IDLE1 = RawAnimation.begin().thenPlay("animation.doze_drake.idle1");
    protected static final RawAnimation DOZEDRAKE_IDLE2 = RawAnimation.begin().thenPlay("animation.doze_drake.idle2");
    protected static final RawAnimation DOZEDRAKE_IDLE3 = RawAnimation.begin().thenPlay("animation.doze_drake.idle3");
    protected static final RawAnimation DOZEDRAKE_IDLE4 = RawAnimation.begin().thenPlay("animation.doze_drake.idle4");
    protected static final RawAnimation DOZEDRAKE_IDLE5 = RawAnimation.begin().thenPlay("animation.doze_drake.idle5");

    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(DozeDrakeEntity.class, EntityDataSerializers.BOOLEAN);

    public int animationbuffer = 5;

    public DozeDrakeEntity(EntityType<? extends RideableFlightEntity> pEntityType, Level pLevel) {
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
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.2D, true));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 2.2D, 32.0F));
        this.goalSelector.addGoal(4, new FloatGoal(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(6, new IdleAnimationGoal(this, 5));
        this.goalSelector.addGoal(7, new FlyEntityLookAtPlayer(this, Player.class, 12F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.00));
        this.targetSelector.addGoal(1,new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(pOtherAnimal instanceof DozeDrakeEntity)) {
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
        //If the amaro is moving
        if (event.isMoving() && this.onGround() && !this.hasControllingPassenger()) {
            event.getController().setAnimation(DOZEDRAKE_WALK);
            return PlayState.CONTINUE;
            // If the Amaro is moving with a rider
        } else if (event.isMoving() && this.onGround() && this.hasControllingPassenger()) {
            event.getController().setAnimation(DOZEDRAKE_RUN);
            return PlayState.CONTINUE;
            // Fly Animation
        } else if (!this.onGround() && !this.isElytraFlying()) { // Set the amaro to fly
            event.getController().setAnimation(DOZEDRAKE_FLY);
            return PlayState.CONTINUE;
            // Fly Dash Animation
        } else if (!this.onGround() && this.isElytraFlying() && ElytraGlideCalculation.isFlightBoosting(this)) {
            event.getController().stop();
            event.getController().setAnimation(DOZEDRAKE_FLY_DASH);
            return PlayState.CONTINUE;
            // Neutral Fly Animation
        } else if (!this.onGround() && this.isElytraFlying()) {
            event.getController().setAnimation(DOZEDRAKE_GLIDE);
            return PlayState.CONTINUE;
        }
        // If the amaro is not moving
        else {
            //
            if (this.isInSittingPose() && !this.isAsleep()) {
                event.getController().setAnimation(DOZEDRAKE_SIT);
                return PlayState.CONTINUE;
            }
            if (this.isInSittingPose() && this.isAsleep()) {
                event.getController().stop();
                event.getController().setAnimation(DOZEDRAKE_SLEEP);
                return PlayState.CONTINUE;
            }

            if (this.onGround() && !this.isInSittingPose()) {
                event.getController().setAnimation(DOZEDRAKE_NEUTRAL);
                return PlayState.CONTINUE;
            }
        }
        return PlayState.CONTINUE;

    }

    private <E extends GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
        int idlePose = this.getIdlePose();

        // Idle Animations
        if (!event.isMoving() && this.onGround() && !this.isInSittingPose() && !this.isAsleep()) {
            switch (idlePose) {
                case 1: event.getController().setAnimation(DOZEDRAKE_IDLE1);
                    return PlayState.CONTINUE;
                case 2: event.getController().setAnimation(DOZEDRAKE_IDLE2);
                    return PlayState.CONTINUE;
                case 3: event.getController().setAnimation(DOZEDRAKE_IDLE3);
                    return PlayState.CONTINUE;
                case 4: event.getController().setAnimation(DOZEDRAKE_IDLE4);
                    return PlayState.CONTINUE;
                case 5: event.getController().setAnimation(DOZEDRAKE_IDLE5);
                    return PlayState.CONTINUE;
            }
        }

        if (!event.isMoving() && this.getEntityWakeUpState() && this.isInSittingPose()) {

        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {
        if (this.swinging == true) {
            event.getController().setAnimation(DOZEDRAKE_ATTACK);
            return PlayState.CONTINUE;
        }

        //event.getController().forceAnimationReset();
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState blinkPredicate(AnimationState<E> event) {
        if (!this.isAsleep()) {
            event.getController().setAnimation(DOZEDRAKE_BLINK);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private <E extends GeoAnimatable> void soundListener(SoundKeyframeEvent<E> event) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 3.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<DozeDrakeEntity> movementController = new AnimationController(this, "movement", 15, this::movementPredicate);
        movementController.setSoundKeyframeHandler(this::soundListener);

        controllers.add(movementController);
        controllers.add(new AnimationController(this, "idle", 15, this::idlePredicate));
        controllers.add(new AnimationController(this, "blink", 0, this::blinkPredicate));
        controllers.add(new AnimationController(this, "attack", 0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    @Override
    public void setIdlePose(int pose) {
        super.setIdlePose(pose);
        if (pose <= 3) {
            this.setGoToSleepState(true);
        } else {
            this.setEntityWakeUpState(true);
        }
    }

    protected SoundEvent getAmbientSound() {

        if (this.isAsleep()) {
            if (this.random.nextInt(100) == 0) {
                return SoundRegistry.DOZEDRAKE_HONK_MIMI.get();
            } else {
                return SoundRegistry.DOZEDRAKE_SNOOZE.get();
            }
        }

        return SoundRegistry.DOZEDRAKE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundRegistry.DOZEDRAKE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.DOZEDRAKE_DEATH.get();
    }

    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public void tick() {
        super.tick();
        if (getIdleTimer() > 0) {
            setIdleTimer(getIdleTimer() - 1);
        }

        if (this.getGoToSleepState()) {
            this.animationbuffer -= 1;
            if (this.animationbuffer < 0) {
                this.setSleep(true);
                this.setGoToSleepState(false);
                this.animationbuffer = 5;
            }
        }
        if (this.getEntityWakeUpState()) {
            this.animationbuffer -= 1;
            if (this.animationbuffer < 0) {
                this.setEntityWakeUpState(false);
                this.setSleep(false);
                this.animationbuffer = 5;
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.isTame()) {
            //  Attempt to Saddle the Dragon
            if (isSaddleable() && !this.isBaby() && itemStack.is(Items.SADDLE)) {
                itemStack.shrink(1);
                this.setSaddle(true);
                equipSaddle(getSoundSource());
                return InteractionResult.sidedSuccess(level().isClientSide());

           // Ride Check
            } else if (isSaddled() && !this.isBaby() && !player.isShiftKeyDown()) {
                if (!level().isClientSide) {
                    this.setRidingPlayer(player);
                    this.setOrderedToSit(false);
                    this.setEntityWakeUpState(true);
                    navigation.stop();
                    this.setTarget(null);
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }

            if (itemStack.is(Items.MELON_SLICE)) {
                this.setIdlePose(2);
                return InteractionResult.sidedSuccess(level().isClientSide());
            }

            InteractionResult emptyhand = super.mobInteract(player, hand);
            if (!emptyhand.consumesAction() && player.isShiftKeyDown()) {
                this.setOrderedToSit(!this.isOrderedToSit()); // toggle the opposite of sit
                this.navigation.stop();
                this.flying = false;
                if (this.isInSittingPose()) {
                    player.displayClientMessage(Component.literal(this.getName().getString() + " is now sitting"), false);
                } else {
                    player.displayClientMessage(Component.literal(this.getName().getString() + " is now sitting"), false);
                }
                return InteractionResult.SUCCESS;
            }

        } else {
            // Tame Attempt Check
            if (itemStack.is(Items.BEEF) && !this.isBaby()) {
                itemStack.shrink(1);
                // Have a 1-3 chance of taming the amaro
                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.navigation.stop();
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }

        }

        return super.mobInteract(player, hand);
    }

    private void setRidingPlayer(Player player) {
        player.setYRot(getYRot());
        player.setXRot(getXRot());
        player.startRiding(this);
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
    public LivingEntity getControllingPassenger() {
        List<Entity> list = getPassengers();
        if (list.isEmpty()) {
            return null;
        } else {
            return (LivingEntity)list.get(0);
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
        this.updateSwingTime();
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
        this.entityData.set(HAS_SADDLE, b);
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
        return this.entityData.get(HAS_SADDLE);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime) {
        this.remainingPersistentAngerTime = pRemainingPersistentAngerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pPersistentAngerTarget) {
        persistentAngerTarget = pPersistentAngerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public boolean canAttackType(EntityType<?> pType) {
        return super.canAttackType(pType);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public boolean doHurtTarget(Entity pEntity) {
        float f = this.getAttackDamage();
        boolean flag = super.doHurtTarget(pEntity);

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

}


