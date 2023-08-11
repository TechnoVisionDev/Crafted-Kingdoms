package com.technovision.tribes;

import com.technovision.tribes.data.Database;
import com.technovision.tribes.data.objects.*;

import java.util.HashMap;

public class TribesGlobal {

    private static final HashMap<String, Tribe> TRIBES = new HashMap<>();

    public TribesGlobal() {
        // Get towns from database
        for (Tribe tribe : Database.tribes.find()) {
            addTribe(tribe);
        }
    }

    public static void addTribe(Tribe tribe) {
        TRIBES.put(tribe.getName().toLowerCase(), tribe);
    }

    public static void removeTribe(String name) {
        TRIBES.remove(name.toLowerCase());
    }

    public static boolean isTribe(String name) {
        return TRIBES.containsKey(name.toLowerCase());
    }
}
