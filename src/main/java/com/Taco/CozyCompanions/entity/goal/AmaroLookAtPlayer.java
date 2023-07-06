package com.Taco.CozyCompanions.entity.goal;

import com.Taco.CozyCompanions.entity.custom.AmaroEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;


public class AmaroLookAtPlayer extends LookAtPlayerGoal {
    private final AmaroEntity entity;

    public AmaroLookAtPlayer(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance) {
        super(pMob, pLookAtType, pLookDistance);
        this.entity = (AmaroEntity)pMob;
    }

    public AmaroLookAtPlayer(Mob pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance, float pProbability) {
        super(pMob, pLookAtType, pLookDistance, pProbability);
        this.entity = (AmaroEntity)pMob;
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
