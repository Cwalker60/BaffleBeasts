package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.client.BubblePowerHud;
import com.taco.bafflebeasts.entity.client.FlightPowerHud;
import com.taco.bafflebeasts.entity.goal.*;
import com.taco.bafflebeasts.sound.SoundRegistry;
import com.taco.bafflebeasts.util.ElytraGlideCalculation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

    private int sleepTickCooldown = 0;
    private int bubbleBlastCooldown = 0;

    private int remainingPersistentAngerTime;
    private boolean bubbleBlastGUIFlicker;

    private UUID persistentAngerTarget;

    protected static final RawAnimation DOZEDRAKE_NEUTRAL = RawAnimation.begin().thenLoop("animation.doze_drake.neutral");
    protected static final RawAnimation DOZEDRAKE_WALK = RawAnimation.begin().thenLoop("animation.doze_drake.walk");
    protected static final RawAnimation DOZEDRAKE_RUN = RawAnimation.begin().thenLoop("animation.doze_drake.sprint");
    protected static final RawAnimation DOZEDRAKE_FLY = RawAnimation.begin().thenLoop("animation.doze_drake.fly");
    protected static final RawAnimation DOZEDRAKE_GLIDE = RawAnimation.begin().thenLoop("animation.doze_drake.glide");
    protected static final RawAnimation DOZEDRAKE_FLY_DASH = RawAnimation.begin().thenPlay("animation.doze_drake.fly_dash");
    protected static final RawAnimation DOZEDRAKE_SIT = RawAnimation.begin().thenPlayAndHold("animation.doze_drake.sitdown");
    protected static final RawAnimation DOZEDRAKE_SLEEP = RawAnimation.begin().thenPlay("animation.doze_drake.gotosleep").thenPlay("animation.doze_drake.sleep");
    protected static final RawAnimation DOZEDRAKE_WAKEUP = RawAnimation.begin().thenPlay("animation.doze_drake.wakeup");
    protected static final RawAnimation DOZEDRAKE_BUBBLE_BLAST = RawAnimation.begin().thenPlay("animation.doze_drake.bubble_charge");
    protected static final RawAnimation DOZEDRAKE_MOUNT_ATTACK = RawAnimation.begin().thenPlay("animation.doze_drake.mount_attack");
    protected static final RawAnimation DOZEDRAKE_BLINK = RawAnimation.begin().thenLoop("animation.doze_drake.blink");
    protected static final RawAnimation DOZEDRAKE_ATTACK = RawAnimation.begin().thenPlay("animation.doze_drake.attack");
    protected static final RawAnimation DOZEDRAKE_IDLE1 = RawAnimation.begin().thenPlay("animation.doze_drake.idle1");
    protected static final RawAnimation DOZEDRAKE_IDLE2 = RawAnimation.begin().thenPlay("animation.doze_drake.idle2");
    protected static final RawAnimation DOZEDRAKE_IDLE3 = RawAnimation.begin().thenPlay("animation.doze_drake.idle3");
    protected static final RawAnimation DOZEDRAKE_IDLE4 = RawAnimation.begin().thenPlay("animation.doze_drake.idle4");
    protected static final RawAnimation DOZEDRAKE_IDLE5 = RawAnimation.begin().thenPlay("animation.doze_drake.idle5");

    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(DozeDrakeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CAN_AI_SLEEP = SynchedEntityData.defineId(DozeDrakeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BUBBLE_CHARGE = SynchedEntityData.defineId(DozeDrakeEntity.class, EntityDataSerializers.BOOLEAN);

    public int animationbuffer = 5;

    public DozeDrakeEntity(EntityType<? extends RideableFlightEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, 6, 150);
        sleepTickCooldown = 0;
        this.setEntityWakeUpState(true);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ATTACK_DAMAGE, 5.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.FOLLOW_RANGE, 48.0f).build();
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SADDLE, false);
        this.entityData.define(CAN_AI_SLEEP, true);
        this.entityData.define(BUBBLE_CHARGE, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("DozeDrakeHasSaddle", this.isSaddled());
        tag.putBoolean("BubbleCharge", this.isBubbleBlasting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddle(tag.getBoolean("DozeDrakeHasSaddle"));
        this.setBubbleBlast(tag.getBoolean("BubbleCharge"));
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.2D, true));
        this.goalSelector.addGoal(3, new DozeDrakeBubbleAttackGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsTargetGoal(this, 2.2D, 32.0F));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(7, new IdleAnimationGoal(this, 5));
        this.goalSelector.addGoal(8, new FlyEntityFollowOwnerGoal(this,2.2d,15,4,true));
        this.goalSelector.addGoal(9, new FlyEntityLookAtPlayer(this, Player.class, 6F));
        this.goalSelector.addGoal(10, new FlyEntityRandomLookAtGoal(this));
        this.goalSelector.addGoal(11, new DozeDrakeRandomStrollGoal(this, 1.00));
        this.goalSelector.addGoal(12, new DozeDrakeSleepGoal(this));

        this.targetSelector.addGoal(1,new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new OwnerHurtTargetGoal(this));
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

        // If the amaro is moving
        if (event.isMoving() && this.onGround() && !this.hasControllingPassenger() && !this.isAsleep() && !this.isOrderedToSit()) {
            event.getController().setAnimation(DOZEDRAKE_WALK);
            return PlayState.CONTINUE;
        // If the Amaro is moving with a rider
        } else if (this.isMoving && this.onGround() && this.hasControllingPassenger()) {
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

            if (this.getEntityWakeUpState()) {
                event.getController().setAnimation(DOZEDRAKE_WAKEUP);
                return PlayState.CONTINUE;
            }
            //
            if (this.isInSittingPose() && !this.isAsleep() && !this.getEntityWakeUpState()) {
                event.getController().setAnimation(DOZEDRAKE_SIT);
                return PlayState.CONTINUE;
            }

            if (this.isAsleep() && (this.isInSittingPose() || !this.isTame())) {
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


        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {
        if (this.swinging == true) {
            event.getController().setAnimation(DOZEDRAKE_ATTACK);
            return PlayState.CONTINUE;
        }

        //event.getController().forceAnimationReset();
        return PlayState.STOP;
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
        controllers.add(new AnimationController(this, "attack", 0, this::attackPredicate)
                .triggerableAnim("bubble_blast", DOZEDRAKE_BUBBLE_BLAST)
                .triggerableAnim("mount_attack", DOZEDRAKE_MOUNT_ATTACK));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    @Override
    public void setIdlePose(int pose) {
        super.setIdlePose(pose);
        if (this.isTame()) {
            if (pose <= 3) {
                this.setSleep(true);
            } else {
                this.setEntityWakeUpState(true);
            }
        }

    }

    protected SoundEvent getAmbientSound() {

        if (this.isAsleep() && this.isOrderedToSit()) {
            if (this.random.nextInt(100) == 0) {
                return SoundRegistry.DOZEDRAKE_HONK_MIMI.get();
            } else {
                return SoundRegistry.DOZEDRAKE_SNOOZE.get();
            }
        }

        return SoundRegistry.DOZEDRAKE_IDLE.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SHEEP_STEP, 1.00F, 0.5f);
    }

    public boolean isBubbleBlasting() {
        return this.entityData.get(BUBBLE_CHARGE);
    }

    public void setBubbleBlast(boolean b) {
        this.entityData.set(BUBBLE_CHARGE, b);
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

        // if dozedrake is actively targeting someone, wake it up
        if (this.targetSelector.getRunningGoals().anyMatch(target -> (target.getGoal() instanceof HurtByTargetGoal))) {
            this.setEntityWakeUpState(true);
            this.setOrderedToSit(false);
            this.sleepTickCooldown = 0;
        }

        if (this.getEntityWakeUpState()) {
            this.animationbuffer -= 1;
            if (this.animationbuffer < 0) {
                this.setEntityWakeUpState(false);
                this.setSleep(false);
                this.animationbuffer = 5;
            }
        }

        // If a wild Dozedrake is asleep, start ticking the cooldown before it can sleep again.
        if (this.isAsleep() && !this.isTame()) {
            this.sleepTickCooldown++;
            if (sleepTickCooldown > 300) {
                sleepTickCooldown = 0;
                this.setEntityWakeUpState(true);
                this.setOrderedToSit(false);
            }
        }

        // Check the cooldown for the bubble blast move. Once the cooldown timer is reached, set
        // the BubbleBlast ability to be ready.
        if (!this.isBubbleBlasting()) {
            this.bubbleBlastCooldown++;
            if (bubbleBlastCooldown > 100) {
                bubbleBlastCooldown = 0;
                this.setBubbleBlast(true);
                // If true, set the bubbleBlastGUIFlicker to tick to animate the GUI.
                this.bubbleBlastGUIFlicker = true;
            }
        }
        if (this.level().isClientSide) {
            if (bubbleBlastGUIFlicker == true) {
                BubblePowerHud.updateBubbleGUI();
                if (BubblePowerHud.getBubbleAnimationDrawstate() > 9) {
                    this.bubbleBlastGUIFlicker = false;
                    BubblePowerHud.BUBBLE_ANIMATION_DRAWSTATE = -1;
                }
            }
        }

    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // DEBUG LINE
        if (itemStack.is(Items.MELON_SLICE)) {
            this.setIdlePose(2);
            this.setSleep(true);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

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

            // Breed Check
            if (itemStack.is(Items.COOKED_BEEF) && this.age == 0 && this.canFallInLove()) {
                this.setInLove(player);
                this.usePlayerItem(player, hand, itemStack);
                return InteractionResult.sidedSuccess(level().isClientSide());
            }

            // Sit check
            InteractionResult emptyhand = super.mobInteract(player, hand);
            if (!emptyhand.consumesAction() && player.isShiftKeyDown()) {
                if (this.isOrderedToSit()) {
                    this.setEntityWakeUpState(true);
                }
                this.setOrderedToSit(!this.isOrderedToSit()); // toggle the opposite of sit
                if (this.isOrderedToSit()) {
                    player.displayClientMessage(Component.literal(this.getName().getString() + " is now sitting!"), true);
                } else {
                    player.displayClientMessage(Component.literal(this.getName().getString() + " is now following!"), true);
                }
                this.navigation.stop();
                this.flying = false;
                return InteractionResult.SUCCESS;
            }

        } else {
            // Tame Attempt Check
            if (itemStack.is(Items.BEEF)) {
                itemStack.shrink(1);
                // Have a 1-3 chance of taming the amaro
                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setSleep(false); this.sleepTickCooldown = 0; this.setEntityWakeUpState(true); // Reset Sleep State
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }

        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return this.getPassengers().size() < getMaxPassengers();
    }

    private void setRidingPlayer(Player player) {
        if (!this.level().isClientSide()) {
            List<Entity> passengers = this.getPassengers();
            if (passengers.size() < this.getMaxPassengers()) {
                player.setYRot(getYRot());
                player.setXRot(getXRot());
                player.startRiding(this);
            }
        }
    }

    public int getMaxPassengers() {
        return 4;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction pCallback) {
        super.positionRider(passenger, pCallback);

        Entity rider = this.getControllingPassenger();
        int index = this.getPassengers().indexOf(passenger) + 1;
        // Try to position the rider at 2.0
        double yOffset = this.getBbHeight() - 2.0;
        // Controlling Rider Position
        if (rider != null) {
            // 1 slot of the mount
            double xPass = this.getX();
            double yPass = this.getY() + (this.getBbHeight() - yOffset);
            double zPass = this.getZ();

            switch (index) {
            // Top left passenger
                case 1 :
                    xPass = xPass + 0.8 * Math.cos(Math.toRadians(this.getYRot() + 50));
                    zPass = zPass + 0.8 * Math.sin(Math.toRadians(this.getYRot() + 50)); break;
            // Top right passenger
                case 2 :
                    xPass = xPass - 0.8 * Math.cos(Math.toRadians(this.getYRot() - 50));
                    zPass = zPass - 0.8 * Math.sin(Math.toRadians(this.getYRot() - 50)); break;
            // Bottom left passenger
                case 3 :
                    xPass = xPass + 0.8 * Math.cos(Math.toRadians(this.getYRot() - 50));
                    zPass = zPass + 0.8 * Math.sin(Math.toRadians(this.getYRot() - 50)); break;
            // Bottom right passenger
                case 4 :
                    xPass = xPass - 0.8 * Math.cos(Math.toRadians(this.getYRot() + 50));
                    zPass = zPass - 0.8 * Math.sin(Math.toRadians(this.getYRot() + 50)); break;
            }

            passenger.setPos(xPass, yPass, zPass);


//            if (rider instanceof LivingEntity) {
//                ((LivingEntity) rider).yBodyRot = this.yBodyRot;
//            }
            if (passenger instanceof LivingEntity) {
                passenger.setYBodyRot(this.yBodyRot);;
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
        BaffleBeasts.MAIN_LOGGER.debug("dozedrake wants to attack!");
    }

    public boolean canAttackType(EntityType<?> pType) {
        return super.canAttackType(pType);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    public int getBubbleBlastCooldown () {
        return this.bubbleBlastCooldown;
    }

//    public boolean doHurtTarget(Entity pEntity) {
//        float f = this.getAttackDamage();
//        boolean flag = super.doHurtTarget(pEntity);
//
//        return flag;
//    }

}


