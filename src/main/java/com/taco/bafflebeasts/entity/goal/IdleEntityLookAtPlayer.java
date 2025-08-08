package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.IdleAnimatedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;

public class IdleEntityLookAtPlayer extends LookAtPlayerGoal {
    private final IdleAnimatedEntity entity;

    public IdleEntityLookAtPlayer(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
        super(pMob, pLookAtType, pLookDistance);
        this.entity = (IdleAnimatedEntity) pMob;
    }

    public IdleEntityLookAtPlayer(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, float pProbability) {
        super(pMob, pLookAtType, pLookDistance, pProbability);
        this.entity = (IdleAnimatedEntity) pMob;
    }
    /**
     Add to the canUse method to allow this look goal to only work when the amaro is not sleeping.
     This stops the random looking when the amaro is sleeping.
     */
    @Override
    public boolean canUse() {
        if (!this.entity.isAsleep() || !this.entity.hasControllingPassenger()) {
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
        }
        return super.canContinueToUse();
    }

}
