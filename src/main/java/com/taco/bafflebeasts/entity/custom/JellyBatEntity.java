package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.goal.*;
import com.taco.bafflebeasts.item.JellyDonutItem;
import com.taco.bafflebeasts.item.ModItems;
import com.taco.bafflebeasts.sound.CustomSoundEvents;
import com.taco.bafflebeasts.sound.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JellyBatEntity extends RideableFlightEntity implements GeoEntity, FlyingAnimal, IForgeShearable {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    private static final int MAX_TICKS_BEFORE_ROAM = 600;
    private static final int MAX_TICKS_UPSIDEDOWN_COOLDOWN = 600;
    private static final int FUR_REGROWTH = 2400;

    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.GLOW_BERRIES, Items.APPLE, Items.MELON_SLICE);

    protected static final RawAnimation JELLYBAT_WALK = RawAnimation.begin().thenLoop("animation.jellybat.walk");
    protected static final RawAnimation JELLYBAT_NUETRAL = RawAnimation.begin().thenLoop("animation.jellybat.neutral");
    protected static final RawAnimation JELLYBAT_FLY = RawAnimation.begin().thenLoop("animation.jellybat.fly");
    protected static final RawAnimation JELLYBAT_BLINK = RawAnimation.begin().thenLoop("animation.jellybat.blink");
    protected static final RawAnimation JELLYBAT_GROUND_SLEEP = RawAnimation.begin().thenPlay("animation.jellybat.ground_gotosleep").thenLoop("animation.jellybat.ground_sleep");
    protected static final RawAnimation JELLYBAT_GROUND_IDLE1 = RawAnimation.begin().thenPlay("animation.jellybat.ground_idle1");
    protected static final RawAnimation JELLYBAT_GROUND_IDLE2 = RawAnimation.begin().thenPlay("animation.jellybat.ground_idle2");
    protected static final RawAnimation JELLYBAT_UPSIDEDOWN = RawAnimation.begin().thenPlay("animation.jellybat.upsidedown");

    private static final EntityDataAccessor<Boolean> HANGING_ON_CEILING = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_JELLY_FUR = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DONUT_EFFECT_COLOR = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DONUT_EFFECT = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SECONDARY_DONUT_EFFECT = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> SUPER_SIZE = SynchedEntityData.defineId(JellyBatEntity.class, EntityDataSerializers.BOOLEAN);

    private PathNavigation groundNavigation;
    private FlyingPathNavigation flyingNavigation;

    public BlockPos upsideDownBlock;
    public int animationbuffer = 5;

    private int ticksRoamCooldown;
    private int ticksUpsideDownCooldown;
    private int furRegrowthCooldown;

    public boolean roamDelay;
    public boolean upsideDownDelay;
    public boolean shouldTickRefresh = true;

    public JellyBatEntity(EntityType<? extends RideableFlightEntity> entityType, Level level) {
        super(entityType, level, 4,100);
        this.flying = true;
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setTame(false);
        this.setNoGravity(true);
        this.setDonutColor(0xFFFFFF);
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
        this.entityData.define(DONUT_EFFECT, "");
        this.entityData.define(SECONDARY_DONUT_EFFECT, "");
        this.entityData.define(SUPER_SIZE, false);
        this.entityData.define(HAS_SADDLE, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("JellyBatIsUpsideDown", this.isUpsideDown());
        tag.putBoolean("JellyBatHasFur", this.hasFur());
        tag.putInt("JellyBatDonutColor", this.getDonutColor());
        tag.putString("DonutEffect", this.getDonutEffect());
        tag.putString("DonutEffect2", this.get2ndDonutEffect());
        tag.putBoolean("SuperSize", this.getSuperSize());
        tag.putBoolean("HasSaddle", this.isSaddled());
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setUpsideDown(tag.getBoolean("JellyBatIsUpsideDown"));
        this.setFur(tag.getBoolean("JellyBatHasFur"));
        this.setDonutColor(tag.getInt("JellyBatDonutColor"));
        this.setDonutEffect(tag.getString("DonutEffect"));
        this.set2ndDonutEffect(tag.getString("DonutEffect2"));
        this.setSuperSize(tag.getBoolean("SuperSize"));
        this.setSaddle(tag.getBoolean("HasSaddle"));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public static boolean jellyBatSpawnRules(EntityType<? extends Entity> bat, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return (pLevel.getBlockState(pPos.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) || pLevel.getBlockState(pPos.below()).is(BlockTags.BASE_STONE_OVERWORLD) ||
                pLevel.getBlockState(pPos.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON));
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor pLevel, MobSpawnType pSpawnReason) {
        return true;
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new FloatGoal(this));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.1));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.2D, FOOD_ITEMS, false));
        this.goalSelector.addGoal(6, new JellyBatFollowOwnerGoal(this, 2.0D, 10.F, 2.0F, true));
        this.goalSelector.addGoal(7, new FlyEntityLookAtPlayer(this, Player.class, 6F));
        this.goalSelector.addGoal(8, new IdleAnimationGoal(this, 2));
        this.goalSelector.addGoal(9, new JellyBatRoamGoal(this, 1.0d));
        this.goalSelector.addGoal(10, new JellyBatUpsideDownGoal(this));
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
    public String getDonutEffect() {
        return this.entityData.get(DONUT_EFFECT);
    }

    public void setDonutEffect(String p) {
        this.entityData.set(DONUT_EFFECT, p);
    }

    public int getDonutColor() {
        return this.entityData.get(DONUT_EFFECT_COLOR);
    }

    public void setDonutColor(int color) {
        this.entityData.set(DONUT_EFFECT_COLOR, color);
    }

    public String get2ndDonutEffect() {
        return this.entityData.get(SECONDARY_DONUT_EFFECT);
    }

    public void set2ndDonutEffect(String p) {
        this.entityData.set(SECONDARY_DONUT_EFFECT, p);
    }


    public boolean hasFur() {
        return this.entityData.get(HAS_JELLY_FUR);
    }

    public void setFur(boolean b) {
        this.entityData.set(HAS_JELLY_FUR, b);
    }

    public boolean getSuperSize() {
        return this.entityData.get(SUPER_SIZE);
    }

    public void setSuperSize(boolean b) {

        this.entityData.set(SUPER_SIZE, b);
        if (b == true) {
            this.setPos(this.getPosition(0.0f).add(0.0d, 1.0d, 0.0d));
            this.setBoundingBox(this.getBoundingBox().inflate(2.0,2.0,2.0));
            this.refreshDimensions();
        }
    }

    public boolean isUpsideDown() {
        return this.entityData.get(HANGING_ON_CEILING);
    }

    public void setUpsideDown(boolean b) {
        entityData.set(HANGING_ON_CEILING, b);
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {

        if (this.getSuperSize() == true) {
            return super.getDimensions(pPose).scale(2.0f);
        }

        return pPose == Pose.SLEEPING ? SLEEPING_DIMENSIONS : super.getDimensions(pPose).scale(this.getScale());
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
            int amount;
            if (this.getSuperSize()) {
                amount = 5 + this.getRandom().nextInt(3);
            } else {
                amount = 1 + this.getRandom().nextInt(3);
            }

            List<ItemStack> donuts = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                ItemStack droppedItems = new ItemStack(ModItems.JELLYBAT_DONUT.get());
                Potion p = ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.getDonutEffect()));
                Potion p2 = ForgeRegistries.POTIONS.getValue(new ResourceLocation(this.get2ndDonutEffect()));

                JellyDonutItem.addEffects(droppedItems, p);
                JellyDonutItem.addSecondaryEffects(droppedItems,p2);
                JellyDonutItem.setDonutColor(droppedItems, this.getDonutColor());

