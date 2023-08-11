package com.technovision.civilization;

import com.technovision.civilization.data.Database;
import com.technovision.civilization.data.objects.*;
import com.technovision.civilization.data.enums.BiomeYields;
import com.technovision.civilization.queries.chat.ChatQuery;
import com.technovision.civilization.queries.chat.CreateCivQuery;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static com.technovision.civilization.data.objects.Nexus.NEXUS_MATERIAL;

public class CivGlobal {

    public static final HashMap<String, Civilization> CIVILIZATIONS = new HashMap<>();
    private static final HashMap<String, Town> TOWNS = new HashMap<>();
    public static final HashMap<UUID, Resident> RESIDENTS = new HashMap<>();
    private final CivilizationPlugin plugin;

    public CivGlobal(CivilizationPlugin plugin) {
        this.plugin = plugin;

        // Get civilizations from database
        for (Civilization civ : Database.civilizations.find()) {
            addCiv(civ.getName(), civ);
            if (civ.getCurrentResearch() != null) {
                //scheduleResearchTick(civ);
            }
        }
        // Get towns from database
        for (Town town : Database.towns.find()) {
            addTown(town.getName(), town);
        }
        // Get residents from database
        for (Resident resident : Database.residents.find()) {
            addResident(resident);
        }
    }

    /**
     * Retrieves the civ that a player is part of.
     * @param player the player specified.
     * @return the civ if it exists, otherwise null.
     */
    public static Civilization getCivByPlayer(Player player) {
        Town town = getTownByPlayer(player);
        if (town == null) return null;
        return getCiv(town.getCivName());
    }

    /** Civilization methods */

    public static void createCivilization(Player player) {
        ChatQuery.startQuery(player, new CreateCivQuery(player));
    }

    public static boolean isCiv(String civName) {
        return CIVILIZATIONS.containsKey(civName.toLowerCase());
    }

    public static Civilization getCiv(String civName) {
        return CIVILIZATIONS.get(civName.toLowerCase());
    }

    public static void addCiv(String civName, Civilization civ) {
        CIVILIZATIONS.put(civName.toLowerCase(), civ);
    }

    public static Civilization removeCiv(String civName) {
        return CIVILIZATIONS.remove(civName.toLowerCase());
    }

    /** Town methods */

    /**
     * Calculates the yields that a town placed at this chunk would get.
     * @param world the world the chunk is from
     * @param center the origin chunk
     */
    public static Yields calculateTownYields(World world, ChunkCoord center) {
        Yields totalStats = new Yields();
        // Iterate over the 5x5 chunk area
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Chunk currentChunk = world.getChunkAt(center.getX() + x, center.getZ() + z);
                Yields chunkYields = calculateChunkYields(currentChunk);
                totalStats.add(chunkYields);
            }
        }
        return totalStats;
    }

    /**
     * Calculates the yields for a single chunk.
     * @param world the world the chunk is from
     * @param center the chunk for which we want to calculate the yields
     */
    public static Yields calculateChunkYields(World world, ChunkCoord center) {
        Chunk currentChunk = world.getChunkAt(center.getX(), center.getZ());
        return calculateChunkYields(currentChunk);
    }

    /**
     * Computes the yield for a specific chunk based on its biome.
     * @param chunk the target chunk
     * @return the survey containing the yield data for the chunk
     */
    private static Yields calculateChunkYields(Chunk chunk) {
        Yields stats = new Yields();
        Biome chunkBiome = chunk.getBlock(8, 64, 8).getBiome();
        // Use the biome to fetch the corresponding enum value
        BiomeYields biomeYields;
        try {
            String biomeName = chunkBiome.name();
            stats.addBiome(biomeName);
            biomeYields = BiomeYields.valueOf(biomeName);
        } catch (IllegalArgumentException e) {
            // This biome doesn't have a corresponding enum value, use default
            stats.addBiome("OTHER");
            biomeYields = BiomeYields.OTHER;
        }
        // Add the chunk's yields to the stats
        stats.add(biomeYields);
        return stats;
    }

    public static boolean isNexus(Block block) {
        if (block.getType() != NEXUS_MATERIAL) return false;
        for (Town town : TOWNS.values()) {
            if (town.getNexus().isNexus(block)) return true;
        }
        return false;
    }

    /**
     * Retrieves the town that a player is part of.
     * @param player the player specified.
     * @return the town if it exists, otherwise null.
     */
    public static Town getTownByPlayer(Player player) {
        Resident resident = getResident(player);
        String townName = resident.getTown();
        if (townName == null) return null;
        return getTown(townName);
    }

    public static boolean isTown(String townName) {
        return TOWNS.containsKey(townName.toLowerCase());
    }

    public static Town getTown(String townName) {
        return TOWNS.get(townName.toLowerCase());
    }

    public static void addTown(String townName, Town town) {
        TOWNS.put(townName.toLowerCase(), town);
    }

    public static Town removeTown(String townName) {
        return TOWNS.remove(townName.toLowerCase());
    }

    /** Resident methods */

    public static Resident getResidentByName(String name) {
        UUID id = getIDByName(name);
        if (id == null) return null;
        return RESIDENTS.get(id);
    }

    public static UUID getIDByName(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return player.getUniqueId();
        }
        player = Bukkit.getPlayer(name);
        if (player != null) {
            return player.getUniqueId();
        }
        return null;
    }

    public static Resident getResident(Player player) {
        Resident res = RESIDENTS.get(player.getUniqueId());
        if (res == null) {
            res = createResident(player);
        }
        return res;
    }

    public static Resident createResident(Player player) {
        Resident res = new Resident(player);
        RESIDENTS.put(player.getUniqueId(), res);
        Database.residents.insertOne(res);
        return res;
    }

    private static void addResident(Resident res) {
        RESIDENTS.put(res.getPlayerID(), res);
    }

    /** Other methods */

    public CivilizationPlugin getPlugin() {
        return plugin;
    }
}
