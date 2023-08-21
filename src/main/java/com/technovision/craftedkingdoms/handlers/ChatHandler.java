package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Handles events for group chats.
 *
 * @author TechnoVision
 */
public class ChatHandler implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Resident resident = CKGlobal.getResident(event.getPlayer().getUniqueId());
        String groupChat = resident.getGroupChat();

        if (groupChat.equalsIgnoreCase("global")) {
            // Send in global chat
            event.setFormat(formatForGlobal(event.getPlayer().getDisplayName(), event.getMessage()));
            return;
        }
        if (groupChat.equalsIgnoreCase("local")) {
            // Send in local chat (1000 block radius
            event.setCancelled(true);
            Location senderLocation = event.getPlayer().getLocation();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                // If the player is within 1000 blocks of the sender
                if (senderLocation.distance(onlinePlayer.getLocation()) <= 1000.0) {
                    onlinePlayer.sendMessage(event.getFormat()
                            .replace("%1$s", event.getPlayer().getDisplayName())
                            .replace("%2$s", event.getMessage())
                    );
                }
            }
            return;
        }
        // Check if group chat is set
        Group group = CKGlobal.getGroup(groupChat);
        if (group == null || !group.isResident(resident.getPlayerID())) {
            resident.setGroupChat("global");
            event.setFormat(formatForGlobal(event.getPlayer().getDisplayName(), event.getMessage()));
        } else {
            // Send to group
            event.setCancelled(true);
            MessageUtils.sendGroup(group, event.getPlayer().getDisplayName(), event.getMessage());
        }
    }

    private String formatForGlobal(String playerName, String message) {
        String prefix = ChatColor.GRAY + "[!] ";
        String newName = prefix + playerName;
        return String.format("%1$s: %2$s", newName, ChatColor.WHITE + message);
    }

}
