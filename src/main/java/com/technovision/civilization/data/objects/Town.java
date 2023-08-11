package com.technovision.civilization.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.data.Database;
import com.technovision.civilization.exceptions.AlreadyRegisteredException;
import com.technovision.civilization.exceptions.CivException;
import com.technovision.civilization.managers.RegionManager;
import org.bson.conversions.Bson;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * POJO that stores data for a town within a civilization.
 *
 * @author TechnoVision.
 */
public class Town {

    private String name;
    private String civName;
    private String mayorName;
    private UUID mayorID;
    private int hitpoints;
    private boolean isCapitol;
    private Nexus nexus;
    private Set<ChunkCoord> claims;
    private Set<UUID> members;
    private Set<UUID> assistants;
    private Date dateFounded;
    private int maxClaims;
    private Yields yields;
    private int cultureLevel;

    public Town() { }

    public Town(String name, String civName, Player mayor, boolean isCapitol) throws CivException {
        this.nexus = new Nexus(name, civName, mayor);
        this.name = name;
        this.civName = civName;
        this.mayorName = mayor.getName();
        this.mayorID = mayor.getUniqueId();
        this.isCapitol = isCapitol;
        this.claims = new HashSet<>();
        this.claims.add(nexus.getChunkCoord());
        this.members = new HashSet<>();
        this.members.add(mayorID);
        this.assistants = new HashSet<>();
        this.dateFounded = new Date();

        // Stats
        this.hitpoints = 50;
        this.maxClaims = 24;
        this.yields = new Yields();
        this.cultureLevel = 1;
        addChunkYields(mayor.getWorld(), nexus.getChunkCoord());
    }

    public Town(String name, String civName, String mayorName, UUID mayorID, int hitpoints, boolean isCapitol, Nexus nexus, Set<ChunkCoord> claims, Set<UUID> members, Set<UUID> assistants, Date dateFounded, int maxClaims, Yields yields, int cultureLevel) {
        this.name = name;
        this.civName = civName;
        this.mayorName = mayorName;
        this.mayorID = mayorID;
        this.hitpoints = hitpoints;
        this.isCapitol = isCapitol;
        this.nexus = nexus;
        this.claims = claims;
        this.members = members;
        this.assistants = assistants;
        this.dateFounded = dateFounded;
        this.maxClaims = maxClaims;
        this.yields = yields;
        this.cultureLevel = cultureLevel;
    }

    public void removeResident(Resident res) {
        // Update members list in town
        res.leaveTown();
        members.remove(res.getPlayerID());
        Bson update = Updates.pull("members", res.getPlayerID());
        Database.towns.updateOne(Filters.eq("name", getName()), update);

        // Update members list in civ
        CivGlobal.getCiv(getCivName()).getMembers().remove(res.getPlayerID());
        Database.civilizations.updateOne(Filters.eq("name", getCivName()), update);
    }

    public boolean isMayor(Resident res) {
        return getMayorID().equals(res.getPlayerID());
    }

    public boolean isMayor(Player player) {
        return getMayorID().equals(player.getUniqueId());
    }

    public boolean isAssistant(Resident res) {
        return assistants.contains(res.getPlayerID());
    }

    public boolean isAssistant(Player player) {
        return assistants.contains(player.getUniqueId());
    }

    /**
     * Adds a resident to a town (and the town's civ)
     * @param res the resident joining the town.
     */
    public void addResident(Resident res) throws AlreadyRegisteredException {
        // Add resident to town
        members.add(res.getPlayerID());
        res.joinTown(getName());

        // Update town & civ in database
        Bson update = Updates.push("members", res.getPlayerID());
        Database.towns.updateOne(Filters.eq("name", getName()), update);
        Database.civilizations.updateOne(Filters.eq("name", getCivName()), update);
    }

