package com.technovision.craftedkingdoms.data.objects;

import org.bukkit.entity.Player;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Group {

    private String name;
    private String displayName;
    private String biography;
    private UUID ownerID;
    private Set<UUID> admins;
    private Set<UUID> moderators;
    private Set<UUID> members;
    private Set<String> subGroups;
    private Set<BlockCoord> protectedBlocks;
    private boolean isPublic;
    private String password;
    private Date dateCreated;

    public Group() { }

    public Group(String name, Player owner) {
        this.name = name;
        this.displayName = name;
        this.biography = "";
        this.ownerID = owner.getUniqueId();
        this.admins = new HashSet<>();
        this.moderators = new HashSet<>();
        this.members = new HashSet<>();
        this.subGroups = new HashSet<>();
        this.protectedBlocks = new HashSet<>();
        this.isPublic = false;
        this.password = null;
        this.dateCreated = new Date();
    }

    public Group(String name, Player owner, boolean isPublic) {
        this.name = name;
        this.displayName = name;
        this.biography = "";
        this.ownerID = owner.getUniqueId();
        this.admins = new HashSet<>();
        this.moderators = new HashSet<>();
        this.members = new HashSet<>();
        this.subGroups = new HashSet<>();
        this.protectedBlocks = new HashSet<>();
        this.isPublic = isPublic;
        this.password = null;
        this.dateCreated = new Date();
    }

    public Group(String name, Player owner, boolean isPublic, String password) {
        this.name = name;
        this.displayName = name;
        this.biography = "";
        this.ownerID = owner.getUniqueId();
        this.admins = new HashSet<>();
        this.moderators = new HashSet<>();
        this.members = new HashSet<>();
        this.subGroups = new HashSet<>();
        this.protectedBlocks = new HashSet<>();
        this.isPublic = isPublic;
        this.password = password;
        this.dateCreated = new Date();
    }

    public Group(String name, String displayName, String biography, UUID ownerID, Set<UUID> admins, Set<UUID> moderators, Set<UUID> members, Set<String> subGroups, Set<BlockCoord> protectedBlocks, boolean isPublic, String password, Date dateCreated) {
        this.name = name;
        this.displayName = displayName;
        this.biography = biography;
        this.ownerID = ownerID;
        this.admins = admins;
        this.moderators = moderators;
        this.members = members;
        this.subGroups = subGroups;
        this.protectedBlocks = protectedBlocks;
        this.isPublic = isPublic;
        this.password = password;
        this.dateCreated = dateCreated;
    }

    /** Getters */

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBiography() {
        return biography;
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

    public Set<String> getSubGroups() {
        return subGroups;
    }

    public Set<BlockCoord> getProtectedBlocks() {
        return protectedBlocks;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getPassword() {
        return password;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    /** Setters */

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setBiography(String biography) {
        this.biography = biography;
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

    public void setSubGroups(Set<String> subGroups) {
        this.subGroups = subGroups;
    }

    public void setProtectedBlocks(Set<BlockCoord> protectedBlocks) {
        this.protectedBlocks = protectedBlocks;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
