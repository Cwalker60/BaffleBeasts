package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.networking.ModPackets;
import com.taco.bafflebeasts.networking.packet.IdleEntityMovementSyncC2S;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

/**
    IdleAnimatedEntity is a base class for mobs that will use "idle" animations. It will store booleans for GOTOSLEEPSTATE, ASLEEP, and WAKEUPSTATE
    It will also store a variable to represent what idle to use and a timer to show when it can idle again.
 */
public abstract class IdleAnimatedEntity extends TamableAnimal {

    protected static final EntityDataAccessor<Boolean> GOTOSLEEPSTATE = SynchedEntityData.defineId(IdleAnimatedEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> ASLEEP = SynchedEntityData.defineId(IdleAnimatedEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> WAKEUPSTATE = SynchedEntityData.defineId(IdleAnimatedEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> IDLE_POSE = SynchedEntityData.defineId(IdleAnimatedEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> IDLE_TIMER = SynchedEntityData.defineId(IdleAnimatedEntity.class, EntityDataSerializers.INT);

    /**
     * Maximum idle animations
     */
    private final int maxIdleCount;
    /**
     * Integer for when the mob will go to sleep based on being lower than the current idle pose.
     * sleepThreshold of 2 will go to sleep on idle_pose 1 and 2
     */
    private final int sleepThreshold;
    /**
     * Boolean for if the mob is moving to update to other clients.
     */
    public boolean isMoving;
    /**
     * Boolean to buffer if the mob has already moved so we don't need to update the client to animate the movement.
     */
    public boolean hasMoved;
    /**
     * Integer to delay gotosleep and wakeup animations by 5 tick. (May be refactored later)
     */
    private int animationbuffer = 5;

    /**
     * IdleAnimatedEntity is an entity that will take advantage of several idle animations. Mobs that extend will have 5 data fields for idle
     * poses, gotosleep state, asleep state, wakeup state, and idle timers.
     * @param pEntityType Entity extends tamable animal.
     * @param pLevel Level mob is created in.
     * @param idleAnimations Total idle animations.
     * @param sleepVar Threshhold for sleeping (aim for half of idleAnimations).
     */
    protected IdleAnimatedEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel, int idleAnimations, int sleepVar) {
        super(pEntityType, pLevel);
        this.maxIdleCount = idleAnimations;
        this.sleepThreshold = sleepVar;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IDLE_POSE, 1);
        this.entityData.define(IDLE_TIMER, 400);
        this.entityData.define(ASLEEP, false);
        this.entityData.define(GOTOSLEEPSTATE, false);
        this.entityData.define(WAKEUPSTATE, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("EntityIdlePose", this.getIdlePose());
        tag.putInt("EntityIdleTimer", this.getIdleTimer());
        tag.putBoolean("EntityGoToSleep", this.getGoToSleepState());
        tag.putBoolean("EntityAsleep", this.isAsleep());
        tag.putBoolean("EntityWakeUpState", this.getEntityWakeUpState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setIdlePose(tag.getInt("EntityIdlePose"));
        this.setIdleTimer(tag.getInt("EntityIdleTimer"));
        this.setSleep(tag.getBoolean("EntityAsleep"));
        this.setGoToSleepState(tag.getBoolean("EntityGoToSleep"));
        this.setEntityWakeUpState(tag.getBoolean("EntityWakeUpState"));
    }

    /** isMoving() will send a datapacket to ensure that the moving animation is synched via client/server.
     * isMoving will try to only send this data packet by checking if lastMoving is true/false to ensure a packet is only sent when the mob stops,
     * or starts moving. There probably is a better solution but this is what I have come up with.
     * */
    public void isMovingCheck() {
        Vec2 groundmovement = new Vec2((float)this.getDeltaMovement().x, (float)this.getDeltaMovement().z);
        groundmovement = groundmovement.normalized();

        boolean moving = (Mth.abs(groundmovement.x) > 0 || Mth.abs(groundmovement.y) > 0);
        if (!this.getSharedFlag(7)) {

            if (this.level().isClientSide) {
                if (moving == true && hasMoved == false) {
                    this.isMoving = true; hasMoved = true;
                    ModPackets.sendToServer(new IdleEntityMovementSyncC2S(true, this.getId(), this.getSharedFlag(7)));

                } else if (moving == false && hasMoved == true) {
                    this.isMoving = false; hasMoved = false;
                    ModPackets.sendToServer(new IdleEntityMovementSyncC2S(false, this.getId(), this.getSharedFlag(7)));
                }
            }
        }

    }

    //setIdleAnimation takes in an int, and sets the Amaro's next idle animation to that int.
    //ints will range from 1-5. if an invalid int is out of this range, it will default to 1.
    public void setIdlePose(int pose) {
        this.entityData.set(IDLE_POSE, pose);
        if (this.isTame()) {
            if (pose <= this.sleepThreshold) {
                this.setSleep(true);
            } else {
                this.setEntityWakeUpState(true);
            }
        }

    }

    public int getMaxIdleCount() {
        return this.maxIdleCount;
    }

    public int getSleepThreshold() {
        return this.sleepThreshold;
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

    public void setSleep(boolean b) {
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

    public boolean getEntityWakeUpState() { return this.entityData.get(WAKEUPSTATE);}

    public void setEntityWakeUpState(boolean b) {
        if (isAsleep()) {
            this.entityData.set(WAKEUPSTATE, b);
        }
    }

    @Override
    public void tick() {
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

        super.tick();
    }

    public boolean wantsToAttack(LivingEntity pTarget, LivingEntity pOwner) {
        if (pTarget instanceof TamableAnimal tamableTarget) {
            if (tamableTarget.getOwner() == this.getOwner()) {
                return false;
            }
        }
        return true;
    }

}
