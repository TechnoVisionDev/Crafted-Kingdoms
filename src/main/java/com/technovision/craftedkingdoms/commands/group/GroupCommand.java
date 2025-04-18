package com.technovision.craftedkingdoms.commands.group;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.commands.CommandBase;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.EffectUtils;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Handles group commands.
 *
 * @author TechnoVision
 */
public class GroupCommand extends CommandBase {

    private static final List<String> PERMISSIONS = Arrays.stream(Permissions.values()).map(Permissions::name).toList();

    public static final HashMap<UUID, Long> cooldown = new HashMap<>();

    public GroupCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/group";
        displayName = "Group";

        // Implemented
        commands.put("create", "<name> [type] [password] - Create a new group (defaults to private).");
        commands.put("invite", "<group> <player> - Invite a player to join a group.");
        commands.put("rescind", "<group> <player> - Cancel a player's invite to your group.");
        commands.put("join", "<group> [password] - Join a group that has invited you (or use a password).");
        commands.put("leave", "<group> - Leave a group that you are currently in.");
        commands.put("list", "[player] - List all groups that you or another player are in.");
        commands.put("invites", "List all groups that have invited you.");
        commands.put("remove", "<group> <player> - Remove a player from your group.");
        commands.put("perms", "Manage permissions for player ranks in a group.");
        commands.put("bio", "<group> <text> - Set a biography for a group (100 chars max).");
        commands.put("delete", "<group> - Delete a group you are currently in.");
        commands.put("info", "<group> - Display information about a group.");
        commands.put("promote", "<group> <player> <rank> - Promote or demote a player to a new rank.");
        commands.put("transfer", "<group> <player> - Transfer ownership of a group.");

