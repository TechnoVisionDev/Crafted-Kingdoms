package com.technovision.craftedkingdoms.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

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

    public static boolean isDoor(Block block) {
        Material type = block.getType();
        return type.name().endsWith("_DOOR");
    }
}
