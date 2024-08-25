package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.client.FlightPowerHud;
import com.taco.bafflebeasts.flight.FlightPowerProvider;
import com.taco.bafflebeasts.networking.ModPackets;
import com.taco.bafflebeasts.networking.packet.FlightEntityDashC2SPacket;
import com.taco.bafflebeasts.util.ElytraGlideCalculation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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

    @Override
    public void travel(Vec3 vec3) {
        LivingEntity rider = (LivingEntity) this.getControllingPassenger();
        if (rider != null && this.isVehicle()) {

            this.setYRot(rider.getYRot()); // set the y rotation to the riders rotation
            this.yRotO = this.getYRot();

            AttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
            double gravityValue = gravity.getValue();

            this.setXRot(rider.getXRot()); // set the x rotation to the riders rotation
            this.setRot(this.getYRot(), this.getXRot());

            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            double strafex = rider.xxa * 0.5f;
            double yascend = rider.yya;
            double forwardz = rider.zza;

            Vec3 jvec = this.getDeltaMovement();
            // Add gravity to no gravity. This allows minecraft servers to not kick the player for floating the vehicle.
            // Alternative solution may be to inject code into ServerGamePacketListener to add a check to flying vehicles.
            if (this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -gravityValue / 4.0D, 0.0D));
            }

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
            if (this.isJumping && this.onGround() && !this.isElytraFlying()) {
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

            // When the rider is controlling, set the movement vector
            if (this.isControlledByLocalInstance()) {
                this.setSpeed((float) (this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.1)); // set the speed and multiply it by 20%
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

            if (onGround()) {
                this.flying = false;
                this.isJumping = false;
                this.descend = false;
                this.setNoGravity(false);
                this.setElytraFlying(false);
            }

        } else {
            super.travel(vec3);
        }

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
