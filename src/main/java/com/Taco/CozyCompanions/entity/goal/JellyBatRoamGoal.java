package com.Taco.CozyCompanions.entity.goal;

import com.Taco.CozyCompanions.entity.custom.JellyBatEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

public class JellyBatRoamGoal extends RandomStrollGoal {
    private final JellyBatEntity entity;

    public JellyBatRoamGoal(JellyBatEntity pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier);
        this.entity = pMob;
    }

    // Check if the entity is upside down, if so, have a 1-100 chance to move and break free of being upsidedown.
    @Override
    public boolean canUse() {
        if (entity.isUpsideDown()) {
            if (!this.entity.roamDelay) {
                this.entity.setUpsideDown(false);
                super.canUse();
            } else {
                return false;
            }
        } else if (this.entity.getNavigation().isDone() && this.entity.getRandom().nextInt(25) == 0) {
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getNavigation().isInProgress() && !this.mob.isVehicle();
    }

    @Override
    public void start() {
        // have a 1/5 chance to navigate on the ground
        if (this.entity.getRandom().nextInt(5) == 0) {
            this.entity.setNavigationModeToFlying(false);
            super.start();
        } else {
            this.entity.setNavigationModeToFlying(true);
            Vec3 vec = this.entity.getViewVector(0.0f);
            Vec3 vecPath = HoverRandomPos.getPos(this.entity, 8, 7, vec.x, vec.z,((float)Math.PI / 10F), 3, 1);

            if (vecPath != null) {
                BlockPos pathBlock = new BlockPos(vecPath);
                this.entity.getNavigation().moveTo(this.entity.getNavigation().createPath(pathBlock, 1),1.0d );
            }
        }


    }

}
