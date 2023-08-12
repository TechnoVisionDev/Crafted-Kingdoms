package com.technovision.craftedkingdoms.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Handles prison ender pearl events
 *
 * @author TechnoVision
 */
public class PearlEvents implements Listener {

    private final Set<UUID> playersToTeleportToEnd = new HashSet<>();

    public PearlEvents() { }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killedPlayer = event.getEntity();
        Player killer = killedPlayer.getKiller();
        if (killer == null) {
            return;
        }
        // Check for ender pearl in hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack item = killer.getInventory().getItem(i);
            if (item != null && item.getType() == Material.ENDER_PEARL) {
                // Use up the pearl
                if (item.getAmount() == 1) {
                    killer.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
                // Flag player for teleporting on respawn
                playersToTeleportToEnd.add(killedPlayer.getUniqueId());
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (playersToTeleportToEnd.contains(playerUUID)) {
            World endWorld = Bukkit.getWorld("world_the_end");
            if (endWorld != null) {
                Location endSpawn = endWorld.getSpawnLocation();
                event.setRespawnLocation(endSpawn);
            } else {
                player.sendMessage("Failed to teleport to the End. Check the world name.");
            }

            // Remove UUID from the set
            playersToTeleportToEnd.remove(playerUUID);
        }
    }
}
