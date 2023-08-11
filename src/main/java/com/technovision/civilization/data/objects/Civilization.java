package com.technovision.civilization.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.data.Database;
import com.technovision.civilization.data.enums.Technology;
import org.bson.conversions.Bson;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * POJO that stores data for each civilization.
 *
 * @author TechnoVision.
 */
public class Civilization {

    private String name;
    private String leaderName;
    private UUID leaderID;
    private Set<ChunkCoord> claims;
    private Set<UUID> members;
    private Set<UUID> advisors;
    private String capitol;
    private Set<String> towns;
    private Date dateFounded;
    private Yields yields;
    private String government;
    private Set<String> technology;
    private Research currentResearch;
    private double treasury;

    public Civilization() { }

    public Civilization(String name, Player leader, String capitol, ChunkCoord startingClaim) {
        this.name = name;
        this.leaderName = leader.getName();
        this.leaderID = leader.getUniqueId();
        this.claims = new HashSet<>();
        this.claims.add(startingClaim);
        this.members = new HashSet<>();
        this.members.add(leaderID);
        this.advisors = new HashSet<>();
        this.capitol = capitol;
        this.towns = new HashSet<>();
        towns.add(capitol);
        this.dateFounded = new Date();
        this.yields = new Yields();
        this.government = "Tribalism";
        this.technology = new HashSet<>();
        this.currentResearch = null;
        this.treasury = 0;

        // Add starting chunk yields
        addChunkYields(leader.getWorld(), startingClaim);
    }

    public Civilization(String name, String leaderName, UUID leaderID, Set<ChunkCoord> claims, Set<UUID> members, Set<UUID> advisors, String capitol, Set<String> towns, Date dateFounded, Yields yields, String government, Set<String> technology, Research currentResearch, double treasury) {
        this.name = name;
        this.leaderName = leaderName;
        this.leaderID = leaderID;
        this.claims = claims;
        this.members = members;
        this.advisors = advisors;
        this.capitol = capitol;
        this.towns = towns;
        this.dateFounded = dateFounded;
        this.yields = yields;
        this.government = government;
        this.technology = technology;
        this.currentResearch = currentResearch;
        this.treasury = treasury;
    }

    /**
     * Adds the biome yields to civ for a single chunk
     *
     * @param world  the world the chunk is from.
     * @param center the origin chunk.
     */
    public void addChunkYields(World world, ChunkCoord center) {
        // Add starting chunk yields
        Yields chunkYields = CivGlobal.calculateChunkYields(world, center);
        getYields().add(chunkYields);

        // Update stats in database
        Bson update = Updates.set("yields", getYields());
        Database.civilizations.updateOne(Filters.eq("name", getName()), update);
    }

    public boolean canAfford(double price) {
        return treasury >= price;
    }

    public void deductFromTreasury(double amount) {
        treasury -= amount;
    }

    public void addToTreasury(double amount) {
        treasury += amount;
    }

    public void addClaim(ChunkCoord chunk) {
        this.claims.add(chunk);
    }

    public void tickResearch() {
        double progress = getCurrentResearch().tick(getYields().getScience());
        Bson update = Updates.set("currentResearch.progress", progress);
        Database.civilizations.updateOne(Filters.eq("name", getName()), update);
        if (isResearchComplete()) {
            Technology tech = Technology.valueOf(getCurrentResearch().getTechnology());
            technology.add(tech.toString());
            update = Updates.push("technology", tech.toString());
            Database.civilizations.updateOne(Filters.eq("name", getName()), update);
        }
    }

    public boolean isResearchComplete() {
        if (getCurrentResearch() == null) return true;
        return getCurrentResearch().isComplete();
    }

    public boolean canResearch(Technology tech) {
        boolean canResearch = true;
        for (Technology req : tech.getRequired()) {
            if (!hasResearched(req)) {
                canResearch = false;
                break;
            }
        }
        return canResearch;
    }

    public boolean hasResearched(Technology tech) {
        return technology.contains(tech.toString());
    }

    public List<Technology> getAvailableTechs() {
        List<Technology> available = new ArrayList<>();
        for (Technology tech : Technology.values()) {
            if (hasResearched(tech)) continue;
            if (canResearch(tech)) {
                available.add(tech);
            }
        }
        return available;
    }

    public boolean isLeader(Resident res) {
        return getLeaderID().equals(res.getPlayerID());
    }

    public boolean isLeader(Player player) {
        return getLeaderID().equals(player.getUniqueId());
    }

    public boolean isAdvisor(Resident res) {
        return advisors.contains(res.getPlayerID());
    }

    public boolean isAdvisor(Player player) {
        return advisors.contains(player.getUniqueId());
    }

    /** Getters */

    public String getName() {
        return name;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public UUID getLeaderID() {
        return leaderID;
    }

    public Set<ChunkCoord> getClaims() {
        return claims;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public Set<UUID> getAdvisors() {
        return advisors;
    }

    public String getCapitol() {
        return capitol;
    }

    public Set<String> getTowns() {
        return towns;
    }

    public Date getDateFounded() {
        return dateFounded;
    }

    public Yields getYields() {
        return yields;
    }

    public String getGovernment() {
        return government;
    }

    public Set<String> getTechnology() {
        return technology;
    }

    public Research getCurrentResearch() {
        return currentResearch;
    }

    public double getTreasury() {
        return treasury;
    }

    /** Setters */

    public void setName(String name) {
        this.name = name;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public void setLeaderID(UUID leaderID) {
        this.leaderID = leaderID;
    }

    public void setClaims(Set<ChunkCoord> claims) {
        this.claims = claims;
    }

    public void setMembers(Set<UUID> members) {
        this.members = members;
    }

    public void setAdvisors(Set<UUID> advisors) {
        this.advisors = advisors;
    }

    public void setCapitol(String capitol) {
        this.capitol = capitol;
    }

    public void setTowns(Set<String> towns) {
        this.towns = towns;
    }

    public void setDateFounded(Date dateFounded) {
        this.dateFounded = dateFounded;
    }

    public void setYields(Yields yields) {
        this.yields = yields;
    }

    public void setGovernment(String government) {
        this.government = government;
    }

    public void setTechnology(Set<String> technology) {
        this.technology = technology;
    }

    public void setCurrentResearch(Research currentResearch) {
        this.currentResearch = currentResearch;
    }

    public void setTreasury(double treasury) {
        this.treasury = treasury;
    }
}
