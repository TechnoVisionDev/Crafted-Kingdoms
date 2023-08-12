package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.data.Database;
import org.bson.codecs.pojo.annotations.BsonIgnore;
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

    public Resident() { }

    public Resident(Player player) {
        this.playerID = player.getUniqueId();
        this.playerName = player.getName();
        this.groupChat = false;
        this.groups = new HashSet<>();
    }

    public Resident(UUID playerID, String playerName, boolean groupChat, Set<String> groups) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.groupChat = groupChat;
        this.groups = groups;
    }

    public void joinGroup(String groupName) {
        groups.add(groupName);
        Bson update = Updates.push("group", groupName);
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

    /** Getters */

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
}
