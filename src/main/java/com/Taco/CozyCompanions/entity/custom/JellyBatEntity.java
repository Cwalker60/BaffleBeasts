package com.Taco.CozyCompanions.entity.custom;

import com.Taco.CozyCompanions.entity.goal.IdleAnimationGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class JellyBatEntity extends RideableFlightEntity implements IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    protected static final AnimationBuilder JELLYBAT_WALK = new AnimationBuilder().addAnimation("animation.jellybat.walk", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_NUETRAL = new AnimationBuilder().addAnimation("animation.jellybat.neutral", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_BLINK = new AnimationBuilder().addAnimation("animation.jellybat.blink", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_GOTO_SLEEP = new AnimationBuilder().addAnimation("animation.jellybat.ground_gotosleep", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder JELLYBAT_GROUND_SLEEP = new AnimationBuilder().addAnimation("animation.jellybat.ground_sleep", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_GROUND_IDLE1 = new AnimationBuilder().addAnimation("animation.jellybat.ground_idle1", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder JELLYBAT_GROUND_IDLE2 = new AnimationBuilder().addAnimation("animation.jellybat.ground_idle2", ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    public int animationbuffer = 5;

    public JellyBatEntity(EntityType<? extends RideableFlightEntity> entityType, Level level) {
        super(entityType, level, 4,100);
        this.setTame(false);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.00));
        this.goalSelector.addGoal(3, new IdleAnimationGoal(this, 2));
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    private <E extends IAnimatable>PlayState movementPredicate(AnimationEvent<E> event) {
        if (event.isMoving() && this.isOnGround()) {
            event.getController().setAnimation(JELLYBAT_WALK);
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(JELLYBAT_NUETRAL);
            return PlayState.CONTINUE;
        }

    }

    private <E extends IAnimatable>PlayState blinkPredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(JELLYBAT_BLINK);

        return PlayState.CONTINUE;
    }

    private <E extends IAnimatable>PlayState idlePredicate(AnimationEvent<E> event) {
        if (!this.isInSittingPose() && !event.isMoving() && this.isOnGround()) {
            switch (getIdlePose()) {
                case 1 : event.getController().setAnimation(JELLYBAT_GROUND_IDLE1);
                    return PlayState.CONTINUE;

                case 2 : event.getController().setAnimation(JELLYBAT_GROUND_IDLE2);
                    return PlayState.CONTINUE;
            }
        }


        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController movementController = new AnimationController(this, "movement", 15, this::movementPredicate);
        AnimationController blinkController = new AnimationController(this, "blink", 15, this::blinkPredicate);
        AnimationController idleController = new AnimationController(this, "idle", 15, this::idlePredicate);

        data.addAnimationController(movementController);
        data.addAnimationController(blinkController);
        data.addAnimationController(idleController);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
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
    public void travel(Vec3 vec3) {
        super.travel(vec3);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource pSource) {

    }

    @Override
    public boolean isSaddled() {
        return false;
    }


}
