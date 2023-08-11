package com.technovision.civilization.events;

import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class NexusEvents implements Listener {

    /**
     * Prevent placing blocks adjacent to the nexus
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        World world = block.getWorld();
        // Check if a nexus block is adjacent to the block placed
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 0; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    // Skip the center block (dx == 0, dy == 0, dz == 0)
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    Block adjacent = world.getBlockAt(block.getX() + dx, block.getY() + dy, block.getZ() + dz);
                    if (CivGlobal.isNexus(adjacent)) {
                        event.setCancelled(true);
                        CivMessage.sendError(event.getPlayer(), "You cannot place blocks adjacent to the nexus!");
                        return; // Exit method once we know we can't place the block
                    }
                }
            }
        }
    }

    /**
     * Prevent breaking the nexus block (except during wartime)
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Prevent player from breaking nexus block
        Block block = event.getBlock();
        if (CivGlobal.isNexus(block)) {
            event.setCancelled(true);
            String error = String.format("You cannot break the nexus! Use %s/town nexus %sto move it.", CivColor.Yellow, CivColor.Rose);
            CivMessage.sendError(event.getPlayer(), error);
            return;
        }
    }
}
