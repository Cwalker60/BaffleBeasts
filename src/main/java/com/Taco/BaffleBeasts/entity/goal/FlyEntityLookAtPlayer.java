package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.RideableFlightEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class FlyEntityLookAtPlayer extends LookAtPlayerGoal {
    private final RideableFlightEntity entity;

    public FlyEntityLookAtPlayer(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
        super(pMob, pLookAtType, pLookDistance);
        this.entity = (RideableFlightEntity)pMob;
    }

    public FlyEntityLookAtPlayer(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, float pProbability) {
        super(pMob, pLookAtType, pLookDistance, pProbability);
        this.entity = (RideableFlightEntity)pMob;
    }
    /**
     Add to the canUse method to allow this look goal to only work when the amaro is not sleeping.
     This stops the random looking when the amaro is sleeping.
     */
    public boolean canUse() {
        if (!this.entity.isAsleep()) {
            super.canUse();
        }
        return this.lookAt != null;
    }
    /**
     Check to see if the entity is asleep. If the entity is asleep, stop the goal.
     Else, do the default ContinueToUse.
     */
    public boolean canContinueToUse() {
        if (this.entity.isAsleep()) {
            return false;
        } else {
            super.canContinueToUse();
        }
        return true;
    }


}
