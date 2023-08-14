package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
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
        commands.put("enable", "[group] - Enables fortify mode to reinforce blocks.");
        commands.put("disable", "Disables fortify mode.");
        commands.put("inspect", "Toggle inspect mode");

        // Not Yet Implemented
        /**
        commands.put("materials", "Show the materials for fortifying blocks.");
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

    public void disable_cmd() throws CKException {
        Player player = getPlayer();
        CKGlobal.removeFortifyGroup(player);
        MessageUtils.send(player, ChatColor.GRAY+"You have disabled fortify mode.");
    }

    public void inspect_cmd() throws CKException {
        Resident res = getResident();
        res.toggleInspectMode();
        String mode;
        if (res.isInspectMode()) {
            mode = "enabled";
        } else {
            mode = "disabled";
        }
        MessageUtils.send(getPlayer(), ChatColor.GRAY+"You have "+ ChatColor.GREEN + mode + ChatColor.GRAY + " inspect mode for fortified blocks.");
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
