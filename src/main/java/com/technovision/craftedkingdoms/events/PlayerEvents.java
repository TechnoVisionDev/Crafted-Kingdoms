package com.technovision.craftedkingdoms.events;

import com.technovision.craftedkingdoms.CKGlobal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerEvents implements Listener {

    /**
     * Creates a resident object for new players.
     * @param event fires during player login.
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        CKGlobal.getResident(player);
    }
}
