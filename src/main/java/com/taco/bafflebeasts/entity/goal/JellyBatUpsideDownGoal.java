package com.taco.bafflebeasts.entity.goal;

import com.taco.bafflebeasts.BaffleBeasts;
import com.taco.bafflebeasts.entity.custom.JellyBatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

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

        int xBlock = this.entity.getBlockX() + (int)this.entity.getBbHeight();
        int yBlock = this.entity.getBlockY() + (int)this.entity.getBbHeight();
        int zBlock = this.entity.getBlockZ() + (int)this.entity.getBbHeight();

        Iterable<BlockPos> ceilingBlocks = BlockPos.betweenClosed(xBlock, yBlock, zBlock,
                xBlock, yBlock + 8, zBlock);
        for (BlockPos blocks: ceilingBlocks) {
            // If a solid block, break the loop and set this to be the block position we want.
            if (this.entity.level().getBlockState(blocks).isRedstoneConductor(this.entity.level(), blocks)) {
                targetCeiling = blocks;
                if (this.entity.getSuperSize()) {
                     if (checkAroundBlockPos(targetCeiling) == true) {
                         break;
                     } else {
                         targetCeiling = null;
                     }
                }

                break;
            }
        }

        // if there is no block found, stop the goal.
        if (targetCeiling == null) {
            this.stop();
        } else {
            //BaffleBeasts.MAIN_LOGGER.debug("Current ceiling block is : " + targetCeiling.getX() + "," + targetCeiling.getY() + "," + targetCeiling.getZ());
            this.entity.upsideDownBlock = targetCeiling;
            this.entity.getNavigation().moveTo(targetCeiling.getX(), targetCeiling.getY(), targetCeiling.getZ(), 1.0d);
            this.entity.roamDelay = true;
        }

    }

    public boolean checkAroundBlockPos(BlockPos ceiling) {
        BlockPos below = ceiling.below();
        // north check
        if (!this.entity.level().getBlockState(below.north()).is(Blocks.AIR)) {
            return false;
        }
        // east check
        if (!this.entity.level().getBlockState(below.east()).is(Blocks.AIR)) {
            return false;
        }
        // south check
        if (!this.entity.level().getBlockState(below.south()).is(Blocks.AIR)) {
            return false;
        }
        // west check
        if (!this.entity.level().getBlockState(below.west()).is(Blocks.AIR)) {
            return false;
        }
        return true;
    }

}