        // Not Yet Implemented
        /**
        commands.put("link", "[super-group] [sub-group] - Link two groups together.");
        commands.put("unlink", "[super-group] [sub-group] - Unlink two groups from each other.");
        commands.put("merge", "[group] [merge-group] - Merges the first group into the second group.");
        */
    }

    public void transfer_cmd() throws CKException {
        // Get group from args
        Group group = getGroupFromArgs(1);

        // Check if sender is owner
        Resident senderRes = getResident();
        Player player = getPlayer();
        if (!player.isOp()) {
            if (!senderRes.isInGroup(group.getName())) {
                throw new CKException("You are not a member of that group!");
            }
            if (!group.isOwner(senderRes.getPlayerID())) {
                throw new CKException("You must be the group owner to transfer ownership!");
            }
        }

        // Get resident to transfer to from args
        Resident newOwnerRes = getResidentFromArgs(2);
        if (!newOwnerRes.isInGroup(group.getName())) {
            throw new CKException("You can't transfer ownership to someone outside the group!");
        }

        // Transfer ownership to new player
        group.transferOwnership(newOwnerRes.getPlayerID());
        Player newOwner = Bukkit.getPlayer(newOwnerRes.getPlayerID());
        if (newOwner != null) {
            MessageUtils.send(newOwner, String.format("%sYou have become the new owner of %s%s",
                    ChatColor.GRAY, ChatColor.YELLOW,
                    group.getName()
            ));
        }
        MessageUtils.send(sender, String.format("%sYou have transferred ownership of %s%s%s to %s%s",
                ChatColor.GRAY, ChatColor.YELLOW,
                group.getName(),
                ChatColor.GRAY, ChatColor.YELLOW,
                newOwnerRes.getPlayerName()
        ));
    }

    public void create_cmd() throws CKException {
        // Get group name from args
        if (args.length < 2) {
            throw new CKException("You must enter a name for your group.");
        }
        String name = StringUtils.toSafeString(args[1], 30, "Your group name");

        // 1 min cooldown for creating groups
        Player player = getPlayer();
        Long time = cooldown.get(player.getUniqueId());
        if (time != null && System.currentTimeMillis() - time < 1000 * 60) {
            MessageUtils.sendError(player, "You must wait 1 minute to create a new group!");
            return;
        }

        // Check if group name is taken
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
        cooldown.put(player.getUniqueId(), System.currentTimeMillis());

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
        Resident res = getResidentFromArgs(2);
        if (res.hasInvite(group.getName())) {
            throw new CKException("Your group has already invited that player to join!");
        }
        if (res.isInGroup(group.getName())) {
            throw new CKException("That player is already in your group!");
        }

        // Invite player
        res.invite(group.getName());
        Player invitedPlayer = Bukkit.getPlayer(res.getPlayerID());
        if (invitedPlayer != null) {
            MessageUtils.send(invitedPlayer, ChatColor.GRAY + "You received an invite to join the group " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + ".");
            MessageUtils.send(invitedPlayer, ChatColor.GRAY + "Use the " + ChatColor.YELLOW + "/group join" + ChatColor.GRAY + " command to join!");
        }
        MessageUtils.send(getPlayer(), ChatColor.GRAY + "You sent an invite to " + ChatColor.YELLOW + res.getPlayerName() + ChatColor.GRAY + " to join " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + ".");
    }

    public void rescind_cmd() throws CKException {
        // Get group from args
        Group group = getGroupFromArgs(1);
        if (group.isPublic()) {
            throw new CKException("That group is public and thus does not use invites!");
        }

        // Check if sender can rescind invites
        Resident senderRes = getResident();
        if (!senderRes.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!senderRes.hasPermission(group, Permissions.MEMBERS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"MEMBERS"+ChatColor.RED+" permission to rescind invites.");
        }

        // Get resident to rescind invite from args
        if (args.length < 3) {
            throw new CKException("You must specify a player!");
        }
        Resident res = getResidentFromArgs(2);
        if (res.isInGroup(group.getName())) {
            throw new CKException("That player is already in your group!");
        }
        if (!res.hasInvite(group.getName())) {
            throw new CKException("That player doesn't have an invite!");
        }
        res.uninvite(group.getName());

        // Send messages
        Player player = Bukkit.getPlayer(res.getPlayerID());
        if (player != null) {
            MessageUtils.send(player, ChatColor.YELLOW + group.getName() + ChatColor.GRAY + " has rescinded their invite to you.");
        }
        MessageUtils.send(getPlayer(), ChatColor.GRAY + "You rescinded an invite to " + ChatColor.YELLOW + res.getPlayerName() + ChatColor.GRAY + " from " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + ".");
    }

    public void join_cmd() throws CKException {
        Group group = getGroupFromArgs(1);
        Resident res = getResident();
        if (group.isResident(res.getPlayerID())) {
            throw new CKException("You are already a member of that group!");
        }

        // Check for password-protected groups
        if (group.getPassword() != null) {
            // If password provided, check its correctness
            if (args.length >= 3 && group.getPassword().equals(args[2])) {
                joinGroup(group, res);
                return;
            }
            // If password not provided, but user has an invite
            else if (res.hasInvite(group.getName())) {
                joinGroup(group, res);
                return;
            }
            // If neither correct password nor invite
            else {
                throw new CKException("The password you entered is not correct or you haven't been invited!");
            }
        }

        // For non-password protected groups: Check if public or has invite
        if (group.isPublic() || res.hasInvite(group.getName())) {
            joinGroup(group, res);
            return;
        }
        throw new CKException("You have not been invited to join that group!");
    }

    public void leave_cmd() throws CKException {
        // Get resident and group
        Group group = getGroupFromArgs(1);
        Resident res = getResident();
        if (!group.isResident(res.getPlayerID())) {
            throw new CKException("You are not a member of that group!");
        }
        if (group.isOwner(res.getPlayerID())) {
            throw new CKException("As the owner you must transfer ownership or delete the group to leave.");
        }

        // Remove player and send messages
        group.removeMember(res);
        MessageUtils.send(getPlayer(), ChatColor.GRAY + "You have left the group " + ChatColor.YELLOW + group.getName());
        MessageUtils.sendGroup(group, ChatColor.YELLOW + res.getPlayerName() + ChatColor.GRAY+" has left the group " + ChatColor.YELLOW + group.getName());
    }

    public void remove_cmd() throws CKException {
        // Get data from args and check if valid
        Group group = getGroupFromArgs(1);
        Resident resToRemove = getResidentFromArgs(2);
        Resident senderRes = getResident();
        if (!senderRes.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!resToRemove.isInGroup(group.getName())) {
            throw new CKException("That player is not in your group!");
        }
        if (senderRes.getPlayerID().equals(resToRemove.getPlayerID())) {
            throw new CKException("You can't remove yourself! Use the /group leave command instead.");
        }

        // Check if sender has perms to remove
        UUID id = resToRemove.getPlayerID();
        if (group.isOwner(id)) {
            throw new CKException("You cannot remove the owner of a group!");
        }
        if (group.isAdmin(id) && !senderRes.hasPermission(group, Permissions.ADMINS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"ADMINS"+ChatColor.RED+" permission to remove admins.");
        }
        if (group.isModerator(id) && !senderRes.hasPermission(group, Permissions.MODS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"MODS"+ChatColor.RED+" permission to remove moderators.");
        }
        if (group.isAdmin(id) && !senderRes.hasPermission(group, Permissions.MEMBERS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"MEMBERS"+ChatColor.RED+" permission to remove members.");
        }

        // Remove player from group and send messages
        group.removeMember(resToRemove);
        MessageUtils.sendGroup(group, ChatColor.YELLOW + resToRemove.getPlayerName() + ChatColor.GRAY+" has been removed from " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + " by " + ChatColor.YELLOW + getPlayer().getName());
        MessageUtils.send(resToRemove, "You have been removed from " + ChatColor.YELLOW + group.getName() + ChatColor.GRAY + " by " + ChatColor.YELLOW + getPlayer().getName());
    }

    public void list_cmd() throws CKException {
        // Get resident (from args if specified)
        Resident res;
        boolean isMe;
        if (args.length >= 2) {
            String name = args[1].toLowerCase();
            name = name.replace("%", "(\\w*)");
            res = CKGlobal.getResident(name);
            if (res == null) {
                throw new CKException("The player you specified doesn't exist.");
            }
            isMe = false;
        } else {
            res = getResident();
            isMe = true;
        }

        // Get list of valid groups
        List<String> groupList = new ArrayList<>();
        for (String name : res.getGroups()) {
            Group group = CKGlobal.getGroup(name);
            if (group != null) {
                String groupString = String.format("%s%s%s - %s",
                        ChatColor.YELLOW,
                        group.getName(),
                        ChatColor.GRAY,
                        getRankAsString(group, res.getPlayerID()),
                        ChatColor.YELLOW
                );
                groupList.add(groupString);
            }
        }
        if (groupList.isEmpty()) {
            MessageUtils.send(sender, ChatColor.GRAY+"You are not in any groups!");
            return;
        }

        // Send groups to player
        if (isMe) { MessageUtils.sendHeading(sender, "Your Groups"); }
        else { MessageUtils.sendHeading(sender, res.getPlayerName() + "'s Groups"); }
        MessageUtils.send(sender, groupList.toArray(new String[0]));
    }

    public void invites_cmd() throws CKException {
        Resident res = getResident();

        // Get list of valid invites
        List<String> groupList = new ArrayList<>();
        for (String name : res.getInvites()) {
            Group group = CKGlobal.getGroup(name);
            if (group != null) {
                String groupString = String.format("%s%s%s",
                        ChatColor.YELLOW,
                        group.getName(),
                        ChatColor.GRAY,
                        getRankAsString(group, res.getPlayerID()),
                        ChatColor.YELLOW
                );
                groupList.add(groupString);
            }
        }
        if (groupList.isEmpty()) {
            MessageUtils.send(sender, ChatColor.GRAY+"You do not have any group invites yet!");
            return;
        }

        // Send invites to player
        Player player = getPlayer();
        MessageUtils.sendHeading(player, "Your Invites");
        MessageUtils.send(player, groupList.toArray(new String[0]));
    }

    public void perms_cmd() {
        GroupPermsCommand cmd = new GroupPermsCommand(plugin);
        cmd.onCommand(sender, null, "perms", this.stripArgs(args, 1));
    }

    public void delete_cmd() throws CKException {
        // Get data from args
        Group group = getGroupFromArgs(1);
        Resident res = getResident();
        if (!group.isOwner(res.getPlayerID())) {
            throw new CKException("You must be the owner to delete a group!");
        }

        // Delete group and send message
        String groupName = group.getName();
        group.delete();
        MessageUtils.send(sender, ChatColor.GRAY + "You have deleted the group " + ChatColor.YELLOW + groupName);
    }

    public void bio_cmd() throws CKException {
        // Get data from args
        Group group = getGroupFromArgs(1);
        if (args.length <= 2) {
            throw new CKException("You must enter some text for your group's bio!");
        }
        String text = combineArgs(Arrays.copyOfRange(args, 2, args.length));
        if (text.length() > 100) {
            throw new CKException("Your group's bio cannot be greater than 100 characters!");
        }

        // Set bio and send message
        group.addBiography(text);
        MessageUtils.send(sender, ChatColor.GREEN + "Successfully set bio for group " + ChatColor.YELLOW + group.getName());
    }

    public void info_cmd() throws CKException {
        // Get group and player data
        Group group = getGroupFromArgs(1);
        List<String> admins = new ArrayList<>();
        for (UUID id : group.getAdmins()) {
            admins.add(CKGlobal.getResident(id).getPlayerName());
        }
        List<String> moderators = new ArrayList<>();
        for (UUID id : group.getModerators()) {
            moderators.add(CKGlobal.getResident(id).getPlayerName());
        }
        List<String> members = new ArrayList<>();
        for (UUID id : group.getMembers()) {
            members.add(CKGlobal.getResident(id).getPlayerName());
        }

        // Create message
        List<String> msgList = new ArrayList<>();
        if (group.getBiography() != null) {
            msgList.add(ChatColor.GRAY + group.getBiography());
        }
        // Add Owner
        msgList.add("");
        msgList.add("" + ChatColor.AQUA + ChatColor.BOLD + "Owner: " + ChatColor.GRAY + CKGlobal.getResident(group.getOwnerID()).getPlayerName());
        // Add Admins
        msgList.add("" + ChatColor.AQUA + ChatColor.BOLD + "Admins: " + ChatColor.GRAY + (admins.isEmpty() ? "None" : String.join(", ", admins)));
        // Add Moderators
        msgList.add("" + ChatColor.AQUA + ChatColor.BOLD + "Moderators: " + ChatColor.GRAY + (moderators.isEmpty() ? "None" : String.join(", ", moderators)));
        // Add Members
        msgList.add("" + ChatColor.AQUA + ChatColor.BOLD + "Members: " + ChatColor.GRAY + (members.isEmpty() ? "None" : String.join(", ", members)));
        String[] msg = msgList.toArray(new String[0]);

        // Send message
        MessageUtils.sendHeading(sender, group.getName());
        MessageUtils.send(sender, msg);
    }

    public void promote_cmd() throws CKException {
        // Get data from args and check if valid
        Group group = getGroupFromArgs(1);
        Resident restoPromote = getResidentFromArgs(2);
        Ranks rank = getRankFromArgs(3);
        Resident senderRes = getResident();
        if (!senderRes.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!restoPromote.isInGroup(group.getName())) {
            throw new CKException("That player is not in your group!");
        }
        if (senderRes.getPlayerID().equals(restoPromote.getPlayerID())) {
            throw new CKException("You can't promote yourself!");
        }

        // Check if sender has perms to promote
        UUID id = restoPromote.getPlayerID();
        if (group.isOwner(id)) {
            throw new CKException("You cannot promote the owner of a group!");
        }

        // Check permissions and current ranks based on the target rank
        Ranks currentRank;
        if (group.isMember(id)) currentRank = Ranks.MEMBER;
        else if (group.isModerator(id)) currentRank = Ranks.MODERATOR;
        else if (group.isAdmin(id)) currentRank = Ranks.ADMIN;
        else throw new CKException("Invalid current rank for the player.");

        if (currentRank == rank) {
            throw new CKException("That player already has that rank!");
        }

        switch (rank) {
            case MEMBER -> {
                if (senderRes.hasPermission(group, Permissions.ADMINS)) {
                    // Demotion allowed
                    group.promote(id, currentRank, rank);
                } else {
                    throw new CKException("You need the " + ChatColor.YELLOW + "ADMINS" + ChatColor.RED + " permission to demote to member rank.");
                }
            }
            case MODERATOR -> {
                if (currentRank == Ranks.MEMBER && (senderRes.hasPermission(group, Permissions.MODS) || senderRes.hasPermission(group, Permissions.ADMINS))) {
                    // Promotion allowed
                    group.promote(id, currentRank, rank);
                } else if (currentRank == Ranks.ADMIN && senderRes.hasPermission(group, Permissions.ADMINS)) {
                    // Demotion allowed
                    group.promote(id, currentRank, rank);
                } else {
                    throw new CKException("You need appropriate permissions to promote or demote to moderator rank.");
                }
            }
            case ADMIN -> {
                if (senderRes.hasPermission(group, Permissions.ADMINS)) {
                    // Promotion allowed
                    group.promote(id, currentRank, rank);
                } else {
                    throw new CKException("You need the " + ChatColor.YELLOW + "ADMINS" + ChatColor.RED + " permission to promote to admin rank.");
                }
            }
            default -> throw new CKException("Invalid target rank specified.");
        }

        // Send messages
        Player promotedPlayer = Bukkit.getPlayer(restoPromote.getPlayerID());
        if (promotedPlayer != null) {
            MessageUtils.send(promotedPlayer, String.format("%sYour rank in %s%s%s changed from %s%s%s to %s%s",
                    ChatColor.GRAY, ChatColor.YELLOW,
                    group.getName(),
                    ChatColor.GRAY, ChatColor.YELLOW,
                    currentRank.getName(),
                    ChatColor.GRAY, ChatColor.YELLOW,
                    rank.getName()
            ));
        }

        MessageUtils.send(sender, String.format("%sYou changed %s%s%s rank in %s%s%s from %s%s%s to %s%s",
                ChatColor.GRAY, ChatColor.YELLOW,
                restoPromote.getPlayerName(),
                ChatColor.GRAY, ChatColor.YELLOW,
                group.getName(),
                ChatColor.GRAY, ChatColor.YELLOW,
                currentRank.getName(),
                ChatColor.GRAY, ChatColor.YELLOW,
                rank.getName()
        ));
    }

    private void joinGroup(Group group, Resident res) {
        MessageUtils.sendGroup(group, String.format("%s%s%s has joined the group %s%s",
                ChatColor.YELLOW,
                res.getPlayerName(),
                ChatColor.GRAY,
                ChatColor.YELLOW,
                group.getName()
        ));
        group.addMember(res);
        res.uninvite(group.getName());
        MessageUtils.sendSuccess(sender, "You have joined the group " + ChatColor.YELLOW + group.getName());
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

    private String getRankAsString(Group group, UUID playerID) {
        String rank = null;
        if (group.isMember(playerID)) rank = "Member";
        else if (group.isModerator(playerID)) rank = "Moderator";
        else if (group.isAdmin(playerID)) rank = "Admin";
        else if (group.isOwner(playerID)) rank = "Owner";
        return rank;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // If the user has only typed '/group', suggest the next part of the command
            List<String> suggestions = new ArrayList<>(commands.keySet());
            suggestions.add("perms");
            return suggestions;
        } else if (args.length == 2) {
            // If the user has typed '/group [something]', suggest group names for appropriate commands
            if ("perms".equalsIgnoreCase(args[0])) {
                return List.of("add", "remove", "list", "inspect", "reset");
            }
            switch (args[0].toLowerCase()) {
                case "invite", "rescind", "join", "leave", "remove", "bio", "delete", "info", "promote", "link", "unlink", "merge", "transfer" -> {
                    return CKGlobal.getResident((Player) sender).getGroups().stream().toList();
                }
            }
        } else if (args.length == 3) {
            // If the user has typed '/group [command] [something]', suggest player names or group names for appropriate commands
            if ("perms".equalsIgnoreCase(args[0])) {
                return CKGlobal.getResident((Player) sender).getGroups().stream().toList();
            }
            switch (args[0].toLowerCase()) {
                case "invite", "rescind", "remove", "transfer", "promote" -> {
                    return filterPlayerNames(args[2]);
                }
                case "link", "unlink", "merge" -> {
                    return CKGlobal.getResident((Player) sender).getGroups().stream().toList();
                }
            }
        } else if (args.length == 4) {
            // If the user has typed '/group promote [group] [player]' or '/group perms [group] [rank]', suggest ranks or permissions
            if ("promote".equalsIgnoreCase(args[0])) {
                return List.of("Member", "Moderator", "Admin");
            } else if ("perms".equalsIgnoreCase(args[0])) {
                return List.of("Member", "Moderator", "Admin");
            }
        } else if (args.length == 5) {
            // If the user has typed '/group perms [group] [rank] [something]', suggest permissions
            if ("perms".equalsIgnoreCase(args[0]) && ("add".equalsIgnoreCase(args[1]) || "remove".equalsIgnoreCase(args[1]))) {
                return PERMISSIONS;
            }
        }
        return Collections.emptyList();
    }

    private List<String> filterPlayerNames(String typedName) {
        String typedLower = typedName.toLowerCase();
        List<String> suggestions = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(typedLower)) {
                suggestions.add(player.getName());
            }
        }
        return suggestions;
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
