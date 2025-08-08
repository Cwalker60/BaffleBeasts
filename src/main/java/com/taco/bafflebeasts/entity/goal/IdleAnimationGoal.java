package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.IdleAnimatedEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class IdleAnimationGoal extends Goal {
    private final IdleAnimatedEntity entity;
    private final int maxposes;

    /**
     *  IdleAnimationGoal sets an idle pose for a IdleAnimatedEntity when fired if their "IdleTimer" is 0.
     *  It will take in the mob, and the number of idle animations available.
     * @param mob Mob for the goal.
     * @param maxPoses Number of idle animations.
     */
    public IdleAnimationGoal(IdleAnimatedEntity mob, int maxPoses) {
        this.entity = mob;
        this.maxposes = maxPoses;
    }
    // This goal can be used when the amaro is alive, and some time has passed since the last idle.
    @Override
    public boolean canUse() {
        return (entity.isAlive() && entity.getIdleTimer() <= 0);
    }

    // set the idle pose to be a random pose from numbers 1-5.
    // reset the timer to 400 as well.
    @Override
    public void start() {
        super.start();
        int pose = (int)(Math.random() * maxposes) + 1;
        this.entity.setIdlePose(pose);
        this.entity.setIdleTimer(400);
    }
}
