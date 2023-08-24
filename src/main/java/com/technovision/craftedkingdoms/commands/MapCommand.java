package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to get link in-game for the dynmap page.
 *
 * @author TechnoVision
 */
public class MapCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        MessageUtils.send(sender, ChatColor.GREEN + "Dynmap: " + ChatColor.YELLOW + "http://map.craftedkingdoms.com/");
        return true;
    }
}
