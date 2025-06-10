package com.taco.bafflebeasts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TailColorCheck {
    public static final Map<Integer, Block> TERRACOTTA_BLOCKS_MAP = new HashMap<Integer, Block>();
    public static final Map<Integer, Block> WOOL_BLOCKS_MAP = new HashMap<Integer, Block>();
    public static final Map<Integer, Block> CONCRETE_MAP = new HashMap<Integer, Block>();
    public static final Map<Integer, Block> CONCRETE_POWDER_MAP = new HashMap<Integer, Block>();
    public static final Map<Integer, Block> GLASS_MAP = new HashMap<Integer, Block>();
    public static final Map<Integer, Block> GLASS_PANE_MAP = new HashMap<Integer, Block>();


    public static void init() {
        // Terracotta Blocks
        TERRACOTTA_BLOCKS_MAP.put(0,Blocks.WHITE_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(1,Blocks.ORANGE_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(2,Blocks.MAGENTA_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(3,Blocks.LIGHT_BLUE_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(4,Blocks.YELLOW_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(5,Blocks.LIME_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(6,Blocks.PINK_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(7,Blocks.GRAY_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(8,Blocks.LIGHT_GRAY_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(9,Blocks.CYAN_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(10,Blocks.PURPLE_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(11,Blocks.BLUE_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(12,Blocks.BROWN_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(13,Blocks.GREEN_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(14,Blocks.RED_TERRACOTTA);
        TERRACOTTA_BLOCKS_MAP.put(15,Blocks.BLACK_TERRACOTTA);

        WOOL_BLOCKS_MAP.put(0,Blocks.WHITE_WOOL);
        WOOL_BLOCKS_MAP.put(1,Blocks.ORANGE_WOOL);
        WOOL_BLOCKS_MAP.put(2,Blocks.MAGENTA_WOOL);
        WOOL_BLOCKS_MAP.put(3,Blocks.LIGHT_BLUE_WOOL);
        WOOL_BLOCKS_MAP.put(4,Blocks.YELLOW_WOOL);
        WOOL_BLOCKS_MAP.put(5,Blocks.LIME_WOOL);
        WOOL_BLOCKS_MAP.put(6,Blocks.PINK_WOOL);
        WOOL_BLOCKS_MAP.put(7,Blocks.GRAY_WOOL);
        WOOL_BLOCKS_MAP.put(8,Blocks.LIGHT_GRAY_WOOL);
        WOOL_BLOCKS_MAP.put(9,Blocks.CYAN_WOOL);
        WOOL_BLOCKS_MAP.put(10,Blocks.PURPLE_WOOL);
        WOOL_BLOCKS_MAP.put(11,Blocks.BLUE_WOOL);
        WOOL_BLOCKS_MAP.put(12,Blocks.BROWN_WOOL);
        WOOL_BLOCKS_MAP.put(13,Blocks.GREEN_WOOL);
        WOOL_BLOCKS_MAP.put(14,Blocks.RED_WOOL);
        WOOL_BLOCKS_MAP.put(15,Blocks.BLACK_WOOL);

        CONCRETE_MAP.put(0,Blocks.WHITE_CONCRETE);
        CONCRETE_MAP.put(1,Blocks.ORANGE_CONCRETE);
        CONCRETE_MAP.put(2,Blocks.MAGENTA_CONCRETE);
        CONCRETE_MAP.put(3,Blocks.LIGHT_BLUE_CONCRETE);
        CONCRETE_MAP.put(4,Blocks.YELLOW_CONCRETE);
        CONCRETE_MAP.put(5,Blocks.LIME_CONCRETE);
        CONCRETE_MAP.put(6,Blocks.PINK_CONCRETE);
        CONCRETE_MAP.put(7,Blocks.GRAY_CONCRETE);
        CONCRETE_MAP.put(8,Blocks.LIGHT_GRAY_CONCRETE);
        CONCRETE_MAP.put(9,Blocks.CYAN_CONCRETE);
        CONCRETE_MAP.put(10,Blocks.PURPLE_CONCRETE);
        CONCRETE_MAP.put(11,Blocks.BLUE_CONCRETE);
        CONCRETE_MAP.put(12,Blocks.BROWN_CONCRETE);
        CONCRETE_MAP.put(13,Blocks.GREEN_CONCRETE);
        CONCRETE_MAP.put(14,Blocks.RED_CONCRETE);
        CONCRETE_MAP.put(15,Blocks.BLACK_CONCRETE);

        CONCRETE_POWDER_MAP.put(0,Blocks.WHITE_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(1,Blocks.ORANGE_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(2,Blocks.MAGENTA_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(3,Blocks.LIGHT_BLUE_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(4,Blocks.YELLOW_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(5,Blocks.LIME_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(6,Blocks.PINK_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(7,Blocks.GRAY_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(8,Blocks.LIGHT_GRAY_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(9,Blocks.CYAN_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(10,Blocks.PURPLE_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(11,Blocks.BLUE_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(12,Blocks.BROWN_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(13,Blocks.GREEN_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(14,Blocks.RED_CONCRETE_POWDER);
        CONCRETE_POWDER_MAP.put(15,Blocks.BLACK_CONCRETE_POWDER);

        GLASS_MAP.put(0,Blocks.WHITE_STAINED_GLASS);
        GLASS_MAP.put(1,Blocks.ORANGE_STAINED_GLASS);
        GLASS_MAP.put(2,Blocks.MAGENTA_STAINED_GLASS);
        GLASS_MAP.put(3,Blocks.LIGHT_BLUE_STAINED_GLASS);
        GLASS_MAP.put(4,Blocks.YELLOW_STAINED_GLASS);
        GLASS_MAP.put(5,Blocks.LIME_STAINED_GLASS);
        GLASS_MAP.put(6,Blocks.PINK_STAINED_GLASS);
        GLASS_MAP.put(7,Blocks.GRAY_STAINED_GLASS);
        GLASS_MAP.put(8,Blocks.LIGHT_GRAY_STAINED_GLASS);
        GLASS_MAP.put(9,Blocks.CYAN_STAINED_GLASS);
        GLASS_MAP.put(10,Blocks.PURPLE_STAINED_GLASS);
        GLASS_MAP.put(11,Blocks.BLUE_STAINED_GLASS);
        GLASS_MAP.put(12,Blocks.BROWN_STAINED_GLASS);
        GLASS_MAP.put(13,Blocks.GREEN_STAINED_GLASS);
        GLASS_MAP.put(14,Blocks.RED_STAINED_GLASS);
        GLASS_MAP.put(15,Blocks.BLACK_STAINED_GLASS);

        GLASS_PANE_MAP.put(0,Blocks.WHITE_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(1,Blocks.ORANGE_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(2,Blocks.MAGENTA_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(3,Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(4,Blocks.YELLOW_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(5,Blocks.LIME_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(6,Blocks.PINK_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(7,Blocks.GRAY_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(8,Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(9,Blocks.CYAN_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(10,Blocks.PURPLE_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(11,Blocks.BLUE_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(12,Blocks.BROWN_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(13,Blocks.GREEN_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(14,Blocks.RED_STAINED_GLASS_PANE);
        GLASS_PANE_MAP.put(15,Blocks.BLACK_STAINED_GLASS_PANE);

    }



    public static void setColorBlock(int color, BlockPos pBlockPos, Level pLevel) {
        // If the block is a terracotta block, set it to the color of the tail.
        if (!pLevel.isClientSide()) {
            BlockState targetBlock = pLevel.getBlockState(pBlockPos);
            // Terracotta
            if (targetBlock.is(Blocks.TERRACOTTA) || TERRACOTTA_BLOCKS_MAP.containsValue(targetBlock.getBlock())) {
                // Only break the blocks that are not current color
                if (!targetBlock.is(TERRACOTTA_BLOCKS_MAP.get(color))) {
                    pLevel.destroyBlock(pBlockPos, false);
                    pLevel.setBlock(pBlockPos, TERRACOTTA_BLOCKS_MAP.get(color).defaultBlockState(),1);
                }
            }
            // Wool
            if (WOOL_BLOCKS_MAP.containsValue(targetBlock.getBlock())) {
                // Only break the blocks that are not current color
                if (!targetBlock.is(WOOL_BLOCKS_MAP.get(color))) {
                    pLevel.destroyBlock(pBlockPos, false);
                    pLevel.setBlock(pBlockPos, WOOL_BLOCKS_MAP.get(color).defaultBlockState(),1);
                }
            }
            // Concrete
            if (CONCRETE_MAP.containsValue(targetBlock.getBlock())) {
                // Only break the blocks that are not current color
                if (!targetBlock.is(CONCRETE_MAP.get(color))) {
                    pLevel.destroyBlock(pBlockPos, false);
                    pLevel.setBlock(pBlockPos, CONCRETE_MAP.get(color).defaultBlockState(),1);
                }
            }
            // Concrete Powder
            if (CONCRETE_POWDER_MAP.containsValue(targetBlock.getBlock())) {
                // Only break the blocks that are not current color
                if (!targetBlock.is(CONCRETE_POWDER_MAP.get(color))) {
                    pLevel.destroyBlock(pBlockPos, false);
                    pLevel.setBlock(pBlockPos, CONCRETE_POWDER_MAP.get(color).defaultBlockState(),1);
                }
            }
            // Glass
            if (targetBlock.is(Blocks.GLASS) || GLASS_MAP.containsValue(targetBlock.getBlock())) {
                // Only break the blocks that are not current color
                if (!targetBlock.is(GLASS_MAP.get(color))) {
                    pLevel.destroyBlock(pBlockPos, false);
                    pLevel.setBlock(pBlockPos, GLASS_MAP.get(color).defaultBlockState(),1);
                }
            }
            // Glass Panes
            if (targetBlock.is(Blocks.GLASS_PANE) ||GLASS_PANE_MAP.containsValue(targetBlock.getBlock())) {
                // Only break the blocks that are not current color
                if (!targetBlock.is(GLASS_PANE_MAP.get(color))) {
                    pLevel.destroyBlock(pBlockPos, false);
                    pLevel.setBlock(pBlockPos, GLASS_PANE_MAP.get(color).defaultBlockState(),1);
                }
            }
        }
    }

    /**
     * setPaintedSquare will paint a 5x5 square based on the originPoint it is given.
     * @param orginPoint
     * @param pDirection
     * @param pLevel
     * @return
     */
    public static void setPaintedSquare(BlockPos orginPoint, Direction pDirection, Level pLevel, int pColor) {
        BlockPos corner1;
        BlockPos corner2;

        // Get blocks if the player is looking at a ceiling/floor
        if (pDirection.equals(Direction.UP) || (pDirection.equals(Direction.DOWN))) {
            corner1 = orginPoint.north(2).west(2);
            corner2 = orginPoint.south(2).east(2);
            // Iterate through each block and set the painted block.
            Stream<BlockPos> blockStream = BlockPos.betweenClosedStream(corner1, corner2);
            blockStream.forEach(blocks -> {
                setColorBlock(pColor, blocks, pLevel);
            });

        } else {
            corner1 = orginPoint.relative(pDirection.getClockWise(), 2).above(2);
            corner2 = orginPoint.relative(pDirection.getClockWise(), -2).below(2);

            Stream<BlockPos> blockStream = BlockPos.betweenClosedStream(corner1, corner2);
            blockStream.forEach(blocks -> {
                setColorBlock(pColor, blocks, pLevel);
            });
        }

    }




}
