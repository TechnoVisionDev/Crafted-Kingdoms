package com.technovision.craftedkingdoms.handlers;

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
public class PrisonPearlHandler implements Listener {

    private final Set<UUID> pearledPlayers = new HashSet<>();

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
                pearledPlayers.add(killedPlayer.getUniqueId());
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (pearledPlayers.contains(playerUUID)) {
            World nether = Bukkit.getWorld("world_nether");
            if (nether != null) {
                Location netherSpawn = nether.getSpawnLocation();
                event.setRespawnLocation(netherSpawn);
            } else {
                player.sendMessage("Failed to teleport to the Nether. Contact an admin!");
            }

            // Remove UUID from the set
            pearledPlayers.remove(playerUUID);
        }
    }
}
