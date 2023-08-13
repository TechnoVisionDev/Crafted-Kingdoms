package com.technovision.craftedkingdoms.data.enums;

import java.util.List;
import java.util.Set;

import static com.technovision.craftedkingdoms.data.enums.Permissions.*;

/**
 * Enums containing default perms each player rank in groups.
 *
 * @author TechnoVision
 */
public enum Ranks {

    OWNER(Set.of(DOORS, CHESTS, BLOCKS, ADMINS, MODS, MEMBERS, PASSWORD, SUBGROUP, PERMS, DELETE, MERGE, LIST_PERMS, TRANSFER, CROPS, GROUPSTATS, LINKING)),
    ADMIN(Set.of(DOORS, CHESTS, BLOCKS, MODS, MEMBERS, PASSWORD, LIST_PERMS, CROPS, GROUPSTATS)),
    MODERATOR(Set.of(DOORS, CHESTS, BLOCKS, MEMBERS, CROPS)),
    MEMBER(Set.of(DOORS, CHESTS));

    private final Set<Permissions> permissions;

    Ranks(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }
}
