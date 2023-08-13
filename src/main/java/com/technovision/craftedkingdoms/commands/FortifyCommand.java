package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.commands.CommandBase;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Handles fortify commands to protect blocks.
 *
 * @author TechnoVision
 */
public class FortifyCommand extends CommandBase {

    public FortifyCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/fortify";
        displayName = "Fortify";

        // Implemented
        commands.put("enable", "[group] - Automatically fortify blocks you place with the item in your offhand.");

        // Not Yet Implemented
        /**
        commands.put("setgroup", "[group] - Set the default group when fortifying");
        commands.put("disable", "[group] - Blocks you place will no longer be automatically fortified.");
        commands.put("tiers", "List the item tiers for fortifying blocks.");
        commands.put("inspect", "Check if the block you are looking at is fortified.");
        */
    }

    public void enable_cmd() throws CKException {
        // Get group from args
        if (args.length < 2) {
            throw new CKException("You must specify a group name!");
        }
        Group group = CKGlobal.getGroup(args[1]);
        if (group == null) {
            throw new CKException("The group " + ChatColor.YELLOW + args[1] + ChatColor.RED + " doesn't exist!");
        }

        // Check that player has perms to protect land for this group
        Resident senderRes = getResident();
        if (!senderRes.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!senderRes.hasPermission(group, Permissions.BLOCKS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"BLOCKS"+ChatColor.RED+" permission to fortify blocks.");
        }

        // Enable fortify mode
        Player player = getPlayer();
        CKGlobal.addFortifyGroup(player, group.getName());
        MessageUtils.send(player, ChatColor.GRAY+"You have enabled fortify mode for the group "+ChatColor.YELLOW+group.getName()+ChatColor.GRAY+".");
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
