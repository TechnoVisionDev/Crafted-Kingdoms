package com.technovision.civilization.managers;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.data.objects.ChunkCoord;
import com.technovision.civilization.data.objects.Town;
import com.technovision.civilization.util.CivColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RegionManager {

    private static final int CHUNK_SIZE = 16;
    private static final int Y_MAX = 255;
    private static final int Y_MIN = 0;

    private final CivilizationPlugin plugin;

    public RegionManager(CivilizationPlugin plugin) {
        this.plugin = plugin;
    }

    public static ChunkCoord claimStartingChunk(String townName, String civName, Player player) {
        // Create region
        String regionName = townName.replace(" ", "_");
        regionName += "_0";
        ProtectedCuboidRegion region = createChunkRegion(regionName, player);
        setTownClaimFlags(townName, civName, region);

        // Add player to region
        LocalPlayer localPlayer = CivilizationPlugin.worldGuard.wrapPlayer(player);
        region.getMembers().addPlayer(localPlayer.getUniqueId());

        // Add region to the world
        com.sk89q.worldguard.protection.managers.RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(player.getWorld()));
        regions.addRegion(region);

        // Return chunk coordinates
        Chunk chunk = player.getLocation().getChunk();
        return new ChunkCoord(player.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static ChunkCoord claimChunk(Town town, Player player) {
        // Create region
        String regionName = town.getName().replace(" ", "_");
        regionName = regionName.replace("\"", "");
        regionName = regionName.replace("\'", "");
        regionName += "_" + town.claimsUsed();
        ProtectedCuboidRegion region = createChunkRegion(regionName, player);
        setTownClaimFlags(town.getName(), town.getCivName(), region);

        // Add entire town to region
        for (UUID memberID : town.getMemberIDs()) {
            region.getMembers().addPlayer(memberID);
        }

        // Add region to the world
        com.sk89q.worldguard.protection.managers.RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(player.getWorld()));
        regions.addRegion(region);

        // Return chunk coordinates
        Chunk chunk = player.getLocation().getChunk();
        return new ChunkCoord(player.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    private static void setTownClaimFlags(String townName, String civName, ProtectedCuboidRegion region) {
        // Member flags
        region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
        region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.ALLOW);
        region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.FIRE_SPREAD, StateFlag.State.DENY);
        region.setFlag(Flags.LAVA_FIRE, StateFlag.State.DENY);
        region.setFlag(Flags.TNT, StateFlag.State.DENY);
        region.setFlag(Flags.PVP, StateFlag.State.DENY);
        region.setFlag(Flags.GREET_MESSAGE, String.format("%s%s (%s) %s[No PvP]", CivColor.Gold, townName, civName, CivColor.Green));

        // Non-Member flags
        region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
        region.setFlag(Flags.BLOCK_BREAK.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
        region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);
        region.setFlag(Flags.BLOCK_PLACE.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
    }

    private static void setPreviewFlags(ProtectedCuboidRegion region) {
        region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
        region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
        region.setFlag(Flags.FIRE_SPREAD, StateFlag.State.DENY);
        region.setFlag(Flags.LAVA_FIRE, StateFlag.State.DENY);
        region.setFlag(Flags.TNT, StateFlag.State.DENY);
        region.setFlag(Flags.PVP, StateFlag.State.DENY);
        region.setFlag(Flags.ITEM_DROP, StateFlag.State.DENY);
        region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY);
        region.setFlag(Flags.CHORUS_TELEPORT, StateFlag.State.DENY);
        region.setFlag(Flags.ENDERPEARL, StateFlag.State.DENY);
    }

    private static ProtectedCuboidRegion createChunkRegion(String regionName, Player player) {
        BlockVector3 origin = calculateChunkOrigin(player);
        return new ProtectedCuboidRegion(
                regionName,
                BlockVector3.at(origin.getX(), Y_MAX, origin.getZ()),
                BlockVector3.at(origin.getX() + 15, Y_MIN, origin.getZ() + 15)
        );
    }

    private static ProtectedCuboidRegion create2By2Region(String regionName, Player player) {
        BlockVector3 origin = calculateChunkOrigin(player);
        return new ProtectedCuboidRegion(
                player.getDisplayName()+"_preview",
                BlockVector3.at(origin.getX(), Y_MAX, origin.getZ()),
                BlockVector3.at(origin.getX()+31, Y_MIN, origin.getZ()+31)
        );
    }

    public static boolean isRegionDefined(String regionName, World world) {
        com.sk89q.worldguard.protection.managers.RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(world));
        if (regions == null) return false;
        return regions.hasRegion(regionName);
    }

    public static ApplicableRegionSet getApplicableRegions(Player player) {
        com.sk89q.worldguard.protection.managers.RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(player.getWorld()));
        Location playerLoc = player.getLocation();
        return regions.getApplicableRegions(BlockVector3.at(playerLoc.getBlockX(), playerLoc.getY(), playerLoc.getZ()));
    }

    public static boolean isInForeignTerritory(Player player) {
        Location location = player.getLocation();
        BlockVector3 bv = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        com.sk89q.worldguard.protection.managers.RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(player.getWorld()));
        for (ProtectedRegion r : regions.getApplicableRegions(bv)) {
            if (r.getMembers().contains(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    private static BlockVector3 calculateChunkOrigin(Player player) {
        // Determine chunk coordinates of the current chunk
        int chunkX = player.getLocation().getChunk().getX();
        int chunkZ = player.getLocation().getChunk().getZ();

        // Calculate the origin for the chunk the player is currently in
        return BlockVector3.at(
                chunkX * CHUNK_SIZE,
                player.getLocation().getY(),
                chunkZ * CHUNK_SIZE);
    }

    private static void removePreviewRegion(Player player) {
        com.sk89q.worldguard.protection.managers.RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(player.getWorld()));
        regions.removeRegion(player.getDisplayName()+"_preview");
    }
}
