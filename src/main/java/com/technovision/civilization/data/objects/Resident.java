package com.technovision.civilization.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.civilization.data.Database;
import com.technovision.civilization.exceptions.AlreadyRegisteredException;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;
import java.util.UUID;

/**
 * POJO object that stores data for players.
 *
 * @author TechnoVision
 */
public class Resident {

    private String playerName;
    private UUID playerID;
    private String town;
    private boolean townChat;
    private boolean civChat;

    public Resident() { }

    public Resident(Player player) {
        this.playerName = player.getName();
        this.playerID = player.getUniqueId();
        this.town = null;
        this.townChat = false;
        this.civChat = false;
    }

    public Resident(String playerName, UUID playerID, String town, boolean townChat, boolean civChat) {
        this.playerName = playerName;
        this.playerID = playerID;
        this.town = town;
        this.townChat = townChat;
        this.civChat = civChat;
    }

    public void joinTown(String townName) throws AlreadyRegisteredException  {
        if (getTown() != null && getTown().equals(townName)) {
            throw new AlreadyRegisteredException(getPlayerName()+" is already a town member.");
        }
        setTown(townName);
        Bson update = Updates.set("town", townName);
        Database.residents.updateOne(Filters.eq("playerID", getPlayerID()), update);
    }

    public void leaveTown() {
        setTown(null);
        Bson update = Updates.unset("town");
        Database.residents.updateOne(Filters.eq("playerID", getPlayerID()), update);
    }

    public Player getAsPlayer() {
        return Bukkit.getPlayer(playerID);
    }

    /** Getters */

    public String getPlayerName() {
        return playerName;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public String getTown() {
        return town;
    }

    public boolean isTownChat() {
        return townChat;
    }

    public boolean isCivChat() {
        return civChat;
    }

    /** Setters */

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerID(UUID playerID) {
        this.playerID = playerID;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setTownChat(boolean townChat) {
        this.townChat = townChat;
    }

    public void setCivChat(boolean civChat) {
        this.civChat = civChat;
    }
}
