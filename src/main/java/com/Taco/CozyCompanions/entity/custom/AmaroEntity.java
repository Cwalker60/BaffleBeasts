package com.Taco.CozyCompanions.entity.custom;

import com.Taco.CozyCompanions.entity.goal.AmaroIdleGoal;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class AmaroEntity extends Animal implements IAnimatable, Saddleable, PlayerRideable, PlayerRideableJumping {

    private AnimationFactory factory = GeckoLibUtil.createFactory(this);


    private static final EntityDataAccessor<Integer> IDLE_POSE = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> IDLE_TIMER = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(AmaroEntity.class, EntityDataSerializers.BOOLEAN);

    private static final Logger LOGGER = LogUtils.getLogger();
    public boolean flying;
    public boolean isJumping;

    //Amaro Constructor
    public AmaroEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);

    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IDLE_POSE, 1);
        this.entityData.define(IDLE_TIMER, 400);
        this.entityData.define(HAS_SADDLE, false);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AmaroIdlePose", this.getIdlePose());
        tag.putInt("AmaroIdleTimer", this.getIdleTimer());
        tag.putBoolean("AmaroHasSaddle", this.isSaddled());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setIdlePose(tag.getInt("AmaroIdlePose"));
        this.setIdleTimer(tag.getInt("AmaroIdleTimer"));
        this.setSaddle(tag.getBoolean("AmaroHasSaddle"));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.250));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new AmaroIdleGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.00));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(7, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        // If the entity is walking, play the run/neutral animation!
        if (event.isMoving() && this.isOnGround()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.run",
                    ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        //If the entity is not on the ground, fly!
        if (!this.isOnGround()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.fly",
                    ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        // Otherwise, play the neutral pose.
        if (!event.isMoving() && this.isOnGround()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.neutral",
                    ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        int idlePose = getIdlePose();

        if (!event.isMoving() && this.isOnGround()) {
            switch (idlePose) {
                case 1: event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.idle1",
                        ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    return PlayState.CONTINUE;
                case 2: event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.idle2",
                        ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    return PlayState.CONTINUE;
                case 3: event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.idle3",
                        ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    return PlayState.CONTINUE;
                case 4: event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.idle4",
                        ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    return PlayState.CONTINUE;
                case 5: event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.idle5",
                        ILoopType.EDefaultLoopTypes.PLAY_ONCE));
                    return PlayState.CONTINUE;
            }
        }

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable> PlayState blinkPredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.amaro.blink",
                ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "walk",
                0, this::predicate));
        data.addAnimationController(new AnimationController(this, "idle",
                0, this::idlePredicate));
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
        return SoundEvents.CAT_PURR;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.CAT_HURT;
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

    protected int getInventorySize() {
        return 2;
    }


    @Override
    public void tick() {
        super.tick();

        if (getIdleTimer() > 0) {
            setIdleTimer(getIdleTimer() - 1);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Boolean to check if the player has a saddle and equip it
        boolean saddlecheck = !this.isSaddled() && this.isSaddleable() && stack.is(Items.SADDLE);
        if (saddlecheck) {
            stack.shrink(1); // remove saddle from players inventory.
            equipSaddle(getSoundSource());
            setSaddle(true);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        // Check to see if you can ride the Amaro
        if (isSaddled()) {
            if (!level.isClientSide) {
                setRidingPlayer(player);
                navigation.stop();
                setTarget(null);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
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

    @Override
    public boolean isSaddleable() {
        return this.isAlive();
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
        if (rider != null) {
            this.setYRot(rider.getYRot()); // set the y rotation to the riders rotation
            this.yRotO = this.getYRot();

            this.setXRot(rider.getXRot() * 0.5f); // set the x rotation to the riders rotation
            this.setRot(this.getYRot(), this.getXRot());

            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float strafex = rider.xxa * 0.5f;
            float forwardz = rider.zza;
            // set an acceleration if forwardx is 0;
            if (forwardz <= 0.0f) {
                forwardz *= 0.5f;
            }
            // Jump Code
            Vec3 jvec = this.getDeltaMovement();
            if (isJumping && this.isOnGround()) {
                this.setDeltaMovement(jvec.x, 1.2, jvec.z);
                isJumping = false;
            }
            if (flying && isJumping) {
                 this.setDeltaMovement(jvec.x, 1.2, jvec.z);
                 isJumping = false;
            }

            // when the rider is controlling, set the movement vector
            if (this.isControlledByLocalInstance()) {
                this.setSpeed((float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.1)); // set the speed and multiply it by 20%
                super.travel(new Vec3((double)strafex, vec3.y, (double)forwardz));
            }
            // if there is no player movement, don't move the mob
            else if (rider instanceof Player) {
                this.setDeltaMovement(Vec3.ZERO);
            }

            if (isOnGround()) {
                this.flying = false;
                this.isJumping = false;
            }
        } else {
            super.travel(vec3);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 vec = this.getDeltaMovement(); // get the vector
        if (!this.onGround && vec.y < 0.0D) {
            this.setDeltaMovement(vec.multiply(1.0D, 0.6D, 1.0D)); // lower the gravity to 0.6
        }

    }

    @Override
    public void onPlayerJump(int pJumpPower) {
        if (this.isSaddled()) {
            this.flying = true;
            this.isJumping = true;
        }
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
