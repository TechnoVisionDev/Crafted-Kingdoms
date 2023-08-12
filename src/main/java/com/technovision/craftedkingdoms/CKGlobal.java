package com.technovision.craftedkingdoms;

import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CKGlobal {

    private static final HashMap<String, Group> GROUPS = new HashMap<>();
    private static final HashMap<UUID, Resident> RESIDENTS = new HashMap<>();
    private static final HashMap<Player, String> FORTIFY_GROUPS = new HashMap<>();
    private static final HashMap<Location, FortifiedBlock> FORTIFIED_BLOCKS = new HashMap<>();


    public CKGlobal() {
        // Get groups from database
        for (Group group : Database.GROUPS.find()) {
            addGroup(group);
            for (FortifiedBlock block : group.getFortifiedBlocks()) {
                addFortifiedBlock(block);
            }
        }
        // Get residents from database
        for (Resident res : Database.RESIDENTS.find()) {
            addResident(res);
        }
    }

    /** Group Methods */

    public static Group createGroup(String name, Player owner, boolean isPublic, String password) {
        Group group;
        if (password != null) {
            group = new Group(name, owner, isPublic, password);
        } else {
            group = new Group(name, owner, isPublic);
        }
        addGroup(group);
        Database.GROUPS.insertOne(group);
        getResident(owner).joinGroup(name);
        return group;
    }

    public static Group getGroup(String groupName) {
        if (groupName == null) return null;
        return GROUPS.get(groupName.toLowerCase());
    }

    public static void addGroup(Group group) {
        GROUPS.put(group.getName().toLowerCase(), group);
    }

    public static void removeGroup(String name) {
        GROUPS.remove(name.toLowerCase());
    }

    public static boolean isGroup(String name) {
        return GROUPS.containsKey(name.toLowerCase());
    }

    public static void addFortifyGroup(Player player, String groupName) {
        FORTIFY_GROUPS.put(player, groupName);
    }

    public static Group getFortifyGroup(Player player) {
        return getGroup(FORTIFY_GROUPS.get(player));
    }

    public static void addFortifiedBlock(FortifiedBlock block) {
        FORTIFIED_BLOCKS.put(block.findLocation(), block);
    }

    public static FortifiedBlock getFortifiedBlock(Location location) {
        return FORTIFIED_BLOCKS.get(location);
    }

    public static void removeFortifiedBlock(Location location) {
        FortifiedBlock block = FORTIFIED_BLOCKS.remove(location);
        getGroup(block.getGroup()).removeFortifiedBlock(block);
    }

    /** Player & Resident Methods */

    /**
     * Gets the UUID of a player that is either online or offline.
     * @param playerName the name of the player to search for.
     * @return the UUID of the player, null if player doesn't exist.
     */
    public static UUID getPlayerID(String playerName) {
        UUID id = getOnlinePlayerID(playerName);
        if (id != null) return id;
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                return offlinePlayer.getUniqueId();
            }
        }
        return null;
    }

    /**
     * Gets the UUID of a player that is online
     * @param playerName the name of the player to search for.
     * @return the UUID of the player, null if player is offline or doesn't exist.
     */
    public static UUID getOnlinePlayerID(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null) {
            return player.getUniqueId();
        }
        return null;
    }

    public static Resident getResident(Player player) {
        Resident res = RESIDENTS.get(player.getUniqueId());
        if (res == null) {
            res = createResident(player);
        }
        return res;
    }

    public static Resident getResident(String playerName) {
        UUID id = getPlayerID(playerName);
        if (id == null) return null;
        return RESIDENTS.get(id);
    }

    public static Resident createResident(Player player) {
        Resident res = new Resident(player);
        addResident(res);
        Database.RESIDENTS.insertOne(res);
        return res;
    }

    private static void addResident(Resident res) {
        RESIDENTS.put(res.getPlayerID(), res);
    }
}
