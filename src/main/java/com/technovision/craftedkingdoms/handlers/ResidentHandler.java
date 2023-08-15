package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CKGlobal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Handles events that affect players and residents.
 *
 * @author TechnoVision
 */
public class ResidentHandler implements Listener {

    /**
     * Creates a resident object for new players.
     * @param event fires during player login.
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        CKGlobal.getResident(player);
    }

    /**
     * Prevents players from taking items from command inventories.
     * @param event Fires when player clicks an inventory.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Fortify Materials")) {
            event.setCancelled(true);
        }
    }
}
