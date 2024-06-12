package com.Taco.CozyCompanions.entity.goal;

import com.Taco.CozyCompanions.CozyCompanions;
import com.Taco.CozyCompanions.entity.custom.JellyBatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * JellyBatUpsideDownGoal is an ai goal that attempts to search for a nearby ceiling block to go upsidedown.
 */
public class JellyBatUpsideDownGoal extends Goal {

    private final JellyBatEntity entity;

    public JellyBatUpsideDownGoal(JellyBatEntity mob) {
        this.entity = mob;
    }

    @Override
    public boolean canUse() {
        if (this.entity.isUpsideDown()) {
            return false;
        } else {
            if (this.entity.getRandom().nextInt(30) == 0 && !this.entity.upsideDownDelay) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return !this.entity.getNavigation().isDone() && !this.entity.isVehicle();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        // Look through at least 8 blockpos ups to see if there is a suitable ceiling to perch.
        // If so, move the jellybat to the ceiling.
        BlockPos targetCeiling = null;

        Iterable<BlockPos> ceilingBlocks = BlockPos.betweenClosed(this.entity.getBlockX(), this.entity.getBlockY(), this.entity.getBlockZ(),
                  this.entity.getBlockX(), this.entity.getBlockY() + 8, this.entity.getBlockZ());
        for (BlockPos blocks: ceilingBlocks) {
            // If a solid block, break the loop and set this to be the block position we want.
            if (this.entity.getLevel().getBlockState(blocks).isRedstoneConductor(this.entity.getLevel(), blocks)) {
                targetCeiling = blocks;
                break;
            }
        }

        // if there is no block found, stop the goal.
        if (targetCeiling == null) {
            this.stop();
        } else {
            CozyCompanions.MAIN_LOGGER.debug("Current ceiling block is : " + targetCeiling.getX() + "," + targetCeiling.getY() + "," + targetCeiling.getZ());
            this.entity.upsideDownBlock = targetCeiling;
            this.entity.getNavigation().moveTo(targetCeiling.getX(), targetCeiling.getY(), targetCeiling.getZ(), 1.0d);
            this.entity.roamDelay = true;
        }

    }

}
