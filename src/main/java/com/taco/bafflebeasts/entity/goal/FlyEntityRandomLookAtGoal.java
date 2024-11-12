package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class FlyEntityRandomLookAtGoal extends RandomLookAroundGoal {
    protected RideableFlightEntity entity;


    public FlyEntityRandomLookAtGoal(Mob pMob) {
        super(pMob);
        entity = (RideableFlightEntity) pMob;
    }

    @Override
    public boolean canUse() {
        if (entity.isAsleep()) {
            return false;
        }
        return super.canUse();
    }


}
