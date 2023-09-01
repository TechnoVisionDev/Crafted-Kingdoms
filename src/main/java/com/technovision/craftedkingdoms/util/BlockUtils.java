package com.technovision.craftedkingdoms.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;

public class BlockUtils {

    /**
     * Get the bottom part of a door.
     *
     * @param block A block that is part of a door (either the top or bottom half).
     * @return The bottom part of the door.
     */
    public static Block getBottomPartOfDoor(Block block) {
        BlockData blockData = block.getBlockData();
        Bisected door = (Bisected) blockData;
        if (door.getHalf() == Bisected.Half.BOTTOM) {
            // If it's already the bottom, just return it
            return block;
        } else {
            // Otherwise, it's the top part, so get the bottom part (one block below)
            return block.getRelative(BlockFace.DOWN);
        }
    }

    /**
     * Get the bottom half of a bed
     *
     * @param block A block that is part of a bed (either the top or bottom half).
     * @return The bottom part of the bed.
     */
    public static Block getBedFoot(Block block) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Bed bed) {
            BlockFace facing = bed.getFacing();
            if (bed.getPart() == Bed.Part.HEAD) {
                return block.getRelative(facing.getOppositeFace());
            }
        }
        return block;
    }

    public static boolean isDoor(Block block) {
        Material type = block.getType();
        return type.name().endsWith("_DOOR");
    }

    public static boolean isBed(Block block) {
        Material type = block.getType();
        return type.name().endsWith("_BED");
    }
}
