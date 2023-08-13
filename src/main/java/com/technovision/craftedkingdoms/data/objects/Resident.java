package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Resident {

    private UUID playerID;
    private String playerName;
    private boolean groupChat;
    private Set<String> groups;
    private Set<String> invites;

    public Resident() { }

    public Resident(Player player) {
        this.playerID = player.getUniqueId();
        this.playerName = player.getName();
        this.groupChat = false;
        this.groups = new HashSet<>();
        this.invites = new HashSet<>();
    }

    public Resident(UUID playerID, String playerName, boolean groupChat, Set<String> groups, Set<String> invites) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.groupChat = groupChat;
        this.groups = groups;
        this.invites = invites;
    }

    public boolean hasPermission(Group group, Permissions perm) {
        if (group.isOwner(playerID)) {
            return true;
        }
        if (group.isMember(playerID)) {
            return group.hasPermission(Ranks.MEMBER, perm);
        }
        if (group.isModerator(playerID)) {
            return group.hasPermission(Ranks.MODERATOR, perm);
        }
        if (group.isAdmin(playerID)) {
            return group.hasPermission(Ranks.ADMIN, perm);
        }
        return false;
    }

    /**
     * Adds a group to this resident's list of invites.
     * @param groupName the name of the group.
     */
    public void invite(String groupName) {
        invites.add(groupName);
        Bson update = Updates.push("invites", groupName);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);

        // Send message to player if they are online
        Player player = Bukkit.getPlayer(playerID);
        if (player != null) {
            MessageUtils.send(player, ChatColor.GRAY + "You received an invite to join the group " + ChatColor.YELLOW + groupName + ChatColor.GRAY + ".");
            MessageUtils.send(player, ChatColor.GRAY + "Use the " + ChatColor.YELLOW + "/group join" + ChatColor.GRAY + " command to join!");
        }
    }

    /**
     * Checks if player has an invitation to a group.
     * @param groupName the name of the group to check for an invitation.
     * @return true if player has invite from group, otherwise false.
     */
    public boolean hasInvite(String groupName) {
        return invites.contains(groupName);
    }

    /**
     * Adds a group to this resident's list of active groups.
     * @param groupName the name of the group.
     */
    public void joinGroup(String groupName) {
        groups.add(groupName);
        Bson update = Updates.push("groups", groupName);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
    }

    @BsonIgnore
    public boolean isInGroup() {
        return !groups.isEmpty();
    }

    @BsonIgnore
    public boolean isInGroup(String groupName) {
        for (String group : groups) {
            if (group.equalsIgnoreCase(groupName)) {
                return true;
            }
        }
        return false;
    }

    /** Getters */

    public UUID getPlayerID() {
        return playerID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isGroupChat() {
        return groupChat;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Set<String> getInvites() {
        return invites;
    }

    /** Setters */

    public void setPlayerID(UUID playerID) {
        this.playerID = playerID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    public void setInvites(Set<String> invites) {
        this.invites = invites;
    }
}
