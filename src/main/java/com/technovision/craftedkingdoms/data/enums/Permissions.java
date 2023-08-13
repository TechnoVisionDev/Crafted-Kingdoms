package com.technovision.craftedkingdoms.data.enums;

/**
 * Enums containing data for permissions assigned to group ranks.
 *
 * @author TechnoVision
 */
public enum Permissions {
    DOORS("Can open and close reinforced doors"),
    CHESTS("Can open and close reinforced chests"),
    BLOCKS("Can reinforce blocks to the group or bypass existing reinforced blocks"),
    ADMINS("Can add or remove admins"),
    MODS("Can add or remove mods"),
    MEMBERS("Can add or remove members"),
    PASSWORD("Can add or remove password to the group"),
    SUBGROUP("Can add subgroup"),
    PERMS("Can modify the permissions a PlayerType has"),
    DELETE("Can delete the group"),
    JOIN_PASSWORD("Can specify which PlayerType a player will be, when they join with a password"),
    MERGE("Can merge groups with another group they have MERGE permissions on"),
    LIST_PERMS("Can list the permissions of any PlayerType"),
    TRANSFER("Can transfer the group from one OWNER to another person. Does not affect other owners on the group"),
    CROPS("Allows access to reinforced crops"),
    GROUPSTATS("Allows access to use GroupStats Command"),
    LINKING("Can nest and un-nest the group");

    private final String description;

    Permissions(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
