package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.goal.FlyEntityFollowOwnerGoal;
import com.taco.bafflebeasts.entity.goal.FlyEntityLookAtPlayer;
import com.taco.bafflebeasts.entity.goal.IdleAnimationGoal;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
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

public class AmaroEntity extends RideableFlightEntity implements GeoEntity, PlayerRideable, PlayerRideableJumping {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> AMARO_VARIANT = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.MELON_SLICE, Items.GLISTERING_MELON_SLICE);

    protected static final RawAnimation AMARO_SPRINT = RawAnimation.begin().thenLoop("animation.amaro.sprint");
    protected static final RawAnimation AMARO_FLY = RawAnimation.begin().thenLoop("animation.amaro.fly");
    protected static final RawAnimation AMARO_RUN = RawAnimation.begin().thenLoop("animation.amaro.run");
    protected static final RawAnimation AMARO_SIT = RawAnimation.begin().thenLoop("animation.amaro.sit");
    protected static final RawAnimation AMARO_NEUTRAL = RawAnimation.begin().thenLoop("animation.amaro.neutral");
    protected static final RawAnimation AMARO_IDLE1 = RawAnimation.begin().thenPlay("animation.amaro.idle1");
    protected static final RawAnimation AMARO_IDLE2 = RawAnimation.begin().thenPlay("animation.amaro.idle2");
    protected static final RawAnimation AMARO_IDLE3 = RawAnimation.begin().thenPlay("animation.amaro.idle3");
    protected static final RawAnimation AMARO_IDLE4 = RawAnimation.begin().thenPlay("animation.amaro.idle4");
    protected static final RawAnimation AMARO_IDLE5 = RawAnimation.begin().thenPlay("animation.amaro.idle5");
    protected static final RawAnimation AMARO_SLEEP = RawAnimation.begin().thenPlay("animation.amaro.gotosleep").thenLoop("animation.amaro.sleep");
    protected static final RawAnimation AMARO_WAKEUP = RawAnimation.begin().thenPlay("animation.amaro.wakeup");
    protected static final RawAnimation AMARO_BLINK = RawAnimation.begin().thenLoop("animation.amaro.blink");
    protected static final RawAnimation AMARO_GLIDE = RawAnimation.begin().thenLoop("animation.amaro.glide");
    protected static final RawAnimation AMARO_FLIGHT_DASH = RawAnimation.begin().thenPlay("animation.amaro.fly_dash");

    public int animationbuffer = 5;

    //Amaro Constructor
    public AmaroEntity(EntityType<? extends RideableFlightEntity> entityType, Level level) {
        super(entityType, level, 6, 100);
        this.setTame(false);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SADDLE, false);
        this.entityData.define(AMARO_VARIANT, 1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("AmaroHasSaddle", this.isSaddled());
        tag.putInt("AmaroVariant", this.getVariant());
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddle(tag.getBoolean("AmaroHasSaddle"));
        this.setAmaroVariant(tag.getInt("AmaroVariant"));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        this.setAmaroVariant((int)(Math.random() * 3) + 1);
        if (pSpawnData == null) {
            pSpawnData = new AgeableMob.AgeableMobGroupData(false);
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(5, new FlyEntityFollowOwnerGoal(this, 2.0D, 10.F, 2.0F, true));
        this.goalSelector.addGoal(6, new PanicGoal(this, 1.250));
        this.goalSelector.addGoal(7, new FlyEntityLookAtPlayer(this, Player.class, 12F));
        this.goalSelector.addGoal(8, new IdleAnimationGoal(this, 5));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 1.00));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(11, (new HurtByTargetGoal(this)).setAlertOthers());
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
            AmaroEntity otherAmaro = (AmaroEntity)pOtherAnimal;
            if (!otherAmaro.isTame()) {
                return false;
            } else if (otherAmaro.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && otherAmaro.isInLove();
            }
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        Item item = pStack.getItem();
        return (item == Items.MELON_SLICE);
    }

    @Nullable
    @Override
    public AmaroEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        AmaroEntity baby = ModEntityTypes.Amaro.get().create(pLevel);

        return baby;
    }

    // this plays the walking, neutral, flying, sit, and sleep animations
    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        //If the amaro is moving
        if (event.isMoving() && this.onGround() && !this.hasControllingPassenger()) {
            event.getController().setAnimation(AMARO_RUN);
            return PlayState.CONTINUE;
        // If the Amaro is moving with a rider
        } else if (this.isMoving && this.onGround() && this.hasControllingPassenger()) {
            event.getController().setAnimation(AMARO_SPRINT);
            return PlayState.CONTINUE;
        // Fly Animation
        } else if (!this.onGround() && !this.isElytraFlying()) { // Set the amaro to fly
            event.getController().setAnimation(AMARO_FLY);
            return PlayState.CONTINUE;
        // Fly Dash Animation
        } else if (!this.onGround() && this.isElytraFlying() && ElytraGlideCalculation.isFlightBoosting(this)) {
            event.getController().stop();
            event.getController().setAnimation(AMARO_FLIGHT_DASH);
            return PlayState.CONTINUE;
        // Neutral Fly Animation
        } else if (!this.onGround() && this.isElytraFlying()) {
            event.getController().setAnimation(AMARO_GLIDE);
            return PlayState.CONTINUE;
        }
        // If the amaro is not moving
        else {
            //
            if (this.isInSittingPose() && !this.isAsleep()) {
                event.getController().setAnimation(AMARO_SIT);
                return PlayState.CONTINUE;
            }
            if (this.isInSittingPose() && this.isAsleep()) {
                event.getController().stop();
                event.getController().setAnimation(AMARO_SLEEP);
                return PlayState.CONTINUE;
            }

            if (this.onGround() && !this.isInSittingPose()) {
                event.getController().setAnimation(AMARO_NEUTRAL);
                return PlayState.CONTINUE;
            }
        }
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
        int idlePose = this.getIdlePose();

        // Idle Animations
        if (!event.isMoving() && this.onGround() && !this.isInSittingPose()) {
            switch (idlePose) {
                case 1: event.getController().setAnimation(AMARO_IDLE1);
                    return PlayState.CONTINUE;
                case 2: event.getController().setAnimation(AMARO_IDLE2);
                    return PlayState.CONTINUE;
                case 3: event.getController().setAnimation(AMARO_IDLE3);
                    return PlayState.CONTINUE;
                case 4: event.getController().setAnimation(AMARO_IDLE4);
                    return PlayState.CONTINUE;
                case 5: event.getController().setAnimation(AMARO_IDLE5);
                    return PlayState.CONTINUE;
            }
        }

        if (!event.isMoving() && this.getEntityWakeUpState() && this.isInSittingPose()) {
            event.getController().forceAnimationReset();
            event.getController().setAnimation(AMARO_WAKEUP);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState blinkPredicate(AnimationState<E> event) {
        if (!this.isAsleep()) {
            event.getController().setAnimation(AMARO_BLINK);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <ENTITY extends GeoAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 3.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controller) {
        AnimationController<AmaroEntity> walkController = new AnimationController(this, "walk", 0, this::predicate);
        walkController.setSoundKeyframeHandler(this::soundListener);
        controller.add(walkController);


        controller.add(new AnimationController(this, "idle",
                15, this::idlePredicate));
        controller.add(new AnimationController(this, "blink",
                0, this::blinkPredicate));

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SHEEP_STEP, 1.00F, 0.5f);
    }

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

    public int getVariant() {
        return this.entityData.get(AMARO_VARIANT);
    }

    public void setAmaroVariant(int variant) {
        this.entityData.set(AMARO_VARIANT, variant);
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

    @Override
    public void tick() {
        super.tick();
        // Idle timer
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
        ItemStack stack = player.getItemInHand(hand);
        if (this.level().isClientSide) {
            if (this.isTame() ) {
                return InteractionResult.SUCCESS;
            }

        } else {
            // tamed interactions
            if (this.isTame()) {
                // Breeding Interactions
                int age = this.getAge();
                if (stack.is(Items.MELON_SLICE)) {
                    if (!this.level().isClientSide && age == 0 && this.canFallInLove()) {
                        this.usePlayerItem(player, hand, stack);
                        this.setInLove(player);
                        return InteractionResult.SUCCESS;
                    }
                    // Baby Growth Interaction
                    if (this.isBaby()) {
                        this.usePlayerItem(player, hand, stack);
                        this.ageUp(getSpeedUpSecondsWhenFeeding(-age), true);
                        return InteractionResult.sidedSuccess(this.level().isClientSide);
                    }

                    if (this.level().isClientSide) {
                        return InteractionResult.CONSUME;
                    }
                }

                // Equip Saddle
                boolean saddlecheck = !this.isSaddled() && this.isSaddleable() && stack.is(Items.SADDLE) && !this.isBaby();
                if (saddlecheck) {
                    stack.shrink(1); // remove saddle from players inventory.
                    equipSaddle(getSoundSource());
                    setSaddle(true);
                    return InteractionResult.sidedSuccess(level().isClientSide());
                }

                // Check to see if you can ride the Amaro
                if (isSaddled() && this.isTame() && !player.isShiftKeyDown() && !isHealItem(stack.getItem())) {
                    if (!level().isClientSide) {
                        setRidingPlayer(player);
                        this.setOrderedToSit(false);
                        this.setEntityWakeUpState(true);
                        navigation.stop();
                        setTarget(null);
                    }
                    return InteractionResult.sidedSuccess(level().isClientSide());
                }
                // Heal check
                if (isHealItem(stack.getItem()) &&!player.isShiftKeyDown()) {
                    stack.shrink(1);
                    this.heal(4.0f);
                    this.spawnTamingParticles(true);
                    return InteractionResult.sidedSuccess(level().isClientSide());
                }

                // Sit check
                InteractionResult emptyhand = super.mobInteract(player, hand);
                if (player.isShiftKeyDown() && !emptyhand.consumesAction()) {
                    this.setOrderedToSit(!this.isOrderedToSit()); // toggle the opposite of sit
                    if (this.isOrderedToSit()) {
                        player.displayClientMessage(Component.literal(this.getName().getString() + " is now sitting!"), true);
                    } else {
                        player.displayClientMessage(Component.literal(this.getName().getString() + " is now following!"), true);
                    }
                    this.navigation.stop();
                    this.flying = false;
                    return InteractionResult.sidedSuccess(level().isClientSide());
                }
                // Untamed interactions
                // Tame check
            } else if (stack.is(Items.GLISTERING_MELON_SLICE) && !this.isTame()) {
                stack.shrink(1);
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
        return super.mobInteract(player,hand);
    }

    //Mob Attributes
    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.ARMOR, 2.0d)
                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
    }

    private boolean isHealItem(Item food) {
        if (food == Items.APPLE || food == Items.GLOW_BERRIES ) {
            return true;
        }
        return false;
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
    public LivingEntity getControllingPassenger() {
        List<Entity> list = getPassengers();
        if (list.isEmpty()) {
            return null;
        } else {
            return (LivingEntity)list.get(0);
        }
    }

    @Override
    public boolean isSaddled() {
        return entityData.get(HAS_SADDLE);
    }

    public void setRidingPlayer(Player player) {
        player.setYRot(getYRot());
        player.setXRot(getXRot());
        player.startRiding(this);
    }

    public void setSaddle(boolean b) {
        entityData.set(HAS_SADDLE, b);
    }

    public boolean canBeControlledByRider() {
        return getControllingPassenger() instanceof LivingEntity ;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction pCallback) {
        super.positionRider(passenger, pCallback);

        Entity rider = getControllingPassenger();
        if (rider != null) {
            // Set the position of the rider to the current amaro position
            // Try to put the rider on 1.45 Y height
            double yOffset = this.getBbHeight() - 1.45;
            passenger.setPos(this.getX(), this.getY() + (this.getBbHeight() - yOffset),
                    this.getZ());

            // Face the rider the same direction the amaro is facing.
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

}