//                BaffleBeasts.MAIN_LOGGER.debug(droppedItems.getTag().getString("Potion"));
//                BaffleBeasts.MAIN_LOGGER.debug(droppedItems.getTag().getString("SecondaryPotion"));

                donuts.add(droppedItems);
            }
            return donuts;
        }
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        JellyBatEntity baby = ModEntityTypes.JellyBat.get().create(pLevel);
        JellyBatEntity parent = (JellyBatEntity)pOtherParent;
        baby.setDonutEffect(this.getDonutEffect());
        baby.set2ndDonutEffect(parent.getDonutEffect());
        baby.setTame(true);

        if (baby.get2ndDonutEffect().isEmpty()) {
            baby.setDonutColor(this.getDonutColor());
        } else {
            int blendedColor = this.getDonutColor() + parent.getDonutColor();
            blendedColor /= 2;
            baby.setDonutColor(blendedColor);

        }
        return baby;
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (!(pOtherAnimal instanceof JellyBatEntity)) {
            return false;
        } else {
            JellyBatEntity otherBat = (JellyBatEntity)pOtherAnimal;
            if (otherBat.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && otherBat.isInLove();
            }
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        Item item = pStack.getItem();
        return (item == Items.GLOW_BERRIES);
    }

    private <E extends GeoAnimatable> PlayState movementPredicate(AnimationState<E> event) {
        if (event.isMoving() && this.onGround()) {
            event.getController().setAnimation(JELLYBAT_WALK);
            return PlayState.CONTINUE;
        } else if (event.isMoving() && !this.onGround()) {
            event.getController().setAnimation(JELLYBAT_FLY);
            return PlayState.CONTINUE;
        } else {
            if (!event.isMoving() && this.isUpsideDown()) {
                event.getController().setAnimation(JELLYBAT_UPSIDEDOWN);
                return PlayState.CONTINUE;
            } else if (!event.isMoving() && this.isInSittingPose() && this.isAsleep()) {
                event.getController().stop();
                event.getController().setAnimation(JELLYBAT_GROUND_SLEEP);
                return PlayState.CONTINUE;
            } else {
                event.getController().setAnimation(JELLYBAT_NUETRAL);
                return PlayState.CONTINUE;
            }
        }
    }

    private <E extends GeoAnimatable>PlayState blinkPredicate(AnimationState<E> event) {
        if (!this.isAsleep()) {
            event.getController().setAnimation(JELLYBAT_BLINK);
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private <E extends GeoAnimatable>PlayState idlePredicate(AnimationState<E> event) {
        if (!this.isInSittingPose() && !event.isMoving() && this.onGround()) {
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controller) {
        AnimationController<JellyBatEntity> movementController = new AnimationController(this, "movement", 15, this::movementPredicate);
        AnimationController<JellyBatEntity> blinkController = new AnimationController(this, "blink", 15, this::blinkPredicate);
        AnimationController<JellyBatEntity> idleController = new AnimationController(this, "idle", 15, this::idlePredicate);

        controller.add(movementController);
        controller.add(blinkController);
        controller.add(idleController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    public static AttributeSupplier setAttributes() {

        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 14)
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
    public void setIdlePose(int pose) {
        super.setIdlePose(pose);
        if (pose == 1) {
            this.setSleep(true);
        } else {
           this.setSleep(false);
        }
    }

    @Override
    public void tick() {
        super.tick();
        // Do one tick to refresh dimensions when the JellyBat spawns on the world.
        if (shouldTickRefresh == true) {
            this.refreshDimensions();
            shouldTickRefresh = false;
        }
        // Idle timer
        if (getIdleTimer() > 0) {
            setIdleTimer(getIdleTimer() - 1);
        }

        // Regrow fur when sheared.
        if (!this.hasFur()) {
            this.furRegrowthCooldown++;
            if (this.furRegrowthCooldown > FUR_REGROWTH) {
                this.furRegrowthCooldown = 0;
                this.setFur(true);
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
        // If a player is trying to tempt a jellybat, set upsideDown to false and reset the cooldown.
        if (this.goalSelector.getRunningGoals().anyMatch(target -> (target.getGoal() instanceof TemptGoal) )) {
            if (this.isUpsideDown()) {
                this.setUpsideDown(false);
                this.ticksUpsideDownCooldown = MAX_TICKS_UPSIDEDOWN_COOLDOWN;
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);

        // Breeding Interactions
        int age = this.getAge();
        if (itemStack.is(Items.GLOW_BERRIES)) {
            if (!this.level().isClientSide && age == 0 && this.canFallInLove()) {
                this.usePlayerItem(pPlayer, pHand, itemStack);
                this.tame(pPlayer);
                this.setInLove(pPlayer);
                return InteractionResult.SUCCESS;
            }
            // Baby Growth Interaction
            if (this.isBaby()) {
                this.usePlayerItem(pPlayer, pHand, itemStack);
                this.ageUp(getSpeedUpSecondsWhenFeeding(-age), true);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }

        // Potion Check
        if (itemStack.getItem() instanceof PotionItem) {
            Potion potion = PotionUtils.getPotion(itemStack);
            String potionName = "";

            if (ForgeRegistries.POTIONS.containsValue(potion)) {
                potionName = ForgeRegistries.POTIONS.getKey(potion).getPath();
            }

            this.setDonutEffect(potionName);
            this.setDonutColor(potion.getEffects().get(0).getEffect().getColor());

            this.usePlayerItem(pPlayer, pHand, itemStack);
            this.level().playSound((Player)null, this, SoundEvents.BOTTLE_FILL, this.getSoundSource(), 0.5F, 1.0F);
            return InteractionResult.SUCCESS;
        }

        // Super Size Check
        if (itemStack.getItem().equals(ModItems.SUPER_SHAKE.get()) && !this.isBaby() && !this.getSuperSize()) {
            this.setSuperSize(true);
            this.usePlayerItem(pPlayer, pHand, itemStack);
            this.tame(pPlayer);
            this.level().playSound((Player)null, this, CustomSoundEvents.JELLYBAT_SUPERSIZE, this.getSoundSource(), 0.5F, 1.0F);

            ParticleOptions particleoptions = ParticleTypes.EXPLOSION;

            for(int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                double d3 = this.getRandomX(1.0D);
                double d4 = this.getRandomY() + 0.5D;
                double d5 = this.getRandomZ(1.0D);
                this.level().addParticle(particleoptions, d3, d4, d5, d0, d1, d2);
            }
        }

        // Saddle Equip check
        boolean saddlecheck = !this.isSaddled() && this.isSaddleable() && itemStack.is(Items.SADDLE) && !this.isBaby();
        if (saddlecheck) {
            itemStack.shrink(1); // remove saddle from players inventory.
            equipSaddle(getSoundSource());
            setSaddle(true);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        // Ride Check
        if (isSaddled() && this.isTame() && !pPlayer.isShiftKeyDown() && !itemStack.is(Items.SHEARS) && !this.isHealItem(itemStack.getItem())) {
            if (!level().isClientSide) {
                setRidingPlayer(pPlayer);
                this.setOrderedToSit(false);
                this.setEntityWakeUpState(true);
                navigation.stop();
                setTarget(null);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        // Sit check
        InteractionResult emptyhand = super.mobInteract(pPlayer, pHand);
        if (pPlayer.isShiftKeyDown() && !emptyhand.consumesAction() && this.isTame() && this.getSuperSize()) {
            this.setOrderedToSit(!this.isOrderedToSit()); // toggle the opposite of sit
            if (this.isOrderedToSit()) {
                pPlayer.displayClientMessage(Component.literal(this.getName().getString() + " is now sitting!"), true);
            } else {
                pPlayer.displayClientMessage(Component.literal(this.getName().getString() + " is now following!"), true);
            }

            this.navigation.stop();
            this.flying = false;
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        // Heal check
        if (isHealItem(itemStack.getItem()) && !pPlayer.isShiftKeyDown()) {
            itemStack.shrink(1);
            this.heal(4.0f);
            this.spawnTamingParticles(true);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }




        return super.mobInteract(pPlayer, pHand);
    }

    private boolean isHealItem(Item item) {
        if (item == Items.MELON_SLICE || item == Items.APPLE || item == Items.MELON_SLICE) {
            return true;
        }
        return false;
    }

    private void setRidingPlayer(Player pPlayer) {
        pPlayer.setYRot(getYRot());
        pPlayer.setXRot(getXRot());
        pPlayer.startRiding(this);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction pCallback) {
        super.positionRider(passenger, pCallback);

        Entity rider = getControllingPassenger();
        if (rider != null) {
            // Set the position of the rider to the current amaro position
            passenger.setPos(this.getX(), this.getY() + (this.getBbHeight() - 0.5),
                    this.getZ());

            // Face the rider the same direction the amaro is facing.
            if (rider instanceof LivingEntity) {
                ((LivingEntity) rider).yBodyRot = this.yBodyRot;
            }
        }

    }

    @Override
    public void tickRidden(Player pPlayer, Vec3 travelVec) {
        super.tickRidden(pPlayer, travelVec);

        if (this.hasControllingPassenger()) {
            this.setNoGravity(true);
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
        Vec3 vec = this.getDeltaMovement();
        if (!this.onGround() && vec.y < 0.0D && !this.isElytraFlying()) {
            this.setDeltaMovement(vec.multiply(1.0D, 0.6D, 1.0D)); // lower the gravity to 0.6
            this.flying = true;
        }
//        if (!this.isOnGround()) {
//            this.flying = true;
//        }

        // When the bat is upsidedown, tick the roam delay before the bat is able to roam again.
        // Then put a delay on being upsidedown.
        if (this.ticksRoamCooldown < MAX_TICKS_BEFORE_ROAM && this.roamDelay) {
            this.ticksRoamCooldown++;
        } else if (this.ticksRoamCooldown >= MAX_TICKS_BEFORE_ROAM && this.roamDelay) {
            this.ticksRoamCooldown = 0;
            this.roamDelay = false;
            this.upsideDownDelay = true;
            this.setOrderedToSit(false);
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
            //BaffleBeasts.MAIN_LOGGER.debug("Upside downblock is : " + upsideDownBlock.getX() + "," +  upsideDownBlock.getY() + "," + upsideDownBlock.getZ());
            //BaffleBeasts.MAIN_LOGGER.debug("Current Block Position is : " + this.blockPosition().getX() + "," + this.blockPosition().getY() + "," + this.blockPosition().getZ());
            if (this.blockPosition().above((int)this.getBbHeight() + 0).equals(upsideDownBlock)  || this.blockPosition().above((int)this.getBbHeight() + 2).equals(upsideDownBlock)) {
                this.setUpsideDown(true);
                this.setDeltaMovement(Vec3.ZERO);
                this.setOrderedToSit(true);
                this.navigation.stop();
                this.setPos(upsideDownBlock.getX() + 0.5, upsideDownBlock.getY() - this.getBbHeight(), upsideDownBlock.getZ() + 0.5);
                upsideDownBlock = null;
            }

        }
    }


    @Override
    public boolean isSaddleable() {
        return this.isAlive() && this.getSuperSize() && this.isTame();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource pSource) {
        entityData.set(HAS_SADDLE, true);
        if (pSource != null) {
            this.level().playSound((Player)null, this, SoundEvents.HORSE_SADDLE, pSource, 0.5F, 1.0F);
        }
    }

    public void setSaddle(boolean b) {
        this.entityData.set(HAS_SADDLE, b);
    }

    @Override
    public boolean isSaddled() {
        return this.entityData.get(HAS_SADDLE);
    }


}
