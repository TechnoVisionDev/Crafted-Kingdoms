package com.technovision.civilization.data.objects;

import com.technovision.civilization.exceptions.CivException;
import com.technovision.civilization.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Nexus {

    public static final int Y_SEA_LEVEL = 63;
    public static final Material NEXUS_MATERIAL = Material.BEACON;

    private BlockCoord blockCoord;
    private ChunkCoord chunkCoord;

    public Nexus() { }

    public Nexus(String townName, String civName, Player player) throws CivException {
        this.blockCoord = placeNexus(player);
        this.chunkCoord = RegionManager.claimStartingChunk(townName, civName, player);
    }

    public Nexus(BlockCoord blockCoord, ChunkCoord chunkCoord) {
        this.blockCoord = blockCoord;
        this.chunkCoord = chunkCoord;
    }

    public BlockCoord placeNexus(Player player) throws CivException {
        Location placement = player.getLocation();
        if (!isValidPlacement(placement)) {
            throw new CivException("Your town's nexus needs more space around or above it");
        }
        if (placement.getBlockY() < Y_SEA_LEVEL) {
            throw new CivException("Your town's nexus must be placed above sea level (Y=63)");
        }
        Block nexusBlock = player.getWorld().getBlockAt(placement);
        nexusBlock.setType(NEXUS_MATERIAL);
        placeNameplate(nexusBlock);
        return new BlockCoord(nexusBlock);
    }

    private void placeNameplate(Block nexus) {
        ArmorStand armorStand = (ArmorStand) nexus.getWorld()
                .spawnEntity(nexus.getLocation().add(0.5, 0, 0.5), EntityType.ARMOR_STAND);

        armorStand.setVisible(false); // so the players can't see the armor stand
        armorStand.setInvulnerable(true); // so the players can't accidentally break the armor stand
        armorStand.setGravity(false); // so the armor stand doesn't fall
        armorStand.setSmall(true); // just to make it a smaller armour stand

        armorStand.setCustomNameVisible(true); // I think this is the default option but just in case
        armorStand.setCustomName(ChatColor.GRAY + "-=( " + ChatColor.AQUA + "Nexus" + ChatColor.GRAY + " )=-");
    }

    private boolean isValidPlacement(Location loc) {
        World world = loc.getWorld();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        // Check there are no blocks on either side (top, north, south, east, west)
        if (!world.getBlockAt(x, y + 1, z).isEmpty() ||
                !world.getBlockAt(x + 1, y, z).isEmpty() ||
                !world.getBlockAt(x - 1, y, z).isEmpty() ||
                !world.getBlockAt(x, y, z + 1).isEmpty() ||
                !world.getBlockAt(x, y, z - 1).isEmpty()) {
            return false;
        }
        // Check there is a block underneath
        return !world.getBlockAt(x, y - 1, z).isEmpty();
    }

    public boolean isNexus(Block block) {
        if (block.getType() != NEXUS_MATERIAL) return false;
        return blockCoord.getX() == block.getX() && blockCoord.getZ() == block.getZ() && blockCoord.getY() == block.getY();
    }

    /** Getters */

    public BlockCoord getBlockCoord() {
        return blockCoord;
    }

    public ChunkCoord getChunkCoord() {
        return chunkCoord;
    }

    /** Setters */

    public void setBlockCoord(BlockCoord blockCoord) {
        this.blockCoord = blockCoord;
    }

    public void setChunkCoord(ChunkCoord chunkCoord) {
        this.chunkCoord = chunkCoord;
    }
}
