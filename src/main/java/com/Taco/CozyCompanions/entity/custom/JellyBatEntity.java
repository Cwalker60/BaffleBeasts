package com.Taco.CozyCompanions.entity.custom;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.goal.IdleAnimationGoal;
import com.Taco.CozyCompanions.entity.goal.JellyBatRoamGoal;
import com.Taco.CozyCompanions.entity.goal.JellyBatUpsideDownGoal;
import com.Taco.CozyCompanions.item.ModItems;
import com.Taco.CozyCompanions.sound.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.NotNull;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JellyBatEntity extends RideableFlightEntity implements IAnimatable, FlyingAnimal, IForgeShearable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private static final int MAX_TICKS_BEFORE_ROAM = 600;
    private static final int MAX_TICKS_UPSIDEDOWN_COOLDOWN = 600;
    private static final int FUR_REGROWTH = 2400;

    protected static final AnimationBuilder JELLYBAT_WALK = new AnimationBuilder().addAnimation("animation.jellybat.walk", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_NUETRAL = new AnimationBuilder().addAnimation("animation.jellybat.neutral", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_FLY = new AnimationBuilder().addAnimation("animation.jellybat.fly", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_BLINK = new AnimationBuilder().addAnimation("animation.jellybat.blink", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_GOTO_SLEEP = new AnimationBuilder().addAnimation("animation.jellybat.ground_gotosleep", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder JELLYBAT_GROUND_SLEEP = new AnimationBuilder().addAnimation("animation.jellybat.ground_sleep", ILoopType.EDefaultLoopTypes.LOOP);
    protected static final AnimationBuilder JELLYBAT_GROUND_IDLE1 = new AnimationBuilder().addAnimation("animation.jellybat.ground_idle1", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder JELLYBAT_GROUND_IDLE2 = new AnimationBuilder().addAnimation("animation.jellybat.ground_idle2", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
    protected static final AnimationBuilder JELLYBAT_UPSIDEDOWN = new AnimationBuilder().addAnimation("animation.jellybat.upsidedown", ILoopType.EDefaultLoopTypes.LOOP);

    private static final EntityDataAccessor<Boolean> HANGING_ON_CEILING = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_JELLY_FUR = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DONUT_EFFECT_COLOR = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.INT);

    private PathNavigation groundNavigation;
    private FlyingPathNavigation flyingNavigation;

    public BlockPos upsideDownBlock;
    public int animationbuffer = 5;

    private int ticksRoamCooldown;
    private int ticksUpsideDownCooldown;
    private int furRegrowthCooldown;

    public boolean roamDelay;
    public boolean upsideDownDelay;
    public MobEffect currentDonutEffect;

    public JellyBatEntity(EntityType<? extends RideableFlightEntity> entityType, Level level) {
        super(entityType, level, 4,100);
        this.flying = true;
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setTame(false);
        this.setNoGravity(true);
        furRegrowthCooldown = 0;
        ticksRoamCooldown = 0;
        ticksUpsideDownCooldown = 0;
        upsideDownDelay = false;
        roamDelay = false;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HANGING_ON_CEILING, false);
        this.entityData.define(HAS_JELLY_FUR, true);
        this.entityData.define(DONUT_EFFECT_COLOR, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("JellyBatIsUpsideDown", this.isUpsideDown());
        tag.putBoolean("JellyBatHasFur", this.hasFur());
        tag.putInt("JellyBatDonutColor", this.getDonutColor());
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setUpsideDown(tag.getBoolean("JellyBatIsUpsideDown"));
        this.setFur(tag.getBoolean("JellyBatHasFur"));
        this.setDonutColor(tag.getInt("JellyBatDonutColor"));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.1));
        this.goalSelector.addGoal(3, new IdleAnimationGoal(this, 2));
        this.goalSelector.addGoal(4, new JellyBatRoamGoal(this, 1.0d));
        this.goalSelector.addGoal(5, new JellyBatUpsideDownGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        this.flyingNavigation = new FlyingPathNavigation(this, pLevel);
        this.groundNavigation = new GroundPathNavigation(this, pLevel);
        return this.flyingNavigation;
    }
    //This swaps the navigation mode so that the bat can walk when landing.
    public void setNavigationModeToFlying(boolean b) {
       this.navigation.stop();
       if (b == true) {
           this.navigation = flyingNavigation;
       } else {
           this.navigation = groundNavigation;
       }
    }

    public boolean getFlyingNavigationState() {
        if (this.navigation instanceof FlyingPathNavigation) {
            return true;
        }

        return false;

    }

    public int getDonutColor() {
        return this.entityData.get(DONUT_EFFECT_COLOR);
    }

    public void setDonutColor(int color) {
        this.entityData.set(DONUT_EFFECT_COLOR, color);
    }

    public boolean hasFur() {
        return this.entityData.get(HAS_JELLY_FUR);
    }

    public void setFur(boolean b) {
        this.entityData.set(HAS_JELLY_FUR, b);
    }

    public boolean isShearable(@NotNull ItemStack item, Level level, BlockPos pos) {
        if (this.isAlive() && this.hasFur() && !this.isBaby()) {
            return true;
        }

        return false;
    }


    public List<ItemStack> onSheared(@Nullable Player player, @NotNull ItemStack item, Level level, BlockPos pos, int fortune) {
        level.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
        this.gameEvent(GameEvent.SHEAR, player);
        this.setFur(false);
        if (!level.isClientSide()) {
            this.setFur(false);
            int amount = 1 + this.getRandom().nextInt(3);
            List<ItemStack> donuts = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                donuts.add(new ItemStack(ModItems.JELLYBAT_DONUT.get()));
            }
            return donuts;
        }
        return Collections.emptyList();
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
        } else if (event.isMoving() && !this.isOnGround()) {
            event.getController().setAnimation(JELLYBAT_FLY);
            return PlayState.CONTINUE;
        } else {
            if (!event.isMoving() && this.isUpsideDown()) {
                event.getController().setAnimation(JELLYBAT_UPSIDEDOWN);
                return PlayState.CONTINUE;
            } else {
                event.getController().setAnimation(JELLYBAT_NUETRAL);
                return PlayState.CONTINUE;
            }
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
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.FLYING_SPEED, 0.6f).build();
    }

    protected SoundEvent getAmbientSound() {
        return SoundRegistry.JELLYBAT_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundRegistry.JELLYBAT_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.JELLYBAT_DEATH.get();
    }

    @Override
    public void tick() {
        super.tick();

        // If there is an active effect, take the first effect, check if it is already applied,
        // and then set the jellydonut effect to that. Also update the jellybat's wool color to the potion color.
        if (!this.getActiveEffectsMap().isEmpty()) {
            Map<MobEffect, MobEffectInstance> effects = this.getActiveEffectsMap();
            MobEffect potionEffect;
            for (Map.Entry<MobEffect, MobEffectInstance> e : effects.entrySet()) {
                potionEffect = e.getKey();
                //CozyCompanions.MAIN_LOGGER.debug("Current Effect on map is : " + potionEffect.toString());
                if (this.currentDonutEffect != potionEffect) {
                    this.currentDonutEffect = potionEffect;
                    //CozyCompanions.MAIN_LOGGER.debug("Current potion effect is " + currentDonutEffect.toString());
                   // CozyCompanions.MAIN_LOGGER.debug("Potion color is " + currentDonutEffect.getColor());
                    this.setDonutColor(currentDonutEffect.getColor());
                    break;
                }
            }
        }

        // Idle timer
        if (getIdleTimer() > 0) {
            setIdleTimer(getIdleTimer() - 1);
        }

        if (!this.hasFur()); {
            this.furRegrowthCooldown++;
            if (this.furRegrowthCooldown > FUR_REGROWTH) {
                this.furRegrowthCooldown = 0;
                this.setFur(true);
            }
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
        if (this.isUpsideDown()) {
            this.setDeltaMovement(Vec3.ZERO);

        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack is = pPlayer.getItemInHand(pHand);
//        if (this.isShearable(is, this.getLevel(), this.blockPosition())) {
//            this.onSheared(pPlayer, is, this.getLevel(), this.blockPosition(), 1);
//            this.setFur(false);
//        }


        return super.mobInteract(pPlayer, pHand);
    }

    public boolean isUpsideDown() {
        return this.entityData.get(HANGING_ON_CEILING);
    }

    public void setUpsideDown(boolean b) {
        entityData.set(HANGING_ON_CEILING, b);
    }

    @Override
    public void travel(Vec3 vec3) {
        super.travel(vec3);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.isOnGround()) {
            this.flying = true;
        }

        // When the bat is upsidedown, tick the roam delay before the bat is able to roam again.
        // Then put a delay on being upsidedown.
        if (this.ticksRoamCooldown < MAX_TICKS_BEFORE_ROAM && this.roamDelay) {
            this.ticksRoamCooldown++;
        } else if (this.ticksRoamCooldown >= MAX_TICKS_BEFORE_ROAM && this.roamDelay) {
            this.ticksRoamCooldown = 0;
            this.roamDelay = false;
            this.upsideDownDelay = true;
        }

        // When the bat is finished being upside down, tick the time before it can be upside down again.
        if (this.ticksUpsideDownCooldown < MAX_TICKS_UPSIDEDOWN_COOLDOWN && this.upsideDownDelay) {
            this.ticksUpsideDownCooldown++;
        } else if (this.ticksUpsideDownCooldown >= MAX_TICKS_UPSIDEDOWN_COOLDOWN && this.upsideDownDelay) {
            this.ticksUpsideDownCooldown = 0;
            this.upsideDownDelay = false;
        }


        // If the target upsideDownBlock is right below the bat, set upside down to true and stop moving.
        if (!isUpsideDown() && upsideDownBlock != null) {
            //CozyCompanions.MAIN_LOGGER.debug("Upside downblock is : " + upsideDownBlock.getX() + "," +  upsideDownBlock.getY() + "," + upsideDownBlock.getZ());
            //CozyCompanions.MAIN_LOGGER.debug("Current Block Position is : " + this.blockPosition().getX() + "," + this.blockPosition().getY() + "," + this.blockPosition().getZ());
            if (this.blockPosition().above(1).equals(upsideDownBlock) || this.blockPosition().above(2).equals(upsideDownBlock)) {
                CozyCompanions.MAIN_LOGGER.debug("Upside down block found!");
                this.setUpsideDown(true);
                this.setPos(upsideDownBlock.getX() + 0.5, upsideDownBlock.getY() - 1.2, upsideDownBlock.getZ() + 0.5);
                CozyCompanions.MAIN_LOGGER.debug("JellyBat pos after moving : " + this.getX() + "," + this.getY() + "," + this.getZ());
                this.setDeltaMovement(Vec3.ZERO);
                upsideDownBlock = null;
            }
        }
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