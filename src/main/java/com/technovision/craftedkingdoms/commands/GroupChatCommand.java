package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupChatCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            MessageUtils.sendError(sender, "You must specify a group name!");
            return true;
        }

        Resident resident = getResident(sender);
        String groupName = args[0];
        if (groupName.equalsIgnoreCase("global")) {
            resident.setGroupChat("global");
            MessageUtils.send(sender, ChatColor.GREEN + "You are now talking in global chat!");
            return true;
        }
        if (groupName.equalsIgnoreCase("local")) {
            resident.setGroupChat("local");
            MessageUtils.send(sender, ChatColor.GREEN + "You are now talking in local chat! (1000 block radius)");
            return true;
        }

        Group group = CKGlobal.getGroup(groupName);
        if (group == null) {
            MessageUtils.sendError(sender,"The group " + ChatColor.YELLOW + groupName + ChatColor.RED + " doesn't exist!");
            return true;
        }
        if (!group.isResident(resident.getPlayerID())) {
            MessageUtils.sendError(sender,"You are not a member of that group!");
            return true;
        }
        resident.setGroupChat(group.getName());
        MessageUtils.send(sender, ChatColor.GREEN + "You are now talking in group chat for " + group.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            Set<String> groupNames = CKGlobal.getResident((Player) sender).getGroups();
            groupNames.add("Global");
            groupNames.add("Local");
            return groupNames.stream().filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return completions;
    }

    public Resident getResident(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return null;
        }
        return CKGlobal.getResident(player);
    }
}
