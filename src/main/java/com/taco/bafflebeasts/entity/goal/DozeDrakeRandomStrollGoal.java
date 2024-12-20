package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class DozeDrakeRandomStrollGoal extends Goal {
    public static final int DEFAULT_INTERVAL = 120;
    protected final DozeDrakeEntity entity;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected final double speedModifier;
    protected int interval;
    protected boolean forceTrigger;
    private final boolean checkNoActionTime;

    public DozeDrakeRandomStrollGoal(DozeDrakeEntity pMob, double pSpeedModifier) {
        this(pMob, pSpeedModifier, 120);
    }

    public DozeDrakeRandomStrollGoal(DozeDrakeEntity pMob, double pSpeedModifier, int pInterval) {
        this(pMob, pSpeedModifier, pInterval, true);
    }

    public DozeDrakeRandomStrollGoal(DozeDrakeEntity pMob, double pSpeedModifier, int pInterval, boolean pCheckNoActionTime) {
        this.entity = pMob;
        this.speedModifier = pSpeedModifier;
        this.interval = pInterval;
        this.checkNoActionTime = pCheckNoActionTime;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {

        if (this.entity.isVehicle()) {
            return false;
        } else {
            if (!this.forceTrigger) {
                if (this.checkNoActionTime && this.entity.getNoActionTime() >= 100) {
                    return false;
                }

                if (this.entity.getRandom().nextInt(reducedTickDelay(this.interval)) != 0) {
                    return false;
                }
            }

            if (this.entity.isOrderedToSit() && this.entity.isAsleep()) {
                return false;
            }



            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3 getPosition() {
        return DefaultRandomPos.getPos(this.entity, 10, 7);
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return !this.entity.getNavigation().isDone() && !this.entity.isVehicle();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.entity.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.entity.getNavigation().stop();
        super.stop();
    }

    /**
     * Makes task to bypass chance
     */
    public void trigger() {
        this.forceTrigger = true;
    }

    /**
     * Changes task random possibility for execution
     */
    public void setInterval(int pNewchance) {
        this.interval = pNewchance;
    }


}
