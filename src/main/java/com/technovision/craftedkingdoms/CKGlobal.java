package com.technovision.craftedkingdoms;

import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CKGlobal {

    private static final HashMap<String, Group> GROUPS = new HashMap<>();
    private static final HashMap<UUID, Resident> RESIDENTS = new HashMap<>();

    public CKGlobal() {
        // Get groups from database
        for (Group group : Database.GROUPS.find()) {
            addGroup(group);
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

    public static void addGroup(Group group) {
        GROUPS.put(group.getName().toLowerCase(), group);
    }

    public static void removeGroup(String name) {
        GROUPS.remove(name.toLowerCase());
    }

    public static boolean isGroup(String name) {
        return GROUPS.containsKey(name.toLowerCase());
    }

    /** Resident Methods */

    public static UUID getIDByName(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return player.getUniqueId();
        }
        player = Bukkit.getPlayer(name);
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

    public static Resident getResident(String name) {
        UUID id = getIDByName(name);
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
