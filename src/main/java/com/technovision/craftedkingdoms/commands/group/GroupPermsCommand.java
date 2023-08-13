package com.technovision.craftedkingdoms.commands.group;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.commands.CommandBase;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;

/**
 * Handles group permission commands.
 *
 * @author TechnoVision
 */
public class GroupPermsCommand extends CommandBase {

    public GroupPermsCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/group perms";
        displayName = "Group Permissions";

        commands.put("add", "[group] [rank] [permission] - Add a permission to a group's player rank.");
        commands.put("remove", "[group] [rank] [permission] - Remove a permission from a group's player rank.");

        // Not implemented
        /**
        commands.put("list", "List all permissions.");
        commands.put("inspect", "[group] [rank] - View the permissions a group's rank has.");
        commands.put("reset", "[group] - Resets a group's permissions to default.");
        */
    }

    public void add_cmd() throws CKException {
        Group group = getGroupFromArgs(1);
        Ranks rank = getRankFromArgs(2);
        Permissions perm = getPermFromArgs(3);

        // Check that player has permission to modify perms
        Resident res = getResident();
        if (!res.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!res.hasPermission(group, Permissions.PERMS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"PERMS"+ChatColor.RED+" permission to modify ranks.");
        }

        // Check if rank already has perm
        if (group.getRankPermissions().get(rank.toString()).contains(perm.toString())) {
            throw new CKException("The "+ChatColor.YELLOW+rank.getName()+ChatColor.RED+" rank already has that permission!");
        }

        // Add perm to rank
        group.addPermissionToRank(rank, perm);
        MessageUtils.sendSuccess(getPlayer(), String.format("Added the %s%s%s perm to the %s%s%s rank in %s%s%s.",
                ChatColor.YELLOW,
                perm,
                ChatColor.GREEN,
                ChatColor.YELLOW,
                rank.getName(),
                ChatColor.GREEN,
                ChatColor.YELLOW,
                group.getName(),
                ChatColor.GREEN
            )
        );
    }

    public void remove_cmd() throws CKException {
        Group group = getGroupFromArgs(1);
        Ranks rank = getRankFromArgs(2);
        Permissions perm = getPermFromArgs(3);

        // Check that player has permission to modify perms
        Resident res = getResident();
        if (!res.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!res.hasPermission(group, Permissions.PERMS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"PERMS"+ChatColor.RED+" permission to modify ranks.");
        }

        // Check if rank already has perm
        if (!group.getRankPermissions().get(rank.toString()).contains(perm.toString())) {
            throw new CKException("The "+ChatColor.YELLOW+rank.getName()+ChatColor.RED+" rank does not have that permission!");
        }

        // Add perm to rank
        group.removePermissionFromRank(rank, perm);
        MessageUtils.sendSuccess(getPlayer(), String.format("Removed the %s%s%s perm from the %s%s%s rank in %s%s%s.",
                        ChatColor.YELLOW,
                        perm,
                        ChatColor.GREEN,
                        ChatColor.YELLOW,
                        rank.getName(),
                        ChatColor.GREEN,
                        ChatColor.YELLOW,
                        group.getName(),
                        ChatColor.GREEN
                )
        );
    }

    public Permissions getPermFromArgs(int index) throws CKException {
        if (args.length < index+1) {
            throw new CKException("You must specify a permission!");
        }
        try {
            return Permissions.valueOf(args[index].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CKException("The permission " + ChatColor.YELLOW + args[index] + ChatColor.RED + " doesn't exist!");
        }
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
