package com.Taco.CozyCompanions.entity.custom;

import com.Taco.CozyCompanions.entity.goal.AmaroIdleGoal;
import com.Taco.CozyCompanions.entity.goal.AmaroLookAtPlayer;
import com.Taco.CozyCompanions.flight.AmaroFlight;
import com.Taco.CozyCompanions.flight.AmaroFlightProvider;
import com.Taco.CozyCompanions.networking.ModPackets;
import com.Taco.CozyCompanions.networking.packet.AmaroFlightDashC2SPacket;
import com.Taco.CozyCompanions.networking.packet.AmaroFlightPowerC2SPacket;
import com.Taco.CozyCompanions.sound.SoundRegistry;
import com.Taco.CozyCompanions.util.ElytraGlideCalculation;
import com.Taco.CozyCompanions.util.KeyBindings;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class AmaroEntity extends TamableAnimal implements IAnimatable, Saddleable, PlayerRideable, PlayerRideableJumping {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private static final EntityDataAccessor<Integer> AMARO_VARIANT = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> IDLE_POSE = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> IDLE_TIMER = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GOTOSLEEPSTATE = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ASLEEP = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WAKEUPSTATE = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Logger LOGGER = LogUtils.getLogger();

    protected static final AnimationBuilder AMARO_FLY = new AnimationBuilder().addAnimation("animation.amaro.fly", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder AMARO_RUN = new AnimationBuilder().addAnimation("animation.amaro.run", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder AMARO_SIT = new AnimationBuilder().addAnimation("animation.amaro.sit", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder AMARO_NEUTRAL = new AnimationBuilder().addAnimation("animation.amaro.neutral", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder AMARO_IDLE1 = new AnimationBuilder().addAnimation("animation.amaro.idle1", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder AMARO_IDLE2 = new AnimationBuilder().addAnimation("animation.amaro.idle2", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder AMARO_IDLE3 = new AnimationBuilder().addAnimation("animation.amaro.idle3", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder AMARO_IDLE4 = new AnimationBuilder().addAnimation("animation.amaro.idle4", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder AMARO_IDLE5 = new AnimationBuilder().addAnimation("animation.amaro.idle5", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder AMARO_SLEEP = new AnimationBuilder().addAnimation("animation.amaro.sleep", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder AMARO_GOTOSLEEP = new AnimationBuilder().addAnimation("animation.amaro.gotosleep", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder AMARO_WAKEUP = new AnimationBuilder().addAnimation("animation.amaro.wakeup");
    protected static final AnimationBuilder AMARO_BLINK = new AnimationBuilder().addAnimation("animation.amaro.blink", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder AMARO_NONE = new AnimationBuilder().addAnimation("animation.amaro.blink", ILoopType.EDefaultLoopTypes.LOOP);

    public boolean flying = false;
    public boolean isJumping = false;
    public boolean descend = false;
    public boolean elytraFlying = false;

    public int animationbuffer = 5;
    public int flightRechargeBuffer = 100;

    //Amaro Constructor
    public AmaroEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setTame(false);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IDLE_POSE, 1);
        this.entityData.define(IDLE_TIMER, 400);
        this.entityData.define(HAS_SADDLE, false);
        this.entityData.define(ASLEEP, false);
        this.entityData.define(GOTOSLEEPSTATE, false);
        this.entityData.define(WAKEUPSTATE, false);
        this.entityData.define(AMARO_VARIANT, 1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AmaroIdlePose", this.getIdlePose());
        tag.putInt("AmaroIdleTimer", this.getIdleTimer());
        tag.putBoolean("AmaroHasSaddle", this.isSaddled());
        tag.putBoolean("AmaroGoToSleep", this.getGoToSleepState());
        tag.putBoolean("AmaroAsleep", this.isAsleep());
        tag.putBoolean("AmaroWakeUpState", this.getAmaroWakeUpState());
        tag.putInt("AmaroVariant", this.getVariant());
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setIdlePose(tag.getInt("AmaroIdlePose"));
        this.setIdleTimer(tag.getInt("AmaroIdleTimer"));
        this.setSaddle(tag.getBoolean("AmaroHasSaddle"));
        this.setAmaroSleep(tag.getBoolean("AmaroAsleep"));
        this.setGoToSleepState(tag.getBoolean("AmaroGoToSleep"));
        this.setAmaroWakeUpState(tag.getBoolean("AmaroWakeUpState"));
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
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.250));
        this.goalSelector.addGoal(4, new AmaroLookAtPlayer(this, Player.class, 12F));
        this.goalSelector.addGoal(5, new AmaroIdleGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.00));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(8, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    // this plays the walking, neutral, flying, sit, and sleep animations
    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        //If the amaro is moving
        if (event.isMoving() && this.isOnGround()) {
            event.getController().setAnimation(AMARO_RUN);
            return PlayState.CONTINUE;
        } else if (!this.isOnGround() && !this.isElytraFlying()) { // Set the amaro to fly
            event.getController().setAnimation(AMARO_FLY);
            return PlayState.CONTINUE;
        } else if (!this.isOnGround() && this.isElytraFlying()) {
            event.getController().setAnimation(AMARO_NONE);
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
                event.getController().setAnimation(AMARO_SLEEP);
                return PlayState.CONTINUE;
            }

            if (this.isOnGround() && !this.isInSittingPose()) {
                event.getController().setAnimation(AMARO_NEUTRAL);
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        int idlePose = this.getIdlePose();
        // Stop idle animations if the amaro is flying.
//        if (this.isFlying()) {
//            event.getController().markNeedsReload();
//            return PlayState.STOP;
//        }
        // Idle Animations
        if (!event.isMoving() && this.isOnGround() && !this.isInSittingPose()) {
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

        if (!event.isMoving() && this.getAmaroWakeUpState() && this.isInSittingPose()) {
            event.getController().clearAnimationCache();
            event.getController().setAnimation(AMARO_WAKEUP);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState blinkPredicate(AnimationEvent<E> event) {
        if (!this.isAsleep()) {
            event.getController().setAnimation(AMARO_BLINK);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <ENTITY extends IAnimatable> void soundListener(SoundKeyframeEvent<ENTITY> event) {
        this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 3.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
    }

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController walkController = new AnimationController(this, "walk", 15, this::predicate);
        walkController.registerSoundListener(this::soundListener);
        data.addAnimationController(walkController);

        data.addAnimationController(new AnimationController(this, "idle",
                15, this::idlePredicate));
        data.addAnimationController(new AnimationController(this, "blink",
                0, this::blinkPredicate));

    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
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
        return SoundEvents.CAT_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }

    //setIdleAnimation takes in an int, and sets the Amaro's next idle animation to that int.
    //ints will range from 1-5. if an invalid int is out of this range, it will default to 1.
    public void setIdlePose(int idle) {
        this.entityData.set(IDLE_POSE, idle);
        //LOGGER.debug("idle pose is " + idle);
        if (idle <= 3) {
            this.setGoToSleepState(true);
        } else {
            this.setAmaroWakeUpState(true);
        }

//        LOGGER.debug("gotosleep is " + this.getGoToSleepState());
//        LOGGER.debug("sleeping is " + this.isAsleep());
//        LOGGER.debug("wakeUpState is " + this.getAmaroWakeUpState());

    }
    public int getIdlePose() {
        return this.entityData.get(IDLE_POSE);
    }


    public void setIdleTimer(int time) {
        this.entityData.set(IDLE_TIMER, time);
    }

    public int getIdleTimer() {
        return this.entityData.get(IDLE_TIMER);
    }

    public boolean isAsleep() {
        return this.entityData.get(ASLEEP);
    }

    public void setAmaroSleep(boolean b) {
        this.entityData.set(ASLEEP, b);
    }

    public void setGoToSleepState(boolean b) {
        if (this.isAsleep()) {
            this.entityData.set(GOTOSLEEPSTATE, false);
        } else {
            this.entityData.set(GOTOSLEEPSTATE, b);
        }
    }
    public boolean getGoToSleepState() {return this.entityData.get(GOTOSLEEPSTATE); }

    public boolean getAmaroWakeUpState() { return this.entityData.get(WAKEUPSTATE);}
    public void setAmaroWakeUpState(boolean b) {
        if (isAsleep()) {
            this.entityData.set(WAKEUPSTATE, b);
        }
    }

    public int getVariant() {
        return this.entityData.get(AMARO_VARIANT);
    }

    public void setAmaroVariant(int variant) {
        this.entityData.set(AMARO_VARIANT, variant);
    }

    public boolean isFlying() {
        return this.flying;
    }

    public void setDescend(boolean b) {
        this.descend = b;
    }

    protected int getInventorySize() {
        return 2;
    }

    public void setElytraFlying(boolean b) {
        this.elytraFlying = b;
        if (b == true) {
            this.setSharedFlag(7, true);
        } else {
            this.setSharedFlag(7, false);
        }
    }

    public boolean isElytraFlying() {
        return this.elytraFlying;
    }


    @Override
    public void tick() {
        super.tick();
        getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
           if (amaroFlight.getFlightPower() < 6) {
               if (this.isOnGround()) {
                   this.flightRechargeBuffer -= 5;
               } else {
                   this.flightRechargeBuffer--;
               }
           }

           if (flightRechargeBuffer < 0) {
                this.flightRechargeBuffer = 100;
                amaroFlight.addFlightPower(1);
           }
        });

        // Idle timer
        if (getIdleTimer() > 0) {
            setIdleTimer(getIdleTimer() - 1);
        }

        if (this.getGoToSleepState()) {
            this.animationbuffer -= 1;
            if (this.animationbuffer < 0) {
                this.setAmaroSleep(true);
                this.setGoToSleepState(false);
                this.animationbuffer = 5;
            }
        }
        if (this.getAmaroWakeUpState()) {
            this.animationbuffer -= 1;
            if (this.animationbuffer < 0) {
                this.setAmaroWakeUpState(false);
                this.setAmaroSleep(false);
                this.animationbuffer = 5;
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.level.isClientSide) {
           if (this.isTame() ) {
               return InteractionResult.SUCCESS;
           }

        } else {
            // tamed interactions
            if (this.isTame()) {
                // Equip Saddle
                boolean saddlecheck = !this.isSaddled() && this.isSaddleable() && stack.is(Items.SADDLE);
                if (saddlecheck) {
                    stack.shrink(1); // remove saddle from players inventory.
                    equipSaddle(getSoundSource());
                    setSaddle(true);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                // Check to see if you can ride the Amaro
                if (isSaddled() && this.isTame() && !player.isShiftKeyDown() && !isHealItem(stack.getItem())) {
                    if (!level.isClientSide) {
                        setRidingPlayer(player);
                        this.setOrderedToSit(false);
                        this.setAmaroWakeUpState(true);
                        navigation.stop();
                        setTarget(null);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                // Heal check
                if (isHealItem(stack.getItem()) &&!player.isShiftKeyDown()) {
                    stack.shrink(1);
                    this.setIdlePose(4);//DEBUG LINE
                    this.heal(4.0f);
                    this.spawnTamingParticles(true);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                // DEBUG IDLE CHECK
                if (stack.is(Items.SUGAR)) {
                    this.setIdlePose(1);//DEBUG LINE
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }

                // Sit check
                InteractionResult emptyhand = super.mobInteract(player, hand);
                if (player.isShiftKeyDown() && !emptyhand.consumesAction()) {
                    this.setOrderedToSit(!this.isOrderedToSit()); // toggle the opposite of sit
                    this.navigation.stop();
                    this.flying = false;
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            // Untamed interactions
            // Tame check
            } else if (stack.is(Items.GLISTERING_MELON_SLICE) && !this.isTame()) {
                    stack.shrink(1);
                    // Have a 1-3 chance of taming the amaro
                    if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                        this.tame(player);
                        this.navigation.stop();
                        this.level.broadcastEntityEvent(this, (byte)7);
                    } else {
                        this.level.broadcastEntityEvent(this, (byte)6);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
        }
        return super.mobInteract(player,hand);
    }

    //Mob Attributes
    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
    }

    private boolean isHealItem(Item food) {
        if (food == Items.MELON_SLICE || food == Items.APPLE || food == Items.GLOW_BERRIES ) {
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
            this.level.playSound((Player)null, this, SoundEvents.HORSE_SADDLE, pSource, 0.5F, 1.0F);
        }
    }

    @Override
    public Entity getControllingPassenger() {
        List<Entity> list = getPassengers();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
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
    public void positionRider(Entity passenger) {
        super.positionRider(passenger);

        Entity rider = getControllingPassenger();
        if (rider != null) {
            // Set the position of the rider to the current amaro position
            passenger.setPos(this.getX(), this.getY() + (this.getBbHeight() - 1.75),
                    this.getZ());
            // Face the rider the same direction the amaro is facing.
            if (rider instanceof LivingEntity) {
                ((LivingEntity) rider).yBodyRot = this.yBodyRot;
            }
        }

        if (getFirstPassenger() instanceof LivingEntity) {
            LivingEntity r = ((LivingEntity) rider);
            r.xRotO = r.getXRot();
            r.yRotO = r.getYRot();
            rider.setYBodyRot(yBodyRot);
        }
    }
    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public void travel(Vec3 vec3) {
        LivingEntity rider = (LivingEntity) this.getControllingPassenger();
        if (rider != null && this.isVehicle()) {

            this.setYRot(rider.getYRot()); // set the y rotation to the riders rotation
            this.yRotO = this.getYRot();

            this.setXRot(rider.getXRot()); // set the x rotation to the riders rotation
            this.setRot(this.getYRot(), this.getXRot());

            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            double strafex = rider.xxa * 0.5f;
            double yascend = rider.yya;
            double forwardz = rider.zza;

            Vec3 jvec = this.getDeltaMovement();

            // make backward movement twice as slow.
            if (forwardz <= 0.0f) {
                forwardz *= 0.5f;
            }
            // While flying, move towards where the rider is facing.
            if (flying && !isElytraFlying()) {
                this.moveRelative(0.1F,new Vec3(strafex, yascend, forwardz));
                //LOGGER.debug("deltamovement is " + jvec.x + "," + jvec.y + "," + jvec.z);
            }

            // Launch off the ground with more power
            if (this.isJumping && this.isOnGround() && !this.isElytraFlying()) {
                this.setDeltaMovement(jvec.x, 1.8, jvec.z);
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
                this.isJumping = false;
            } // Launch in the air with less power
            if (this.flying && this.isJumping && !this.isElytraFlying()) {
                this.setDeltaMovement(jvec.x, jvec.y + 1.5, jvec.z);
                this.isJumping = false;
            }
            // Launch the amaro forward in the air with elytra gliding, similar to a minecraft rocket.
            if (this.flying && this.isJumping && this.isElytraFlying()) {
                ModPackets.sendToServer(new AmaroFlightDashC2SPacket());
                this.isJumping = false;
            }

            // Descend the amaro if the Descend key is called
            if (this.flying && this.descend && !this.isElytraFlying()) {
                this.moveRelative(0.1F, new Vec3(strafex, -20, forwardz));
            }

            // When the rider is controlling, set the movement vector
            if (this.isControlledByLocalInstance()) {
                this.setSpeed((float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.2)); // set the speed and multiply it by 20%
                // While gliding, use the look vector to elytra glide.
                if (this.isElytraFlying()) {
                    Vec3 look = this.getLookAngle();
                    ElytraGlideCalculation.calculateGlide(this, look);

                } else {
                    super.travel(new Vec3(strafex, yascend, forwardz));
                }

            }

            // If there is no player movement, don't move the mob
            else if (rider instanceof Player) {
                this.setDeltaMovement(Vec3.ZERO);
                return;
            }

            if (isOnGround()) {
                this.flying = false;
                this.isJumping = false;
                this.descend = false;
                this.setElytraFlying(false);
            }

        } else {
            super.travel(vec3);
        }

    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vec = this.getDeltaMovement();
        if (!this.onGround && vec.y < 0.0D && !this.isElytraFlying()) {
            this.setDeltaMovement(vec.multiply(1.0D, 0.6D, 1.0D)); // lower the gravity to 0.6
            this.flying = true;
        }

    }

    @Override
    public void onPlayerJump(int pJumpPower) {
        this.getCapability(AmaroFlightProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
            if (this.isSaddled() && amaroFlight.getFlightPower() > 0) {
                this.flying = true;
                this.isJumping = true;
                //ModPackets.sendToServer(new AmaroFlightPowerC2SPacket(-1));
                amaroFlight.subFlightPower(1);
            }
        });
    }

    @Override
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void handleStartJump(int pJumpPower) {

    }

    @Override
    public void handleStopJump() {

    }
}
