package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles switching chat groups.
 *
 * @author TechnoVision
 */
public class ChatCommand extends CommandBase {

    public ChatCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/chat";
        displayName = "Chat";

        // Implemented
        commands.put("set", "[group] - Set your group for chat messages.");
    }

    public void set_cmd() throws CKException {
        if (args.length < 2) {
            throw new CKException("You must specify a group name!");
        }

        Resident resident = getResident();
        String groupName = args[1];
        if (groupName.equalsIgnoreCase("global")) {
            resident.setGroupChat("global");
            MessageUtils.send(sender, ChatColor.GREEN + "You are now talking in global chat!");
            return;
        }
        if (groupName.equalsIgnoreCase("local")) {
            resident.setGroupChat("local");
            MessageUtils.send(sender, ChatColor.GREEN + "You are now talking in local chat! (1000 block radius)");
            return;
        }

        Group group = CKGlobal.getGroup(groupName);
        if (group == null) {
            throw new CKException("The group " + ChatColor.YELLOW + groupName + ChatColor.RED + " doesn't exist!");
        }
        if (!group.isResident(resident.getPlayerID())) {
            throw new CKException("You are not a member of that group!");
        }
        resident.setGroupChat(group.getName());
        MessageUtils.send(sender, ChatColor.GREEN + "You are now talking in group chat for " + group.getName());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = super.onTabComplete(sender, cmd, alias, args);
        if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
            Set<String> groupNames = CKGlobal.getResident((Player) sender).getGroups();
            groupNames.add("Global");
            groupNames.add("Local");
            return groupNames.stream().filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
        return completions;
    }

    @Override
    public void doDefaultAction() throws CKException {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() throws CKException {
    }
}
