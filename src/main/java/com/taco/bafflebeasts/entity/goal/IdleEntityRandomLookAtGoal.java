package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.entity.custom.IdleAnimatedEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;

public class IdleEntityRandomLookAtGoal extends RandomLookAroundGoal {
    protected IdleAnimatedEntity entity;


    public IdleEntityRandomLookAtGoal(Mob pMob) {
        super(pMob);
        entity = (IdleAnimatedEntity) pMob;
    }

    @Override
    public boolean canUse() {
        if (entity.isAsleep()) {
            return false;
        }
        return super.canUse();
    }


}
