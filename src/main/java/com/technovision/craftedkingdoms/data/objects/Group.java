package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
    private Set<FortifiedBlock> fortifiedBlocks;
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
        this.fortifiedBlocks = new HashSet<>();
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
        this.fortifiedBlocks = new HashSet<>();
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
        this.fortifiedBlocks = new HashSet<>();
        this.isPublic = isPublic;
        this.password = password;
        this.dateCreated = new Date();
    }

    public Group(String name, String displayName, String biography, UUID ownerID, Set<UUID> admins, Set<UUID> moderators, Set<UUID> members, Set<String> subGroups, Set<FortifiedBlock> fortifiedBlocks, boolean isPublic, String password, Date dateCreated) {
        this.name = name;
        this.displayName = displayName;
        this.biography = biography;
        this.ownerID = ownerID;
        this.admins = admins;
        this.moderators = moderators;
        this.members = members;
        this.subGroups = subGroups;
        this.fortifiedBlocks = fortifiedBlocks;
        this.isPublic = isPublic;
        this.password = password;
        this.dateCreated = dateCreated;
    }

    public void fortifyBlock(Block block, Material material) {
        FortifiedBlock fortifiedBlock = new FortifiedBlock(name, block, material);
        fortifiedBlocks.add(fortifiedBlock);
        CKGlobal.addFortifiedBlock(fortifiedBlock);
        Bson update = Updates.push("fortifiedBlocks", fortifiedBlock);
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    public void removeFortifiedBlock(FortifiedBlock block) {
        fortifiedBlocks.remove(block);
        Bson update = Updates.pull("fortifiedBlocks", block);
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
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

    public Set<FortifiedBlock> getFortifiedBlocks() {
        return fortifiedBlocks;
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

    public void setFortifiedBlocks(Set<FortifiedBlock> fortifiedBlocks) {
        this.fortifiedBlocks = fortifiedBlocks;
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
