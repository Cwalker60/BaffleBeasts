package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.client.WrymistPowerHud;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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

public class WrymistEntity extends RideableFlightEntity implements GeoEntity, PlayerRideable, PlayerRideableJumping, NeutralMob {

    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;
    private int tailAttackCooldown = 0;
    private boolean tailAttackIconFlicker = false;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    protected static final RawAnimation WRYMIST_NEUTRAL = RawAnimation.begin().thenLoop("animation.wrymist.neutral");
    protected static final RawAnimation WRYMIST_WALK = RawAnimation.begin().thenLoop("animation.wrymist.walk");
    protected static final RawAnimation WRYMIST_SPRINT = RawAnimation.begin().thenLoop("animation.wrymist.sprint");
    protected static final RawAnimation WRYMIST_FLY = RawAnimation.begin().thenLoop("animation.wrymist.fly");
    protected static final RawAnimation WRYMIST_BLINK = RawAnimation.begin().thenLoop("animation.wrymist.blink");
    protected static final RawAnimation WRYMIST_GLIDE = RawAnimation.begin().thenLoop("animation.wrymist.glide");
    protected static final RawAnimation WRYMIST_SIT = RawAnimation.begin().thenPlay("animation.wrymist.sitting").thenLoop("animation.wrymist.sit");
    protected static final RawAnimation WRYMIST_GLIDE_DASH = RawAnimation.begin().thenPlay("animation.wrymist.fly_dash");
    protected static final RawAnimation WRYMIST_ATTACK = RawAnimation.begin().thenPlay("animation.wrymist.attack");
    protected static final RawAnimation WRYMIST_TAIL_ATTACK = RawAnimation.begin().thenPlay("animation.wrymist.tail_attack");
    protected static final RawAnimation WRYMIST_SLEEP = RawAnimation.begin().thenPlay("animation.wrymist.gotosleep").thenLoop("animation.wrymist.sleep");
    protected static final RawAnimation WRYMIST_IDLE1 = RawAnimation.begin().thenPlay("animation.wrymist.idle1");
    protected static final RawAnimation WRYMIST_IDLE2 = RawAnimation.begin().thenPlay("animation.wrymist.idle2");
    protected static final RawAnimation WRYMIST_IDLE3 = RawAnimation.begin().thenPlay("animation.wrymist.idle3");
//    protected static final RawAnimation WRYMIST_IDLE4 = RawAnimation.begin().thenPlay("animation.wrymist.idle4");
    protected static final RawAnimation WRYMIST_IDLE5 = RawAnimation.begin().thenPlay("animation.wrymist.idle5");

    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(WrymistEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TAIL_DYE = SynchedEntityData.defineId(WrymistEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CAN_TAIL_ATTACK = SynchedEntityData.defineId(WrymistEntity.class, EntityDataSerializers.BOOLEAN);

    public WrymistEntity(EntityType<? extends RideableFlightEntity> entityType, Level level) {
        super(entityType, level, 5, 100, 4, 2);
        this.setTame(false);
    }

    @Override
    public void defineSynchedData() {
        this.entityData.define(HAS_SADDLE, false);
        this.entityData.define(TAIL_DYE, 0);
        this.entityData.define(CAN_TAIL_ATTACK, true);
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasSaddle", this.isSaddled());
        tag.putInt("TailDye", this.getTailDye());
        tag.putBoolean("TailAttackReady", this.canTailAttack());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddle(tag.getBoolean("HasSaddle"));
        this.setTailDye(tag.getInt("TailDye"));
        this.setCanTailAttack(tag.getBoolean("TailAttackReady"));
    }

    public void setCanTailAttack(boolean b) {
        this.entityData.set(CAN_TAIL_ATTACK, b);
    }

    public boolean canTailAttack() {
        return this.entityData.get(CAN_TAIL_ATTACK);
    }

    public int getTailDye() {
        return this.entityData.get(TAIL_DYE);
    }

    public void setTailDye(int dyeId) {
        this.entityData.set(TAIL_DYE, dyeId);
    }


    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ATTACK_DAMAGE, 4.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.ARMOR, 4.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.3f).build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 1.2D, 32.0F));
        this.goalSelector.addGoal(4, new FloatGoal(this));
        this.goalSelector.addGoal(5, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(6, new IdleAnimationGoal(this, 4));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.5f));
        this.goalSelector.addGoal(8, new FlyEntityFollowOwnerGoal(this,1.2d,15,4,true));
        this.goalSelector.addGoal(9, new IdleEntityLookAtPlayer(this, Player.class, 6F));
        this.goalSelector.addGoal(10, new IdleEntityRandomLookAtGoal(this));

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
        } else if (!(pOtherAnimal instanceof WrymistEntity)) {
            return false;
        } else {
            WrymistEntity other = (WrymistEntity) pOtherAnimal;
            if (!other.isTame()) {
                return false;
            } else if (other.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && other.isInLove();
            }
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        WrymistEntity offSpring = ModEntityTypes.Wrymist.get().create(pLevel);
        return offSpring;
    }

    private <E extends GeoAnimatable>PlayState movementPredicate(AnimationState<E> event) {
        if (event.isMoving() && this.onGround() && !this.hasControllingPassenger() && !this.isAsleep() && !this.isInSittingPose()) {
            event.getController().setAnimation(WRYMIST_WALK);
            return PlayState.CONTINUE;
        } else if (this.isMoving && this.onGround() && this.hasControllingPassenger()) {
            event.getController().setAnimation(WRYMIST_SPRINT);
            return PlayState.CONTINUE;
        }  else if (!this.onGround() && !this.isElytraFlying()) { // Fly Animation
            event.getController().setAnimation(WRYMIST_FLY);
            return PlayState.CONTINUE;
            // Fly Dash Animation
        } else if (!this.onGround() && this.isElytraFlying() && ElytraGlideCalculation.isFlightBoosting(this)) {
            event.getController().stop();
            event.getController().setAnimation(WRYMIST_GLIDE_DASH);
            return PlayState.CONTINUE;
            // Neutral Fly Animation
        } else if (!this.onGround() && this.isElytraFlying()) {
            event.getController().setAnimation(WRYMIST_GLIDE);
            return PlayState.CONTINUE;
        } else {
            if (this.isInSittingPose() && !this.isAsleep() && !this.getEntityWakeUpState()) {
                event.getController().setAnimation(WRYMIST_SIT);
                return PlayState.CONTINUE;
            }

            if (this.isAsleep() && (this.isInSittingPose() || !this.isTame())) {
                event.getController().stop();
                event.getController().setAnimation(WRYMIST_SLEEP);
                return PlayState.CONTINUE;
            }

            if (this.onGround() && !this.isInSittingPose()) {
                event.getController().setAnimation(WRYMIST_NEUTRAL);
                return PlayState.CONTINUE;
            }
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable>PlayState idlePredicate(AnimationState<E> event) {
        int idlePose = this.getIdlePose();

        // Idle Animations
        if (!event.isMoving() && this.onGround() && !this.isInSittingPose() && !this.isAsleep()) {
            switch (idlePose) {
                case 1: event.getController().setAnimation(WRYMIST_IDLE1);
                    return PlayState.CONTINUE;
                case 2: event.getController().setAnimation(WRYMIST_IDLE2);
                    return PlayState.CONTINUE;
                case 3: event.getController().setAnimation(WRYMIST_IDLE3);
                    return PlayState.CONTINUE;
//                case 4: event.getController().setAnimation(WRYMIST_IDLE4);
//                    return PlayState.CONTINUE;
                case 4: event.getController().setAnimation(WRYMIST_IDLE5);
                    return PlayState.CONTINUE;
            }
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable>PlayState attackPredicate(AnimationState<E> event) {
        if (this.swinging == true) {
            event.getController().setAnimation(WRYMIST_ATTACK);
            return PlayState.CONTINUE;
        }

//        event.getController().forceAnimationReset();
        return PlayState.STOP;
    }

    private <E extends GeoAnimatable>PlayState blinkPredicate(AnimationState<E> event) {
        if (!this.isAsleep()) {
            event.getController().setAnimation(WRYMIST_BLINK);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }


    private <E extends GeoAnimatable> void soundListener(SoundKeyframeEvent<E> event) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 3.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<WrymistEntity> movementController = new AnimationController<>(this, "movement", 0, this::movementPredicate);
        movementController.setSoundKeyframeHandler(this::soundListener);

        controllers.add(movementController);
        controllers.add(new AnimationController(this, "idle", 15, this::idlePredicate));
        controllers.add(new AnimationController(this, "blink", 0, this::blinkPredicate));
        controllers.add(new AnimationController(this, "attack", 0, this::attackPredicate)
        .triggerableAnim("TailAttack", WRYMIST_TAIL_ATTACK));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    @Override
    public void setIdlePose(int pose) {
        super.setIdlePose(pose);
        if (this.isTame()) {
            if (pose <= 2) {
                this.setSleep(true);
            } else {
                this.setEntityWakeUpState(true);
            }
        }

    }

    public int getTailAttackCooldown() {
        return this.tailAttackCooldown;
    }

    protected SoundEvent getAmbientSound() {
        return SoundRegistry.WRYMIST_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundRegistry.WRYMIST_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.WRYMIST_DEATH.get();
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction pCallback) {
        super.positionRider(passenger, pCallback);

        Entity rider = this.getControllingPassenger();
        double yOffset = this.getBbHeight() - 1.0;

        if (rider != null) {
            double xPass = this.getX();
            double yPass = this.getY() + (this.getBbHeight() - yOffset);
            double zPass = this.getZ();

            passenger.setPos(xPass, yPass, zPass);

            if (passenger instanceof LivingEntity) {
                passenger.setYBodyRot(this.yBodyRot);
            }

        }
    }

    public int getMaxPassengers() {
        return 1;
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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (this.isTame()) {
            // Breed Check
            if (itemStack.is(Items.COOKED_CHICKEN) && this.age == 0 && this.canFallInLove()) {
                this.setInLove(player);
                this.usePlayerItem(player, hand, itemStack);
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
            // Saddle Check
            if (isSaddleable() && !this.isBaby() && itemStack.is(Items.SADDLE)) {
                itemStack.shrink(1);
                this.setSaddle(true);
                equipSaddle(getSoundSource());
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
            // Dye Check
            if (itemStack.getItem() instanceof DyeItem) {
                // Get the color from the DyeItem.
                DyeItem dyeItem = (DyeItem) itemStack.getItem();
                DyeColor color = dyeItem.getDyeColor();
                // Set the tail dye to the color.
                this.setTailDye(color.getId());
                this.level().playSound(this,this.getOnPos(),SoundEvents.DYE_USE,SoundSource.AMBIENT,1.0f,1.0f);

                return InteractionResult.SUCCESS;
            }
            // Ride Check
            if (isSaddled() && !this.isBaby() && !player.isShiftKeyDown() && !isHealItem(itemStack.getItem())) {
                if (!level().isClientSide) {
                    this.setRidingPlayer(player);
                    this.setOrderedToSit(false);
                    this.setEntityWakeUpState(true);
                    navigation.stop();
                    this.setTarget(null);
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
            // Sit check
            InteractionResult emptyhand = super.mobInteract(player, hand);
            if (!emptyhand.consumesAction() && player.isShiftKeyDown() && !this.hasControllingPassenger()) {
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
            // If not tamed, do the following
            // Tame Attempt Check
            if (itemStack.is(Items.CHICKEN)) {
                itemStack.shrink(1);
                // Have a 1-3 chance of taming the amaro
                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setEntityWakeUpState(true); // Reset Sleep State
                    this.level().broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte)6);
                }
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
        }

        // Heal Check
        if (this.isHealItem(itemStack.getItem())) {
            itemStack.shrink(1);
            this.spawnTamingParticles(true);
            this.heal(4.0f);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return super.mobInteract(player,hand);
    }

    private boolean isHealItem(Item food) {
        if (food == Items.PORKCHOP || food == Items.BEEF || food == Items.MUTTON) {
            return true;
        }
        return false;
    }

    @Override
    public void travel(Vec3 vec3) {
        super.travel(vec3);
    }

    @Override
    protected float getRiddenSpeed(Player pPlayer) {
        if (this.isControlledByLocalInstance()) {
            return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.9f;
        }
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.canTailAttack()) {
            this.tailAttackCooldown++;
            if (this.tailAttackCooldown > 100) {
                this.setCanTailAttack(true);
                this.tailAttackCooldown = 0;
                this.tailAttackIconFlicker = true;
            }
        }

        if (this.level().isClientSide) {
            if (tailAttackIconFlicker == true) {
                WrymistPowerHud.updateWrymistGUI();
                if (WrymistPowerHud.getWrymistAnimationDrawstate() > 9) {
                    this.tailAttackIconFlicker = false;
                    WrymistPowerHud.BRUSH_ANIMATION_DRAWSTATE = 0;
                }
            }
        }
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
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SHEEP_STEP, 1.00F, 0.5f);
    }

    @Override
    public boolean isSaddleable() {
        return (this.isAlive() && this.isTame());
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

    private void setSaddle(boolean b) {
        this.entityData.set(HAS_SADDLE, b);
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
        this.persistentAngerTarget = pPersistentAngerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {

    }

}
