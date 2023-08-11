package com.technovision.tribes.util;

import com.technovision.tribes.data.objects.Tribe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageUtils {

    public static void send(Object sender, String line) {
        if ((sender instanceof Player)) {
            ((Player) sender).sendMessage(line);
        } else if (sender instanceof CommandSender) {
            ((CommandSender) sender).sendMessage(line);
        }
    }
    public static void send(Object sender, String[] lines) {
        boolean isPlayer = false;
        if (sender instanceof Player)
            isPlayer = true;

        for (String line : lines) {
            if (isPlayer) {
                ((Player) sender).sendMessage(line);
            } else {
                ((CommandSender) sender).sendMessage(line);
            }
        }
    }

    public static void sendError(Object sender, String line) {
        send(sender, ChatColor.RED+line);
    }

    public static void sendTribe(Tribe tribe, String message) {
        for (UUID id : tribe.getMembers()) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                send(player, message);
            }
        }
    }

    public static void sendSuccess(CommandSender sender, String message) {
        send(sender, ChatColor.GREEN+message);
    }

    public static void sendHeading(CommandSender sender, String title) {
        send(sender, buildTitle(title));
    }

    public static String buildTitle(String title) {
        String line =   "-------------------------------------------------";
        String titleBracket = "[ " + ChatColor.GOLD + title + ChatColor.AQUA + " ]";

        if (titleBracket.length() > line.length()) {
            return ChatColor.AQUA+"-"+titleBracket+"-";
        }

        int min = (line.length() / 2) - titleBracket.length() / 2;
        int max = (line.length() / 2) + titleBracket.length() / 2;

        String out = ChatColor.AQUA + line.substring(0, min);
        out += titleBracket + line.substring(max);

        return out;
    }

    public static String buildSmallTitle(String title) {
        String line = ChatColor.AQUA+"------------------------------";

        String titleBracket = "[ "+title+" ]";

        int min = (line.length() / 2) - titleBracket.length() / 2;
        int max = (line.length() / 2) + titleBracket.length() / 2;

        String out = ChatColor.AQUA + line.substring(0, Math.max(0, min));
        out += titleBracket + line.substring(max);

        return out;
    }

}
