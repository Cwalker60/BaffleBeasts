package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.client.FlightPowerHud;
import com.taco.bafflebeasts.flight.FlightPowerProvider;
import com.taco.bafflebeasts.networking.ModPackets;
import com.taco.bafflebeasts.networking.packet.FlightEntityDashC2SPacket;
import com.taco.bafflebeasts.networking.packet.FlightEntityMovementSyncC2S;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;


import java.util.List;

public abstract class RideableFlightEntity extends TamableAnimal implements Saddleable, PlayerRideable, PlayerRideableJumping {

    public boolean flying = false;
    public boolean isJumping = false;
    public boolean descend = false;
    public boolean elytraFlying = false;
    public boolean flightGUIFlicker = false;

    public int maxFlightRechargeBuffer;
    public int flightRechargeBuffer;
    public int flightPower;

    protected static final EntityDataAccessor<Boolean> GOTOSLEEPSTATE = SynchedEntityData.defineId(RideableFlightEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> ASLEEP = SynchedEntityData.defineId(RideableFlightEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> WAKEUPSTATE = SynchedEntityData.defineId(RideableFlightEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> IDLE_POSE = SynchedEntityData.defineId(RideableFlightEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> IDLE_TIMER = SynchedEntityData.defineId(RideableFlightEntity.class, EntityDataSerializers.INT);

    public boolean isMoving;
    public boolean hasMoved;

    public RideableFlightEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel, int flightP, int flightRecharge) {
        super(pEntityType, pLevel);
        this.flightPower = flightP;
        this.maxFlightRechargeBuffer = flightRecharge;
        this.flightRechargeBuffer = this.maxFlightRechargeBuffer;
        this.setFlightStats();
    }

    public void setFlightStats() {
        this.getCapability(FlightPowerProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
            amaroFlight.setFlightPower(flightPower);
        });
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

    // isMoving() will send a datapacket to ensure that the moving animation is synched via client/server.
    // isMoving will try to only send this data packet by checking if lastMoving is true/false to ensure a packet is only sent when the mob stops,
    // or starts moving. There probably is a better solution but this is what I have come up with.
    public void isMovingCheck() {
        Vec2 groundmovement = new Vec2((float)this.getDeltaMovement().x, (float)this.getDeltaMovement().z);
        groundmovement = groundmovement.normalized();

        boolean moving = (Mth.abs(groundmovement.x) > 0 || Mth.abs(groundmovement.y) > 0);
        if (!this.isElytraFlying()) {

            if (this.level().isClientSide) {
                if (moving == true && hasMoved == false) {
                    this.isMoving = true; hasMoved = true;
                    ModPackets.sendToServer(new FlightEntityMovementSyncC2S(true, this.getId(), this.getSharedFlag(7)));

                } else if (moving == false && hasMoved == true) {
                    this.isMoving = false; hasMoved = false;
                    ModPackets.sendToServer(new FlightEntityMovementSyncC2S(false, this.getId(), this.getSharedFlag(7)));
                }
            }
        }

    }


    @Override
    public void travel(Vec3 vec3) {
        super.travel(vec3);
    }



    @Override
    public void tickRidden(Player pPlayer, Vec3 travelVec) {
        super.tickRidden(pPlayer, travelVec);
        // Set the mob to look at where the player is and rotate the body too.
        Vec2 riderLookVec = new Vec2(pPlayer.getXRot(), pPlayer.getYRot());
        this.setRot(riderLookVec.y, riderLookVec.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

        AttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
        double gravityValue = gravity.getValue();


        if (this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -gravityValue / 4.0D, 0.0D));
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
            if (this.isJumping && this.onGround() && !this.isElytraFlying()) {
                //this.executeRidersJump(travelVec, 1.8f);
                this.setDeltaMovement(jvec.x, 1.8, jvec.z);
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENDER_DRAGON_FLAP, this.getSoundSource(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
                this.isJumping = false;
            } // Launch in the air with less power
            if (this.flying && this.isJumping && !this.isElytraFlying()) {
                this.setDeltaMovement(jvec.x, jvec.y + 1.5, jvec.z);
                this.isJumping = false;
            }
            // Launch the amaro forward in the air with elytra gliding, similar to a minecraft rocket.
            if (this.flying && this.isJumping && this.isElytraFlying()) {
                ModPackets.sendToServer(new FlightEntityDashC2SPacket());
                this.isJumping = false;
            }
            // Descend the amaro if the Descend key is called
            if (this.flying && this.descend && !this.isElytraFlying()) {
                this.moveRelative(0.1F, new Vec3(strafex, -20, forwardz));
            }

            if (this.isElytraFlying()) {
                //ElytraGlideCalculation.calculateGlide(this, this.getLookAngle());
            }

        }

        // If on ground, set all fly states to false;
        if (onGround()) {
            this.flying = false;
            this.isJumping = false;
            this.descend = false;
            this.setElytraFlying(false);

        }

        if (this.level().isClientSide) {
            if (this.isControlledByLocalInstance()) {
                if (!this.isFlying()) {
                    this.isMovingCheck();
                }
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
        if (flying && !isElytraFlying()) {
            this.moveRelative(0.1F,new Vec3(strafex, yascend, forwardz));
            //LOGGER.debug("deltamovement is " + jvec.x + "," + jvec.y + "," + jvec.z);
        }


        return new Vec3(strafex, yascend, forwardz);
    }
    @Override
    protected float getRiddenSpeed(Player pPlayer) {
        if (this.isControlledByLocalInstance()) {
            return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.1f;
        }
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    protected void executeRidersJump(Vec3 travelVec, float jumpBonusHeight) {

    }

    @Override
    public void tick() {
        super.tick();
        getCapability(FlightPowerProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
            if (amaroFlight.getFlightPower() < amaroFlight.getMaXFlightPower()) {
                if (this.onGround()) {
                    this.flightRechargeBuffer -= 5;
                } else {
                    this.flightRechargeBuffer--;
                }
            }

            if (this.flightRechargeBuffer < 0) {
                this.flightRechargeBuffer = this.maxFlightRechargeBuffer;
                amaroFlight.addFlightPower(1);
                //ModPackets.sendToPlayer(new AmaroFlightPowerC2SPacket(1, this.);
                this.flightGUIFlicker = true;
            }

            if (this.flightGUIFlicker == true) {
                FlightPowerHud.updateFlightPowerGUI();
                if (FlightPowerHud.getFlightAnimationDrawstate() > 16) {
                    this.flightGUIFlicker = false;
                    FlightPowerHud.FLIGHT_ANIMATION_DRAWSTATE = -1;
                }
            }

        });
    }

    @Override
    public void onPlayerJump(int pJumpPower) {
        this.getCapability(FlightPowerProvider.AMARO_FLIGHT_POWER).ifPresent(amaroFlight -> {
            if (this.isSaddled() && amaroFlight.getFlightPower() > 0) {
                this.flying = true;
                this.isJumping = true;
                //ModPackets.sendToServer(new AmaroFlightPowerC2SPacket(-1));
                amaroFlight.subFlightPower(1);
            }
        });
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
    public boolean canJump() {
        return this.isSaddled();
    }

    @Override
    public void handleStartJump(int pJumpPower) {

    }

    @Override
    public void handleStopJump() {

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

    public boolean isFlying() {
        return this.flying;
    }

    public void setDescend(boolean b) {
        this.descend = b;
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

}
