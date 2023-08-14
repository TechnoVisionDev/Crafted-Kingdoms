package com.technovision.craftedkingdoms.data.enums;

import java.util.Set;

import static com.technovision.craftedkingdoms.data.enums.Permissions.*;

/**
 * Enums containing default perms each player rank in groups.
 *
 * @author TechnoVision
 */
public enum Ranks {

    OWNER("Owner", Set.of(DOORS, CHESTS, BLOCKS, CONTAINERS, BEDS, ADMINS, MODS, MEMBERS, PASSWORD, SUBGROUP, PERMS, DELETE, MERGE, LIST_PERMS, TRANSFER, CROPS, GROUPSTATS, LINKING)),
    ADMIN("Admin", Set.of(DOORS, CHESTS, BLOCKS, CONTAINERS, BEDS, MODS, MEMBERS, PASSWORD, LIST_PERMS, CROPS, GROUPSTATS)),
    MODERATOR("Moderator", Set.of(DOORS, CHESTS, CONTAINERS, BEDS, BLOCKS, MEMBERS, CROPS)),
    MEMBER("Member", Set.of(DOORS, CHESTS, CONTAINERS, BEDS));

    private final String name;
    private final Set<Permissions> permissions;

    Ranks(String name, Set<Permissions> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
