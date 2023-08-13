package com.technovision.craftedkingdoms.commands.group;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.commands.CommandBase;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.EffectUtils;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Handles group commands.
 *
 * @author TechnoVision
 */
public class GroupCommand extends CommandBase {

    public GroupCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/group";
        displayName = "Group";

        // Implemented
        commands.put("create", "[name] <type> <password> - Create a new group (defaults to private).");
        commands.put("invite", "[group] [player] - Invite a player to join a group.");
        commands.put("join", "[group] <password> - Join a group that has invited you (or use a password).");
        commands.put("perms", "Manage permissions for player ranks in a group.");

        // Not Yet Implemented
        /**
        commands.put("rescind", "[player] - Cancel a player's invite to your group.");
        commands.put("leave", "[group] - Leave a group that you are currently in.");
        commands.put("delete", "[group] - Delete a group you are currently in.");
        commands.put("invites", "List all groups that have invited you.");
        commands.put("remove", "[group] [player] - Remove a player from your group.");
        commands.put("info", "[group] Display information about a group.");
        commands.put("set", "Set a display name and bio for a group.");
        commands.put("promote", "[group] [player] [rank] - Promote or demote a player to a new rank.");
        commands.put("list", "List all groups that you are currently in.");
        commands.put("link", "[group] [subgroup] - Link two groups together.");
        commands.put("unlink", "[group] [subgroup] - Unlink two groups from each other.");
        commands.put("merge", "[group] [merge-group] - Merges group2 into group1.");
        commands.put("transfer", "[group] [player] - Transfer ownership of a group.");
        */
    }

    public void create_cmd() throws CKException {
        // Get group name from args
        if (args.length < 2) {
            throw new CKException("You must enter a name for your group.");
        }
        String name = StringUtils.toSafeString(args[1], 30, "Your group name");

        // TODO: Add 1 min cooldown to creating groups

        // Check if group name is taken
        Player player = getPlayer();
        if (CKGlobal.isGroup(name)) {
            throw new CKException("A group named "+ChatColor.YELLOW+args[1]+ChatColor.RED+" already exists!");
        }

        // Get group type from args if specified
        boolean isPublic = false;
        if (args.length >= 3) {
            if (args[2].equalsIgnoreCase("public")) {
                isPublic = true;
            } else if (!args[2].equalsIgnoreCase("private")) {
                throw new CKException("The group type specified is invalid. Use 'public' or 'private'.");
            }
        }

        // Get group password from args if specified
        String password = null;
        if (args.length >= 4) {
            if (isPublic) {
                throw new CKException("Only private groups can add a password!");
            }
            password = StringUtils.toSafeString(args[3], 30, "Your group password");
        }

        // Create group locally and in database
        Group group = CKGlobal.createGroup(name, player, isPublic, password);

        // Send success message & firework
        MessageUtils.send(player, " ");
        MessageUtils.sendHeading(player, "You Created a Group!");
        MessageUtils.send(player, createGroupMessage(group));
        EffectUtils.launchfirework(EffectUtils.greenFirework, player.getLocation());
    }

    public void invite_cmd() throws CKException {
        // Get group from args
        Group group = getGroupFromArgs(1);
        if (group.isPublic()) {
            throw new CKException("You can only invite members to a private group!");
        }

        // Check if sender can invite
        Resident senderRes = getResident();
        if (!senderRes.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!senderRes.hasPermission(group, Permissions.MEMBERS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"MEMBERS"+ChatColor.RED+" permission to invite players.");
        }

        // Get resident to invite from args
        if (args.length < 3) {
            throw new CKException("You must specify a player to invite");
        }
        Resident res = getResidentFromArgs(2);
        if (res == null) {
            throw new CKException("The group " + ChatColor.YELLOW + group.getName() + ChatColor.RED + " doesn't exist!");
        }
        if (res.hasInvite(group.getName())) {
            throw new CKException("Your group has already invited that player to join!");
        }

        // Invite player
        res.invite(group.getName());
        MessageUtils.send(getPlayer(), ChatColor.GRAY + "You sent an invite to " + ChatColor.YELLOW + res.getPlayerName() + ChatColor.GRAY + " to join " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + ".");
    }

    public void join_cmd() throws CKException {
        // Get group from args
        Group group = getGroupFromArgs(1);
        Resident res = getResident();
        if (group.isMember(res.getPlayerID())) {
            throw new CKException("You are already a member of that group!");
        }

        // Join with password from args if included
        if (args.length >= 3) {
            String password = args[2];
            if (group.isPublic()) {
                throw new CKException("Only private groups use passwords!");
            }
            if (group.getPassword().equals(password)) {
                MessageUtils.sendGroup(group, String.format("%s%s%s has joined the group %s%s",
                        ChatColor.YELLOW,
                        res.getPlayerName(),
                        ChatColor.GRAY,
                        ChatColor.YELLOW,
                        group.getName()
                ));
                group.addMember(res);
                MessageUtils.sendSuccess(sender, "You have joined the group " + ChatColor.YELLOW + group.getName());
                return;
            }
        }

        // Join with invite
        if (!res.hasInvite(group.getName())) {
            throw new CKException("You have not been invited to join that group!");
        }
        MessageUtils.sendGroup(group, String.format("%s%s%s has joined the group %s%s",
                ChatColor.YELLOW,
                res.getPlayerName(),
                ChatColor.GRAY,
                ChatColor.YELLOW,
                group.getName()
        ));
        group.addMember(res);
        MessageUtils.sendSuccess(sender, "You have joined the group " + ChatColor.YELLOW + group.getName());
    }

    public void perms_cmd() throws CKException {
        GroupPermsCommand cmd = new GroupPermsCommand(plugin);
        cmd.onCommand(sender, null, "perms", this.stripArgs(args, 1));
    }

    private String[] createGroupMessage(Group group) {
        String[] msg = {
                ChatColor.GREEN + "You created a " + ChatColor.YELLOW + (group.isPublic() ? "public" : "private") + ChatColor.GREEN + " group named " + ChatColor.YELLOW + group.getName() + ChatColor.GREEN + ".",
                ChatColor.GREEN + "Players must enter a password to join this group.",
                " ",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group" + ChatColor.GRAY + " to manage your group.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group invite" + ChatColor.GRAY + " to invite other players.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group set" + ChatColor.GRAY + " to set a display name and bio.",
                " "
        };
        // If password is null, compact the array to exclude the empty password line
        if (group.getPassword() == null) {
            msg = new String[] {
                    msg[0], // Group creation message
                    msg[2], // Blank line
                    msg[3], // /group message
                    msg[4], // /group invite message
                    msg[5], // /group set message
                    msg[6]  // Blank line
            };
        }
        return msg;
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
