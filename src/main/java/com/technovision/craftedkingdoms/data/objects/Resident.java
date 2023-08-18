package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;
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
    private boolean isPearled;
    private boolean inspectMode;

    public Resident() { }

    public Resident(Player player) {
        this.playerID = player.getUniqueId();
        this.playerName = player.getName();
        this.groupChat = false;
        this.groups = new HashSet<>();
        this.invites = new HashSet<>();
        this.isPearled = false;
        this.inspectMode = false;
    }

    public Resident(UUID playerID, String playerName, boolean groupChat, Set<String> groups, Set<String> invites, boolean isPearled, boolean inspectMode) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.groupChat = groupChat;
        this.groups = groups;
        this.invites = invites;
        this.isPearled = isPearled;
        this.inspectMode = inspectMode;
    }

    /**
     * Checks if a resident's rank in the provided group has a given permission.
     * @param group the group the resident is in.
     * @param perm the permission to check for.
     * @return True if resident has perm, otherwise false.
     */
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

    public boolean hasPermission(String groupName, Permissions perm) {
        Group group = CKGlobal.getGroup(groupName);
        if (group == null) return false;
        return hasPermission(group, perm);
    }

    /**
     * Adds a group to this resident's list of invites.
     * @param groupName the name of the group.
     */
    public void invite(String groupName) {
        invites.add(groupName);
        Bson update = Updates.push("invites", groupName);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
    }

    /**
     * Removes a group from this resident's list of invites.
     * @param groupName the name of the group.
     */
    public void uninvite(String groupName) {
        invites.remove(groupName);
        Bson update = Updates.pull("invites", groupName);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
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
     * Toggles the inspect mode for this resident.
     */
    public void toggleInspectMode() {
        inspectMode = !this.inspectMode;
        Bson update = Updates.pull("inspectMode", inspectMode);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
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

    /**
     * Removes a group from this resident's list of active groups.
     * @param groupName the name of the group.
     */
    public void leaveGroup(String groupName) {
        groups.remove(groupName);
        Bson update = Updates.pull("groups", groupName);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
    }

    public void pearlPlayer() {
        isPearled = true;
        Bson update = Updates.set("pearled", true);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
    }

    public void freePlayer() {
        isPearled = false;
        Bson update = Updates.set("pearled", false);
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

    public boolean isPearled() {
        return isPearled;
    }

    public boolean isInspectMode() {
        return inspectMode;
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

    public void setPearled(boolean pearled) {
        isPearled = pearled;
    }

    public void setInspectMode(boolean inspectMode) {
        this.inspectMode = inspectMode;
    }
}
