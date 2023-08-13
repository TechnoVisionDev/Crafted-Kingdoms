package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

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
    private Map<String, Set<String>> rankPermissions;

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
        fillDefaultPermissions();
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
        fillDefaultPermissions();
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
        fillDefaultPermissions();
    }

    public Group(String name, String displayName, String biography, UUID ownerID, Set<UUID> admins, Set<UUID> moderators, Set<UUID> members, Set<String> subGroups, Set<FortifiedBlock> fortifiedBlocks, boolean isPublic, String password, Date dateCreated, Map<String, Set<String>> rankPermissions) {
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
        this.rankPermissions = rankPermissions;
    }

    /**
     * Creates a map of player ranks to a set of default permissions.
     */
    private void fillDefaultPermissions() {
        this.rankPermissions = new HashMap<>();
        Set<String> perms;
        for (Ranks rank : Ranks.values()) {
            if (rank == Ranks.OWNER) continue;
            perms = new HashSet<>();
            for (Permissions perm : rank.getPermissions()) {
                perms.add(perm.toString());
            }
            this.rankPermissions.put(rank.toString(), perms);
        }
    }

    /**
     * Adds a permission to this group's player rank.
     * @param rank the rank to add the perm to.
     * @param perm the permission to add.
     */
    public void addPermissionToRank(Ranks rank, Permissions perm) {
        this.rankPermissions.get(rank.toString()).add(perm.toString());
        Bson update = Updates.addToSet("rankPermissions." + rank, perm.toString());
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    /**
     * Removes a permission from this group's player rank.
     * @param rank the rank to remove the perm from.
     * @param perm the permission to remove.
     */
    public void removePermissionFromRank(Ranks rank, Permissions perm) {
        this.rankPermissions.get(rank.toString()).remove(perm.toString());
        Bson update = Updates.pull("rankPermissions." + rank, perm.toString());
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    /**
     * Checks if a rank has a permission set for this group.
     * @param rank the rank to check.
     * @param perm the permission to check for.
     * @return true if rank has permission, otherwise false.
     */
    public boolean hasPermission(Ranks rank, Permissions perm) {
        if (rank == Ranks.OWNER) return true;
        return rankPermissions.get(rank.toString()).contains(perm.toString());
    }

    /**
     * Add a block to the list of fortified blocks.
     * @param block the block to fortify
     * @param material the material used to fortify
     */
    public void fortifyBlock(Block block, Material material) {
        FortifiedBlock fortifiedBlock = new FortifiedBlock(name, block, material);
        fortifiedBlocks.add(fortifiedBlock);
        CKGlobal.addFortifiedBlock(fortifiedBlock);
        Bson update = Updates.push("fortifiedBlocks", fortifiedBlock);
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    /**
     * Remove a block from the list of fortified blocks.
     * @param block the block to remove.
     */
    public void removeFortifiedBlock(FortifiedBlock block) {
        fortifiedBlocks.remove(block);
        Bson update = Updates.pull("fortifiedBlocks", block);
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    @BsonIgnore
    public boolean isMember(UUID playerID) {
        return members.contains(playerID);
    }

    @BsonIgnore
    public boolean isModerator(UUID playerID) {
        return moderators.contains(playerID);
    }

    @BsonIgnore
    public boolean isAdmin(UUID playerID) {
        return admins.contains(playerID);
    }

    @BsonIgnore
    public boolean isOwner(UUID playerID) {
        return ownerID.equals(playerID);
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

    public Map<String, Set<String>> getRankPermissions() {
        return rankPermissions;
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

    public void setRankPermissions(Map<String, Set<String>> rankPermissions) {
        this.rankPermissions = rankPermissions;
    }
}
