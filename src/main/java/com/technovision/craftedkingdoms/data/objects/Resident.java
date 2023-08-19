package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Resident {

    private UUID playerID;
    private String playerName;
    private boolean groupChat;
    private Set<String> groups;
    private Set<String> invites;
    private SoulShard soulShard;
    private boolean inspectMode;
    private Date rewardDate;

    public Resident() { }

    public Resident(Player player) {
        this.playerID = player.getUniqueId();
        this.playerName = player.getName();
        this.groupChat = false;
        this.groups = new HashSet<>();
        this.invites = new HashSet<>();
        this.soulShard = null;
        this.inspectMode = false;
        this.rewardDate = null;
    }

    public Resident(UUID playerID, String playerName, boolean groupChat, Set<String> groups, Set<String> invites, SoulShard soulShard, boolean inspectMode, Date rewardDate) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.groupChat = groupChat;
        this.groups = groups;
        this.invites = invites;
        this.soulShard = soulShard;
        this.inspectMode = inspectMode;
        this.rewardDate = rewardDate;
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

    public void shardPlayer(Player killer) {
        this.soulShard = new SoulShard(killer);
        Bson update = Updates.set("soulShard", soulShard);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
    }

    public void freePlayer(ItemStack item) {
        item.setAmount(0);
        this.soulShard = null;
        Bson update = Updates.unset("soulShard");
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);

        Player player = Bukkit.getPlayer(playerID);
        if (player != null) {
            MessageUtils.send(player, ChatColor.LIGHT_PURPLE + "You have been freed from your soul shard!");
        }
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

    public void moveShardToPlayer(UUID playerID) {
        soulShard.setBlockCoord(null);
        soulShard.setHolder(playerID);
        Bson update = Updates.set("soulShard", soulShard);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", this.playerID), update);
    }

    public void moveShardToLocation(Location location) {
        soulShard.setHolder(null);
        soulShard.setBlockCoord(new BlockCoord(location));
        Bson update = Updates.set("soulShard", soulShard);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", this.playerID), update);
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

    public SoulShard getSoulShard() {
        return soulShard;
    }

    public boolean isInspectMode() {
        return inspectMode;
    }

    public Date getRewardDate() {
        return rewardDate;
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

    public void setSoulShard(SoulShard soulShard) {
        this.soulShard = soulShard;
    }

    public void setInspectMode(boolean inspectMode) {
        this.inspectMode = inspectMode;
    }

    public void setRewardDate(Date rewardDate) {
        this.rewardDate = rewardDate;
    }
}
