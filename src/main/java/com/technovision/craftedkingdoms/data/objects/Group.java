package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;
import java.util.*;

public class Group {

    private String name;
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
    private Set<ObjectId> snitches;

    public Group() { }

    public Group(String name, Player owner) {
        this.name = name;
        this.biography = null;
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
        this.snitches = new HashSet<>();
    }

    public Group(String name, Player owner, boolean isPublic) {
        this.name = name;
        this.biography = null;
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
        this.snitches = new HashSet<>();
    }

    public Group(String name, Player owner, boolean isPublic, String password) {
        this.name = name;
        this.biography = null;
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
        this.snitches = new HashSet<>();
    }

    public Group(String name, String biography, UUID ownerID, Set<UUID> admins, Set<UUID> moderators, Set<UUID> members, Set<String> subGroups, Set<FortifiedBlock> fortifiedBlocks, boolean isPublic, String password, Date dateCreated, Map<String, Set<String>> rankPermissions, Set<ObjectId> snitches) {
        this.name = name;
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
        this.snitches = snitches;
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
     * Adds a player as a member of this group.
     * @param resident the resident form of the player
     */
    public void addMember(Resident resident) {
        this.members.add(resident.getPlayerID());
        Bson update = Updates.push("members", resident.getPlayerID());
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
        resident.joinGroup(name);
    }

    /**
     * Removes a player from this group (assumes player is not owner).
     * @param resident the resident form of the player
     */
    public void removeMember(Resident resident) {
        UUID id = resident.getPlayerID();
        List<Bson> updates = new ArrayList<>();
        if (this.members.remove(id)) {
            updates.add(Updates.pull("members", id));
        }
        else if (this.moderators.remove(id)) {
            updates.add(Updates.pull("moderators", id));
        }
        else if (this.admins.remove(id)) {
            updates.add(Updates.pull("admins", id));
        }
        if (!updates.isEmpty()) {
            Bson combinedUpdates = Updates.combine(updates);
            Database.GROUPS.updateOne(Filters.eq("name", name), combinedUpdates);
        }
        resident.leaveGroup(name);
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
     * Sets a new player to be the group owner.
     * @param newOwner the player to become the new owner.
     */
    public void transferOwnership(UUID newOwner) {
        UUID oldOwner = ownerID;
        admins.add(oldOwner);
        ownerID = newOwner;

        Bson pullUpdate;
        if (isMember(newOwner)) {
            members.remove(newOwner);
            pullUpdate = Updates.pull("members", newOwner);
        }
        else if (isModerator(newOwner)) {
            moderators.remove(newOwner);
            pullUpdate = Updates.pull("moderators", newOwner);
        }
        else {
            admins.remove(newOwner);
            pullUpdate = Updates.pull("admins", newOwner);
        }
        Database.GROUPS.updateOne(Filters.eq("name", name), pullUpdate);

        Bson update1 = Updates.set("ownerID", newOwner);
        Bson update2 = Updates.push("admins", oldOwner);
        Database.GROUPS.updateOne(Filters.eq("name", name), Updates.combine(update1, update2));
    }

    /**
     * Change the rank of a resident in a group.
     * @param playerID the ID of the player.
     * @param currentRank the current rank of the player.
     * @param rank the rank to change to.
     */
    public void promote(UUID playerID, Ranks currentRank, Ranks rank) {
        // Remove current rank
        Bson pullUpdate = null;
        if (currentRank == Ranks.MEMBER) {
            members.remove(playerID);
            pullUpdate = Updates.pull("members", playerID);
        }
        else if (currentRank == Ranks.MODERATOR) {
            moderators.remove(playerID);
            pullUpdate = Updates.pull("moderators", playerID);
        }
        else if (currentRank == Ranks.ADMIN) {
            admins.remove(playerID);
            pullUpdate = Updates.pull("admins", playerID);
        }

        // Give new rank
        Bson pushUpdate = null;
        if (rank == Ranks.MEMBER) {
            members.add(playerID);
            pushUpdate = Updates.push("members", playerID);
        }
        else if (rank == Ranks.MODERATOR) {
            moderators.add(playerID);
            pushUpdate = Updates.push("moderators", playerID);
        }
        else if (rank == Ranks.ADMIN) {
            admins.add(playerID);
            pushUpdate = Updates.push("admins", playerID);
        }

        // Update in database
        if (pullUpdate != null && pushUpdate != null) {
            Database.GROUPS.updateOne(Filters.eq("name", name), Updates.combine(pullUpdate, pushUpdate));
        }
    }

    /**
     * Add a block to the list of fortified blocks.
     * @param block the block to fortify
     * @param material the material used to fortify
     */
    public void fortifyBlock(Block block, Material material) {
        fortifyBlock(block.getLocation(), material);
    }

    /**
     * Add a block to the list of fortified blocks.
     * @param blockLocation the location of the block to fortify
     * @param material the material used to fortify
     */
    public void fortifyBlock(Location blockLocation, Material material) {
        FortifiedBlock fortifiedBlock = new FortifiedBlock(name, blockLocation, material);
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

    /**
     * Deletes the group.
     */
    public void delete() {
        Resident res;
        CKGlobal.getResident(ownerID).leaveGroup(name);
        for (UUID id : members) {
            res = CKGlobal.getResident(id);
            res.leaveGroup(name);
        }
        for (UUID id : moderators) {
            res = CKGlobal.getResident(id);
            res.leaveGroup(name);
        }
        for (UUID id : admins) {
            res = CKGlobal.getResident(id);
            res.leaveGroup(name);
        }
        CKGlobal.removeGroup(name);
        Database.GROUPS.deleteOne(Filters.eq("name", name));
    }

    /**
     * Updates the biography for this group.
     * @param biography the biography to set.
     */
    public void addBiography(String biography) {
        setBiography(biography);
        Bson update = Updates.set("biography", biography);
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    /**
     * Counts the total number of residents in this group.
     * @return the number of residents in this group.
     */
    public int countResidents() {
        int count = 1;
        count += members.size();
        count += moderators.size();
        count += admins.size();
        return count;
    }

    @BsonIgnore
    public Set<UUID> getResidents() {
        Set<UUID> residents = new HashSet<>(members);
        residents.addAll(moderators);
        residents.addAll(admins);
        residents.add(ownerID);
        return residents;
    }

    @BsonIgnore
    public boolean isResident(UUID playerID) {
        if (isMember(playerID)) return true;
        if (isModerator(playerID)) return true;
        if (isAdmin(playerID)) return true;
        return isOwner(playerID);
    }

    @BsonIgnore
    public Ranks findRank(UUID playerID) {
        if (isMember(playerID)) return Ranks.MEMBER;
        if (isModerator(playerID)) return Ranks.MODERATOR;
        if (isAdmin(playerID)) return Ranks.ADMIN;
        if (ownerID.equals(playerID)) return Ranks.OWNER;
        return null;
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

    public void addSnitch(Block block) {
        boolean isJukebox = block.getType() == Material.JUKEBOX;
        Snitch snitch = new Snitch(name, block, isJukebox);
        snitches.add(snitch.getId());
        CKGlobal.addSnitch(snitch);

        // Create snitch document
        Database.SNITCHES.insertOne(snitch);

        // Update group
        Bson update = Updates.push("snitches", snitch.getId());
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    public void removeSnitch(Snitch snitch) {
        snitches.remove(snitch.getId());

        // Delete snitch document
        Database.SNITCHES.deleteOne(Filters.eq("_id", snitch.getId()));

        // Update group
        Bson update = Updates.pull("snitches", snitch.getId());
        Database.GROUPS.updateOne(Filters.eq("name", name), update);
    }

    /** Getters */

    public String getName() {
        return name;
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

    public Set<ObjectId> getSnitches() {
        return snitches;
    }

    /** Setters */

    public void setName(String name) {
        this.name = name;
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

    public void setSnitches(Set<ObjectId> snitches) {
        this.snitches = snitches;
    }
}
