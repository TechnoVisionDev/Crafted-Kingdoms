package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.data.Database;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;
import java.util.UUID;

public class Resident {

    private UUID playerID;
    private String playerName;
    private boolean groupChat;
    private String group;

    public Resident() { }

    public Resident(Player player) {
        this.playerID = player.getUniqueId();
        this.playerName = player.getName();
        this.groupChat = false;
        this.group = null;
    }

    public Resident(UUID playerID, String playerName, boolean groupChat, String group) {
        this.playerID = playerID;
        this.playerName = playerName;
        this.groupChat = groupChat;
        this.group = group;
    }

    public void joinGroup(String groupName) {
        setGroup(groupName);
        Bson update = Updates.set("group", groupName);
        Database.RESIDENTS.updateOne(Filters.eq("playerID", playerID), update);
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

    public String getGroup() {
        return group;
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

    public void setGroup(String group) {
        this.group = group;
    }
}
