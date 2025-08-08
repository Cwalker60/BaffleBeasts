package com.taco.bafflebeasts.entity.custom;

import com.taco.bafflebeasts.entity.client.FlightPowerHud;
import com.taco.bafflebeasts.flight.FlightPowerProvider;
import com.taco.bafflebeasts.networking.ModPackets;
import com.taco.bafflebeasts.networking.packet.FlightEntityDashC2SPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;


import java.util.List;

public abstract class RideableFlightEntity extends IdleAnimatedEntity implements Saddleable, PlayerRideable, PlayerRideableJumping {

    public boolean flying = false;
    public boolean isJumping = false;
    public boolean descend = false;
    public boolean elytraFlying = false;
    public boolean flightGUIFlicker = false;

    public int maxFlightRechargeBuffer;
    public int flightRechargeBuffer;
    public int flightPower;

    /**
     * RideableFlightEntities will have the ability to fly with the FlightPower capability.
     * @param pEntityType Entity type of mob
     * @param pLevel Level to create the mob in.
     * @param flightP The amount of jumps (feathers on hud) that the mob can use to fly with.
     * @param flightRecharge The amount of ticks required to restore a jump.
     * @param idleAnimations The amount of idle animations
     * @param sleepVar The threshold to when "sleep" is called when an idle animation is below that value.
     */
    public RideableFlightEntity(EntityType<? extends IdleAnimatedEntity> pEntityType, Level pLevel, int flightP, int flightRecharge, int idleAnimations, int sleepVar) {
        super(pEntityType, pLevel, idleAnimations, sleepVar);
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
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void travel(Vec3 vec3) {
        super.travel(vec3);
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
            if (this.isNoGravity() && !this.isElytraFlying()) {
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

            // Used to tell the client to animate the "glow" effect for the feathers in the hud.
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
