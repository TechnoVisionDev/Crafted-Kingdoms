package com.technovision.civilization.events;

import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.queries.chat.ChatQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class ResidentEvents implements Listener {

    public ResidentEvents() { }

    /**
     * Creates a resident object for new players.
     * @param event fires during player login.
     */
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        CivGlobal.getResident(player);
    }

    /**
     * Handles any interactive chat queries.
     * @param event fires when player sends a chat message.
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ChatQuery chatQuery = ChatQuery.getQuery(player);
        if (chatQuery != null) {
            if (chatQuery.handleResponse(event.getMessage().strip())) {
                chatQuery.cancel();
            }
            event.setCancelled(true);
        }
    }
}
