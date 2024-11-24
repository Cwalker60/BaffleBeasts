package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.DozeDrakeEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class DozeDrakeSleepGoal extends Goal {
    private DozeDrakeEntity entity;
    private static final int CHANCE = 500;


    public DozeDrakeSleepGoal(DozeDrakeEntity e) {
        entity = e;
    }

    @Override
    public boolean canUse() {
        int random = entity.getRandom().nextInt(CHANCE);
        return (entity.isAlive() && !entity.isAsleep() && random == 0 && !entity.isTame());
    }

    @Override
    public void start() {
        this.entity.setSleep(true);
        this.entity.setIdlePose(0);
        this.entity.setOrderedToSit(true);
    }
}
