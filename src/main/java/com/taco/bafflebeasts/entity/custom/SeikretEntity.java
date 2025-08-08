package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.ModEntityTypes;
import com.taco.bafflebeasts.entity.client.SeikretInventoryMenu;
import com.taco.bafflebeasts.entity.goal.*;

import com.taco.bafflebeasts.sound.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeikretEntity extends IdleAnimatedEntity implements GeoEntity, PlayerRideable, PlayerRideableJumping, NeutralMob, ContainerListener, HasCustomInventoryScreen, RangedAttackMob {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final SeikretRangedAttackGoal bowGoal = new SeikretRangedAttackGoal(this,1.1D,20,90);
    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false);

    private static final RawAnimation SEIKRET_BLINK = RawAnimation.begin().thenLoop("seikret.blink");
    private static final RawAnimation SEIKRET_NEUTRAL = RawAnimation.begin().thenLoop("seikret.neutral");
    private static final RawAnimation SEIKRET_WALK = RawAnimation.begin().thenLoop("seikret.walk");
    private static final RawAnimation SEIKRET_RUN = RawAnimation.begin().thenLoop("seikret.run");
    private static final RawAnimation SEIKRET_SPRINT = RawAnimation.begin().thenLoop("seikret.sprint");
    private static final RawAnimation SEIKRET_GLIDE = RawAnimation.begin().thenLoop("seikret.glide");
    private static final RawAnimation SEIKRET_JUMP = RawAnimation.begin().thenPlay("seikret.jump");
    private static final RawAnimation SEIKRET_SIT = RawAnimation.begin().thenPlay("seikret.sitdown").thenLoop("seikret.sit");
    private static final RawAnimation SEIKRET_SLEEP = RawAnimation.begin().thenPlay("seikret.gotosleep").thenLoop("seikret.sleep");
    private static final RawAnimation SEIKRET_ATTACK = RawAnimation.begin().thenPlay("seikret.attack");
    private static final RawAnimation SEIKRET_BOWSTANCE = RawAnimation.begin().thenLoop("seikret.bowstance");
    private static final RawAnimation SEIKRET_IDLE1 = RawAnimation.begin().thenPlay("seikret.idle1");
    private static final RawAnimation SEIKRET_IDLE2 = RawAnimation.begin().thenPlay("seikret.idle2");
    private static final RawAnimation SEIKRET_IDLE3 = RawAnimation.begin().thenPlay("seikret.idle3");
    private static final RawAnimation SEIKRET_IDLE4 = RawAnimation.begin().thenPlay("seikret.idle4");
    private static final RawAnimation SEIKRET_IDLE5 = RawAnimation.begin().thenPlay("seikret.idle5");

    private static final EntityDataAccessor<Boolean> HAS_SADDLE = SynchedEntityData.defineId(SeikretEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SEIKRET_VARIANT = SynchedEntityData.defineId(SeikretEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SPRINT = SynchedEntityData.defineId(SeikretEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_RANGED_WEAPON = SynchedEntityData.defineId(SeikretEntity.class,EntityDataSerializers.BOOLEAN);

    public boolean gliding = false;
    public boolean isJumping = false;
    public SimpleContainer inventory;
    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = null;

    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;

    public SeikretEntity(EntityType<? extends IdleAnimatedEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel,  5, 3);
        this.createInventory();
        this.reassessWeaponGoal();
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ATTACK_SPEED, 2.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.ARMOR, 4.0d)
                .add(Attributes.FOLLOW_RANGE, 48.0f).build();
    }

    @Nullable
    // Set Seikret variant when spawning mob into the world.
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        this.setVariant((int)(Math.random() * 6) + 1);
        if (pSpawnData == null) {
            pSpawnData = new AgeableMob.AgeableMobGroupData(false);
        }

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsTargetGoal(this, 1.5D, 32.0F));
        this.goalSelector.addGoal(5, new FloatGoal(this));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(7, new IdleAnimationGoal(this, 5));
        this.goalSelector.addGoal(8, new FollowOwnerGoal(this,1.2d,9,4,true));
        this.goalSelector.addGoal(9, new IdleEntityLookAtPlayer(this, Player.class, 9F));
        this.goalSelector.addGoal(9, new IdleEntityRandomLookAtGoal(this));
        this.goalSelector.addGoal(10, new RandomStrollGoal(this,1.0d));

        this.targetSelector.addGoal(1,new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(4, new OwnerHurtByTargetGoal(this));
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SADDLE, false);
        this.entityData.define(SEIKRET_VARIANT, 1);
        this.entityData.define(SPRINT,false);
        this.entityData.define(HAS_RANGED_WEAPON,false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("SeikretHasSaddle", this.isSaddled());
        tag.putInt("SeikretVariant",this.getVariant());
        tag.putBoolean("SeikretSprint", this.getIsSprinting());
        tag.putBoolean("HasRangedWeapon", this.hasRangedWeapon());
        // Save Equipment Item
        if (!this.inventory.getItem(0).isEmpty()) {
            tag.put("EquipmentItem", this.inventory.getItem(0).save(new CompoundTag()));
        }

        // Save Inventory Items here
        ListTag inventoryTag = new ListTag();
        for (int i = 1; i < this.getInventorySize(); i++) {
            ItemStack inventoryStack = this.inventory.getItem(i);
            if (!inventoryStack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot",(byte)i);
                inventoryStack.save(itemTag);
                inventoryTag.add(itemTag);
            }
        }

        tag.put("Items", inventoryTag);
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSaddle(tag.getBoolean("SeikretHasSaddle"));
        this.setVariant(tag.getInt("SeikretVariant"));
        this.setSeikretSprint(tag.getBoolean("SeikretSprint"));
        this.setRangedWeapon(tag.getBoolean("HasRangedWeapon"));
        if (tag.contains("EquipmentItem")) {
            ItemStack weaponStack = ItemStack.of(tag.getCompound("EquipmentItem"));
            if (weaponStack.is(Tags.Items.TOOLS)) {
                this.inventory.setItem(0,weaponStack);
            }
        }

        ListTag inventoryTag = tag.getList("Items", 10);
        for (int i = 0; i < inventoryTag.size(); i++) {
            CompoundTag itemTag = inventoryTag.getCompound(i);
            int j = itemTag.getByte("Slot") & 255;
            if (j >= 1 && j < this.getInventorySize()) {
                this.inventory.setItem(j,ItemStack.of(itemTag));
            }
        }

        this.reassessWeaponGoal();

    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(pOtherAnimal instanceof SeikretEntity)) {
            return false;
        } else {
            SeikretEntity otherDrake = (SeikretEntity) pOtherAnimal;
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
        SeikretEntity offSpring = ModEntityTypes.Seikret.get().create(pLevel);

        return offSpring;
    }

    protected SoundEvent getAmbientSound() {
        return SoundRegistry.SEIKRET_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundRegistry.SEIKRET_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SoundRegistry.SEIKRET_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SHEEP_STEP, 1.00F, 0.5f);
    }

    private <E extends GeoAnimatable> PlayState movementPredicate(AnimationState<E> event) {
        // Movement Checks
        if (event.isMoving() && this.onGround() && !this.hasControllingPassenger() && !this.isInSittingPose()) {
            event.getController().setAnimation(SEIKRET_WALK);
            return PlayState.CONTINUE;
        } else if (this.getSharedFlag(3) && this.onGround() && this.isMoving) {
            event.getController().setAnimation(SEIKRET_SPRINT);
            return PlayState.CONTINUE;
        } else if (this.isMoving && this.onGround() && this.hasControllingPassenger()) {
            event.getController().setAnimation(SEIKRET_RUN);
            return PlayState.CONTINUE;
        // Sprint animation, shared flag 3 is the sprint check.
        } else if (!this.onGround() && this.gliding) { // Fly Animation
            event.getController().setAnimation(SEIKRET_GLIDE);
            return PlayState.CONTINUE;
        }

        else {
            // Sitting Pose
            if (this.isInSittingPose() && !this.isAsleep() && !this.getEntityWakeUpState()) {
                event.getController().stop();
                event.getController().setAnimation(SEIKRET_SIT);
                return PlayState.CONTINUE;
            }
            // Sleep check
            if (this.isAsleep() && (this.isInSittingPose() || !this.isTame())) {
                event.getController().stop();
                event.getController().setAnimation(SEIKRET_SLEEP);
                return PlayState.CONTINUE;
            }
            // Neutral if all else passes
            if (!this.isInSittingPose()) {
                event.getController().setAnimation(SEIKRET_NEUTRAL);
                return PlayState.CONTINUE;
            }
        }

        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
        int idlePose = this.getIdlePose();

        // Bow Stance
        if (this.hasRangedWeapon() && this.onGround() && ((!this.isAsleep()) || !this.isInSittingPose())) {
            event.getController().setAnimation(SEIKRET_BOWSTANCE);
            return PlayState.CONTINUE;
        } else if (!this.hasRangedWeapon() || this.gliding) {
            event.getController().stop();
        }


        // Idle Animations
        if (!event.isMoving() && this.onGround() && !this.isInSittingPose() && !this.isAsleep() && !this.hasControllingPassenger()) {
            switch (idlePose) {
                case 1: event.getController().setAnimation(SEIKRET_IDLE1);
                    return PlayState.CONTINUE;
                case 2: event.getController().setAnimation(SEIKRET_IDLE2);
                    return PlayState.CONTINUE;
                case 3: event.getController().setAnimation(SEIKRET_IDLE3);
                    return PlayState.CONTINUE;
                case 4: event.getController().setAnimation(SEIKRET_IDLE4);
                    return PlayState.CONTINUE;
                case 5: event.getController().setAnimation(SEIKRET_IDLE5);
                    return PlayState.CONTINUE;
            }
        }


        return PlayState.STOP;
    }

    private <E extends GeoAnimatable> PlayState blinkPredicate(AnimationState<E> event) {
        if (!this.isAsleep()) {
            event.getController().setAnimation(SEIKRET_BLINK);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationState<E> event) {

        if (this.swinging == true) {
            event.getController().setAnimation(SEIKRET_ATTACK);
            return PlayState.CONTINUE;
        }

        event.getController().forceAnimationReset();

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<SeikretEntity> movementController = new AnimationController(this, "movement", 0, this::movementPredicate)
                .triggerableAnim("jump",SEIKRET_JUMP);

        controllers.add(movementController);
        controllers.add(new AnimationController(this, "idle", 15, this::idlePredicate));
        controllers.add(new AnimationController(this, "blink", 0, this::blinkPredicate));
        controllers.add(new AnimationController(this, "attack", 0, this::attackPredicate));
    }

    protected void createInventory() {
        SimpleContainer simplecontainer = this.inventory;
        this.inventory = new SimpleContainer(this.getInventorySize());
        if (simplecontainer != null) {
            simplecontainer.removeListener(this);
            int i = this.inventory.getContainerSize();

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = simplecontainer.getItem(j);
                if (!itemstack.isEmpty()) {
                    this.inventory.setItem(j, itemstack.copy());
                }
            }
        }

        this.inventory.addListener(this);
        this.updateContainerEquipment();
        this.itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> new net.minecraftforge.items.wrapper.InvWrapper(this.inventory));
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.core.Direction facing) {
        if (capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && itemHandler != null && this.isAlive())
            return itemHandler.cast();
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (itemHandler != null) {
            net.minecraftforge.common.util.LazyOptional<?> oldHandler = itemHandler;
            itemHandler = null;
            oldHandler.invalidate();
        }
    }

    public boolean hasInventoryChanged(Container pInventory) {
        return this.inventory != pInventory;
    }

    protected void updateContainerEquipment() {
        // Update Item in hand if placed in weapon slot
        if (!this.level().isClientSide) {
            this.setSharedFlag(4, !this.inventory.getItem(0).isEmpty());
        }
    }

    @Override
    public void containerChanged(Container pContainer) {
        this.setItemSlot(EquipmentSlot.MAINHAND, this.inventory.getItem(0));
        String weapon = this.checkWeapon(this.inventory.getItem(EquipmentSlot.MAINHAND.getIndex()));
        if (weapon.equals("BOW") || weapon.equals("CROSSBOW")) {
            this.setRangedWeapon(true);
        } else if (weapon.equals("MELEE")) {
            this.setRangedWeapon(false);
        }
        this.reassessWeaponGoal();
    }

    private int getInventorySize() {
        return 16;
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
    protected float getRiddenSpeed(Player pPlayer) {
        if (this.isControlledByLocalInstance()) {
            // Sprint flag
            if (this.getSharedFlag(3)) {
                return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.3f;
            } else {
                return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.1f;
            }
        }

        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    @Override
    public void tick() {
        super.tick();
    }

    /**
     * Called whenever the inventory updates to change from melee to ranged attacks.
     */
    public void reassessWeaponGoal() {
        if (this.level() != null && !this.level().isClientSide()) {
            // Remove attack goals
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);

            // Get Weapon Type and check
            String weapon = this.checkWeapon(this.getMainHandItem());
            if (weapon.equals("BOW") || weapon.equals("CROSSBOW")) {
                this.bowGoal.setMinAttackInterval(20);
                this.goalSelector.addGoal(3, this.bowGoal);

            } else if(weapon.equals("MELEE")) {
                this.goalSelector.addGoal(3,this.meleeGoal);
            }


        }
    }

    /**
     * Get's the projectile from the Seikret's inventory to be used as ammo.
     * Returns null if no ammo is found.
     * @param pVelocity
     * @return
     */
    @Nullable
    protected SeikretProjectileData getProjectileFromInventory(float pVelocity) {
        ArrayList<Projectile> projectiles = new ArrayList<>();
        // Check through each inventory slot in the Seikret and check if the slot is ammo.
        // Create a projectile and return the Projectile data with the slot in the inventory, and projectile.

        for (int i = 1; i < this.getInventorySize(); i++) {
            ItemStack item = this.inventory.getItem(i);
            // If the weapon has infinity or an arrow.
            if (item.getItem() instanceof ArrowItem || this.weaponHasInfinity()) {
                if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MULTISHOT,this.getMainHandItem()) > 0) {
                    for (int j = 0; j < 3; j++) {
                        projectiles.add(this.getArrows(item,pVelocity));
                    }
                    return new SeikretProjectileData(i,projectiles);
                } else {
                    projectiles.add(this.getArrows(item,pVelocity));
                    return new SeikretProjectileData(i,projectiles);
                }

            }
            // Firework check.
            if (item.getItem() instanceof FireworkRocketItem fireworkItem) {
                if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MULTISHOT,this.getMainHandItem()) > 0) {
                    for (int j = 0; j < 3; j++) {
                        Projectile fireworkProjectile = new FireworkRocketEntity(this.level(),item,this,this.getX(),
                                this.getEyeY() - 0.5D,this.getZ(),true);
                        projectiles.add(fireworkProjectile);
                    }
                    return new SeikretProjectileData(i,projectiles);
                } else {
                    Projectile fireworkProjectile = new FireworkRocketEntity(this.level(),item,this,this.getX(),
                            this.getEyeY() - 0.5D,this.getZ(),true);
                    projectiles.add(fireworkProjectile);
                    return new SeikretProjectileData(i,projectiles);
                }
            }

        }

        return null;
    }


    private Projectile getArrows(ItemStack item, float pVelocity) {
        // Get the arrow item from the inventory slot, and create it's data into a projectile.
        ArrowItem arrowItem = (ArrowItem)(item.getItem() instanceof ArrowItem ? item.getItem() : Items.ARROW);
        AbstractArrow abstractArrow = arrowItem.createArrow(this.level(),item,this);
        abstractArrow.setEnchantmentEffectsFromEntity(this,pVelocity);

        // If it's a crossbow, set it's pierce level.
        if (this.checkWeapon(this.getMainHandItem()).equals("CROSSBOW")) {
            abstractArrow.setShotFromCrossbow(true);
            int pierceLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PIERCING, this.getMainHandItem());

            if (pierceLevel > 0) {
                abstractArrow.setPierceLevel((byte)pierceLevel);
            }

        }

        // Set the potion effect of the arrow.
        if (item.is(Items.TIPPED_ARROW) && abstractArrow instanceof Arrow) {
            ((Arrow)abstractArrow).setEffectsFromItem(item);
        }


        return abstractArrow;
    }

    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        // Get projectile for currently held weapon
        //AbstractArrow ammoProjectile = this.getProjectileFromInventory(pDistanceFactor);
        SeikretProjectileData projectileData = this.getProjectileFromInventory(pDistanceFactor);
        ArrayList<Projectile> projectiles = projectileData.getProjectiles();


        if (projectiles != null) {

            Projectile genericProjectile = projectiles.get(0);
            // Get target Cords
            double d0 = pTarget.getX() - this.getX();
            double d1 = pTarget.getY(0.3333333333333333D) - genericProjectile.getY();
            double d2 = pTarget.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);

            // Shoot an arrow projectile.
            if (projectiles.get(0) instanceof AbstractArrow) {

                AbstractArrow ammoProjectile = (AbstractArrow)(projectiles.get(0));
                // Shoot arrow
                ammoProjectile.setCritArrow(true);
                // For Bows, fire the projectile normally.
                if (this.checkWeapon(this.getMainHandItem()).equals("BOW")) {
                    ammoProjectile.shoot(d0, d1 + d3 * (double)0.1F, d2, 2.0f, 0);
                    this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                    this.level().addFreshEntity(ammoProjectile);

                    // For crossbows, check if it has multishot.
                } else if (this.checkWeapon(this.getMainHandItem()).equals("CROSSBOW")) {

                    if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MULTISHOT,this.getMainHandItem()) > 0) {
                        this.fireCrossbowBolt(projectiles.get(0),2.0f,0,-10);
                        this.fireCrossbowBolt(projectiles.get(1),2.0f,0, 0);
                        this.fireCrossbowBolt(projectiles.get(2),2.0f,0,10);
                        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));

                    } else {
                        ammoProjectile.shoot(d0, d1 + d3 * (double)0.1F, d2, 2.0f, 0);
                        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                        this.level().addFreshEntity(ammoProjectile);
                    }
                }

            // Shoot a firework Projectile.
            } else if (projectiles.get(0) instanceof FireworkRocketEntity rocketProjectile) {
                if (this.checkWeapon(this.getMainHandItem()).equals("CROSSBOW")) {
                    if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.MULTISHOT,this.getMainHandItem()) > 0) {
                        this.fireCrossbowBolt(projectiles.get(0),2.0f,0,-10);
                        this.fireCrossbowBolt(projectiles.get(1),2.0f,0, 0);
                        this.fireCrossbowBolt(projectiles.get(2),2.0f,0,10);

                    } else {
                        rocketProjectile.shoot(d0, d1 + d3 * (double)0.1F, d2, 2.0f, 0);
                        this.level().addFreshEntity(rocketProjectile);
                    }

                }
            }

            // Take one arrow away from the inventory unless it is infinity.
            if (weaponHasInfinity()) {

            } else {
                this.inventory.getItem(projectileData.getInventoryIndex()).shrink(1);
            }

            // Damage the weapon and break it on each shot.
            ItemStack weapon = this.getMainHandItem();
            weapon.hurt(1, this.random, null);
            if (weapon.getDamageValue() > weapon.getMaxDamage()) {
                weapon.hurtAndBreak(1, this, onBroken -> {
                    onBroken.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
            }

        }
    }

    // Create multishot functionality next.
    public void fireCrossbowBolt(Projectile projectileData, float pVelocity, float pInaccuracy, float pProjectileAngle) {

        if (projectileData instanceof AbstractArrow projectile) {
            Vec3 vec31 = this.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double)(pProjectileAngle * ((float)Math.PI / 180F)), vec31.x, vec31.y, vec31.z);
            Vec3 vec3 = this.getViewVector(1.0F);
            Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
            projectile.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), pVelocity, pInaccuracy);
            this.level().addFreshEntity(projectile);

        } else if (projectileData instanceof FireworkRocketEntity fireworkProjectile) {
            Vec3 vec31 = this.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double)(pProjectileAngle * ((float)Math.PI / 180F)), vec31.x, vec31.y, vec31.z);
            Vec3 vec3 = this.getViewVector(1.0F);
            Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
            fireworkProjectile.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), pVelocity, pInaccuracy);
            this.level().addFreshEntity(fireworkProjectile);
        }
    }

    public boolean weaponHasInfinity() {
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.INFINITY_ARROWS,this.getMainHandItem()) > 0) {
            return true;
        }
        return false;
    }

    public boolean canShootWeapon() {
        if (weaponHasInfinity()) {
            return true;
        } else if (this.getProjectileFromInventory(10) == null) {
            return false;
        }
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        // Tame Interacts
        if (this.isTame()) {
            // Breed Check
            if (itemStack.is(Items.HAY_BLOCK) && this.age == 0 && this.canFallInLove()) {
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
                return InteractionResult.SUCCESS;
            }

        // Non tame checks
        } else {
            // Tame check
            if (itemStack.is(Items.WHEAT)) {
                itemStack.shrink(1);
                // Have a 1-3 chance of taming the Seikret
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

            // Heal Check
            if (this.isHealItem(itemStack.getItem())) {
                itemStack.shrink(1);
                this.spawnTamingParticles(true);
                this.heal(4.0f);
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void tickRidden(Player pPlayer, Vec3 travelVec) {
        super.tickRidden(pPlayer, travelVec);
        if (this.level().isClientSide()) {
            // Set the mob to look at where the player is and rotate the body too.
            Vec2 riderLookVec = new Vec2(pPlayer.getXRot(), pPlayer.getYRot());
            this.setRot(riderLookVec.y, riderLookVec.x);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

            AttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
            double gravityValue = gravity.getValue();

            // If there's no gravity, add the gravity back. This is used to workaround the "Kicked for flying a vehicle" check
            if (this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -gravityValue, 0.0D));
            }

            double strafex = pPlayer.xxa * 0.5f;
            double yascend = pPlayer.yya;
            double forwardz = pPlayer.zza;

            // make backward movement twice as slow.
            if (forwardz <= 0.0f) {
                forwardz *= 0.5f;
            }

            if (this.isControlledByLocalInstance()) {

                Vec3 jvec = this.getDeltaMovement();
                // Launch off the ground with more power
                if (this.isJumping && this.onGround()) {
                    this.setDeltaMovement(jvec.x, 1.8, jvec.z);
                    this.isJumping = false;
                }
                // If spacebar is being held, descend slowly. This will still display the jumping UI element.

                if (gliding  && !this.onGround()) {
                    this.setDeltaMovement(jvec.multiply(1.0D, 0.6D, 1.0D)); // lower the gravity to 0.6
                }

            }
        }

        // If on ground, set all fly states to false;
        if (onGround()) {
            this.gliding = false;
            this.isJumping = false;

        }

        if (this.level().isClientSide) {
            if (this.isControlledByLocalInstance()) {
                this.isMovingCheck();
            }
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player pPlayer, Vec3 travelVec) {
        double strafex = pPlayer.xxa * 0.5f;
        double yascend = pPlayer.yya;
        double forwardz = pPlayer.zza;

        // make backward movement twice as slow.
        if (forwardz <= 0.0f) {
            forwardz *= 0.5f;
        }

        // While flying, move towards where the rider is facing.
        if (!this.onGround()) {
            this.moveRelative(0.1F,new Vec3(strafex, yascend, forwardz));
        }


        return new Vec3(strafex, yascend, forwardz);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
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
    public boolean canJump() {
        return (isJumping == false && this.isSaddled());
    }

    // Override the jump to no longer use flight power.
    @Override
    public void onPlayerJump(int pJumpPower) {
        if (this.isSaddled() && this.onGround()) {
            this.isJumping = true;
            this.triggerAnim("movement","jump");
        }
    }

    private boolean isHealItem(Item item) {
        return (item.equals(Items.CARROT) || (item.equals(Items.POTATO)));
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

    private void setSaddle(boolean b) {
        this.entityData.set(HAS_SADDLE, b);
    }

    public int getVariant() {
        return this.entityData.get(SEIKRET_VARIANT);
    }

    private void setVariant(int variant) {
        this.entityData.set(SEIKRET_VARIANT,variant);
    }

    public boolean getIsSprinting() {
        return this.getEntityData().get(SPRINT);
    }

    public void setSeikretSprint(boolean seikretSprint) {
        this.entityData.set(SPRINT,seikretSprint);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
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

    public boolean isSaddleable() {
        return (this.isTame() && !this.isSaddled());
    }

    public void equipSaddle(@Nullable SoundSource pSource) {
        entityData.set(HAS_SADDLE, true);
        if (pSource != null) {
            this.level().playSound((Player)null, this, SoundEvents.HORSE_SADDLE, pSource, 0.5F, 1.0F);
        }
    }

    public boolean isSaddled() {
        return this.entityData.get(HAS_SADDLE);
    }

    @Override
    public void handleStartJump(int pJumpPower) {

    }

    @Override
    public void handleStopJump() {

    }

    @Override
    public void openCustomInventoryScreen(Player pPlayer) {
        // CODE TO OPEN GUI GOES HERE
        SeikretEntity seikret = this;

        if (!this.level().isClientSide()) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer, new MenuProvider() {
                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player player) {
                    return new SeikretInventoryMenu(pContainerId, pPlayerInventory, seikret);
                }

                @Override
                public Component getDisplayName() {
                    return SeikretEntity.this.getName();
                }

            }, buffer -> buffer.writeInt(this.getId()));

        }
    }
    @Override
    public boolean doHurtTarget(Entity pEntity) {

        if (!this.getMainHandItem().isEmpty()) {
            ItemStack weapon = this.getMainHandItem();
            // Set the durability of the weapon to lose 1 by each swing, and check if it needs to be broken.
            weapon.hurt(1,this.random,null);
            if (weapon.getDamageValue() > weapon.getMaxDamage()) {
                weapon.hurtAndBreak(1, this, onBroken -> {
                    onBroken.broadcastBreakEvent(EquipmentSlot.MAINHAND);
                });
            }
        }

        return super.doHurtTarget(pEntity);
    }

    public int getInventoryColumns() {
        return 5;
    }

    public String checkWeapon(ItemStack weapon) {
        if (weapon.is(Tags.Items.TOOLS_BOWS)) {
            return "BOW";
        } else if (weapon.is( Tags.Items.TOOLS_CROSSBOWS)) {
            return "CROSSBOW";
        }

        return "MELEE";
    }

    public boolean hasRangedWeapon() {
        return entityData.get(HAS_RANGED_WEAPON);
    }

    public void setRangedWeapon(boolean b) {
        this.entityData.set(HAS_RANGED_WEAPON,b);
    }

    public void dropEquipment() {

        if (this.isSaddled()) {
            if (!this.level().isClientSide()) {
                this.spawnAtLocation(Items.SADDLE);
            }
        }
        
        for (int i = 0; i < this.getInventorySize(); i++) {
            ItemStack item = this.inventory.getItem(i);
            if (!this.level().isClientSide()) {
                this.spawnAtLocation(item);
            }
        }
        super.dropEquipment();
    }

}
