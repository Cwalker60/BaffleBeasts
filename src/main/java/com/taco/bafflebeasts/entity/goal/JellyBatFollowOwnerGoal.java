package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.JellyBatEntity;
import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;

public class JellyBatFollowOwnerGoal extends FlyEntityFollowOwnerGoal {
    private final JellyBatEntity jellybat;

    public JellyBatFollowOwnerGoal(RideableFlightEntity entity, double pSpeedModifier, float pStartDistance, float pStopDistance, boolean pCanFly) {
        super(entity, pSpeedModifier, pStartDistance, pStopDistance, pCanFly);
        this.jellybat = (JellyBatEntity) entity;
    }

    public boolean canUse() {
        // Only follow the owner if the jellybat is supersized.
        if (!jellybat.getSuperSize()) {
            return false;
        }
        return super.canUse();
    }

}