    public boolean hasResident(Resident res) {
        for (UUID id : members) {
            if (res.getPlayerID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public int claimsUsed() {
        return claims.size();
    }

    /**
     * Claims the chunk that a player is standing in for a town.
     * @param player the player claiming land.
     */
    public void claim(Player player) {
        // Claim chunk with WorldGuard
        ChunkCoord chunk = RegionManager.claimChunk(this, player);

        // Update local data
        this.claims.add(chunk);
        CivGlobal.getCiv(getCivName()).addClaim(chunk);

        // Update Database
        Bson update = Updates.set("claims", getClaims());
        Database.towns.updateOne(Filters.eq("name", getName()), update);
        Database.civilizations.updateOne(Filters.eq("name", getCivName()), update);

        // Add yields from chunk
        addChunkYields(player.getWorld(), chunk);
    }

    /**
     * Calculates the number of available land claims that a town has.
     * @return the number of available claims (can be zero).
     */
    public int availableClaims() {
        return getMaxClaims() - (claimsUsed()-1);
    }

    /**
     * Checks if a town has available land claims
     * @return True if there are land claims, otherwise false.
     */
    public boolean hasAvailableClaims() {
        return availableClaims() > 0;
    }

    /**
     * Checks if a town has claimed a specific chunk.
     * @param chunk the chunk to check.
     * @return true if claimed, otherwise false.
     */
    public boolean isClaimed(Chunk chunk) {
        for (ChunkCoord claims : getClaims()) {
            if (claims.getX() != chunk.getX()) continue;
            if (claims.getZ() != chunk.getZ()) continue;
            return true;
        }
        return false;
    }

    /**
     * Checks if a chunk is adjacent to an existing claim.
     * @param chunk the chunk to check.
     * @return true if adjacent, otherwise false.
     */
    public boolean isValidClaim(Chunk chunk) {
        Set<ChunkCoord> claims = getClaims();
        ChunkCoord newClaim = new ChunkCoord(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        // Check if new claim is adjacent to an existing claim
        for (ChunkCoord claim : claims) {
            // Chunks are adjacent if x or z coordinates differ by 1
            if ((Math.abs(claim.getX() - newClaim.getX()) == 1 && claim.getZ() == newClaim.getZ()) ||
                    (Math.abs(claim.getZ() - newClaim.getZ()) == 1 && claim.getX() == newClaim.getX())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the biome yields to town for a single chunk
     *
     * @param world  the world the chunk is from.
     * @param center the origin chunk.
     */
    public void addChunkYields(World world, ChunkCoord center) {
        // Add stats to local data
        Yields chunkYields = CivGlobal.calculateChunkYields(world, center);
        this.yields.add(chunkYields);

        // Update stats in database
        Bson update = Updates.set("yields", this.yields);
        Database.towns.updateOne(Filters.eq("name", getName()), update);
        Database.civilizations.updateOne(Filters.eq("name", getCivName()), update);
    }

    /** Getters */

    public String getName() {
        return name;
    }

    public String getCivName() {
        return civName;
    }

    public String getMayorName() {
        return mayorName;
    }

    public UUID getMayorID() {
        return mayorID;
    }

    public int getHitpoints() {
        return hitpoints;
    }

    public boolean isCapitol() {
        return isCapitol;
    }

    public Nexus getNexus() {
        return nexus;
    }

    public Set<ChunkCoord> getClaims() {
        return claims;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public Set<UUID> getAssistants() {
        return assistants;
    }

    public Date getDateFounded() {
        return dateFounded;
    }

    public int getMaxClaims() {
        return maxClaims;
    }

    public Yields getYields() {
        return yields;
    }

    public int getCultureLevel() {
        return cultureLevel;
    }

    /** Setters */

    public void setName(String name) {
        this.name = name;
    }

    public void setCivName(String civName) {
        this.civName = civName;
    }

    public void setMayorName(String mayorName) {
        this.mayorName = mayorName;
    }

    public void setMayorID(UUID mayorID) {
        this.mayorID = mayorID;
    }

    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    public void setCapitol(boolean capitol) {
        isCapitol = capitol;
    }

    public void setNexus(Nexus nexus) {
        this.nexus = nexus;
    }

    public void setClaims(Set<ChunkCoord> claims) {
        this.claims = claims;
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public void setAssistants(Set<UUID> assistants) {
        this.assistants = assistants;
    }

    public void setDateFounded(Date dateFounded) {
        this.dateFounded = dateFounded;
    }

    public void setMaxClaims(int maxClaims) {
        this.maxClaims = maxClaims;
    }

    public void setYields(Yields yields) {
        this.yields = yields;
    }

    public void setCultureLevel(int cultureLevel) {
        this.cultureLevel = cultureLevel;
    }
}
