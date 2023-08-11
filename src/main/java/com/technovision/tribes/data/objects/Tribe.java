package com.technovision.tribes.data.objects;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Tribe {

    private String name;
    private String displayName;
    private UUID ownerID;
    private Set<UUID> admins;
    private Set<UUID> moderators;
    private Set<UUID> members;
    private Set<BlockCoord> protectedBlocks;

    public Tribe() { }

    public Tribe(String name, Player owner) {
        this.name = name.toLowerCase();
        this.displayName = name;
        this.ownerID = owner.getUniqueId();
        this.admins = new HashSet<>();
        this.moderators = new HashSet<>();
        this.members = new HashSet<>();
        this.protectedBlocks = new HashSet<>();
    }

    public Tribe(String name, String displayName, UUID ownerID, Set<UUID> admins, Set<UUID> moderators, Set<UUID> members, Set<BlockCoord> protectedBlocks) {
        this.name = name;
        this.displayName = displayName;
        this.ownerID = ownerID;
        this.admins = admins;
        this.moderators = moderators;
        this.members = members;
        this.protectedBlocks = protectedBlocks;
    }

    /** Getters */

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    public Set<UUID> getAdmins() {
        return admins;
    }

    public Set<UUID> getModerators() {
        return moderators;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public Set<BlockCoord> getProtectedBlocks() {
        return protectedBlocks;
    }

    /** Setters */

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setOwnerID(UUID ownerID) {
        this.ownerID = ownerID;
    }

    public void setAdmins(Set<UUID> admins) {
        this.admins = admins;
    }

    public void setModerators(Set<UUID> moderators) {
        this.moderators = moderators;
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public void setProtectedBlocks(Set<BlockCoord> protectedBlocks) {
        this.protectedBlocks = protectedBlocks;
    }
}
