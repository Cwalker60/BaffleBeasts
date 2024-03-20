package com.Taco.CozyCompanions.entity.goal;

import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class AmaroIdleGoal extends Goal {
    private final AmaroEntity entity;
    public AmaroIdleGoal(AmaroEntity mob) {
        this.entity = mob;
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
        int pose = (int)(Math.random() * 5) + 1;
        this.entity.setIdlePose(pose);
        this.entity.setIdleTimer(400);
    }



}
