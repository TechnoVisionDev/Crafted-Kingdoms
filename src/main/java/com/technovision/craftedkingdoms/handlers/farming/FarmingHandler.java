package com.technovision.craftedkingdoms.handlers.farming;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.BiomeData;
import com.technovision.craftedkingdoms.data.objects.Crop;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles crop growth and animal breeding for realistic biomes.
 *
 * @author TechnoVision
 */
public class FarmingHandler implements Listener {

    public static final Map<Location, Crop> PLANTED_CROPS = new ConcurrentHashMap<>();
    public static final Map<Location, Crop> PERSISTENT_CROPS = new ConcurrentHashMap<>();

    public FarmingHandler() {
        // Get crops from database
        for (Crop crop : Database.CROPS.find()) {
            Location cropLocation = crop.getBlockCoord().asLocation();
            if (isReadyToGrow(crop)) {
                PERSISTENT_CROPS.put(cropLocation, crop);
            } else {
                PLANTED_CROPS.put(cropLocation, crop);
            }
        }
        Database.CROPS.deleteMany(new Document());

        // Start crop scanner to run every 10 minutes
        Bukkit.getServer().getScheduler().runTaskTimer(
                CraftedKingdoms.plugin,
                new CropGrowTask(), 0, 20 * 5);
    }

    /**
     * Cancels vanilla growth mechanics.
     * @param event Fires when crop is supposed to grow.
     */
    @EventHandler
    public void onCropGrow(BlockGrowEvent event) {
        event.setCancelled(true);
    }

    /**
     * Adds a crop to crop scanner and database when planted.
     * @param event Fires when player plants a crop.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getItem();
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || itemInHand == null) return;

            Material itemType = itemInHand.getType();
            if (BiomeData.isSeed(itemType)) {
                if (itemType != Material.CACTUS && itemType != Material.SUGAR_CANE) {
                    Location cropLocation = clickedBlock.getRelative(BlockFace.UP).getLocation();
                    PLANTED_CROPS.put(cropLocation, new Crop(cropLocation, itemType));
                }
            }
        }
    }

    /**
     * Adds block crops (cactus and sugar cane) to crop scanner and database when planted.
     * @param event Fires when player places cactus or sugar cane.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        Block clickedBlock = event.getBlockPlaced();

        Material itemType = itemInHand.getType();
        if (itemType == Material.CACTUS || itemType == Material.SUGAR_CANE) {
            while (clickedBlock.getRelative(BlockFace.DOWN).getType() == itemType) {
                clickedBlock = clickedBlock.getRelative(BlockFace.DOWN);
            }
            Location cropLocation = clickedBlock.getLocation();
            PLANTED_CROPS.put(cropLocation, new Crop(cropLocation, itemType));
        }
    }

    /**
     * Grows any plants with persistence when a chunk loads.
     * @param event Fires when a chunk is loaded.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Iterator<Map.Entry<Location, Crop>> iterator = FarmingHandler.PERSISTENT_CROPS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, Crop> entry = iterator.next();
            Location location = entry.getKey();
            Crop crop = entry.getValue();

            if (chunk.equals(location.getChunk())) {
                FarmingHandler.growCrop(crop);
                Material cropMaterial = Material.valueOf(crop.getMaterial());
                if (cropMaterial == Material.SUGAR_CANE || cropMaterial == Material.CACTUS) {
                    crop.setTimePlanted(new Date());
                    FarmingHandler.PLANTED_CROPS.put(crop.getBlockCoord().asLocation(), crop);
                } else {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Checks if a crop has completed its growth timer.
     * @param crop the crop to check.
     * @return True if crop is ready to grow, otherwise false.
     */
    public static boolean isReadyToGrow(Crop crop) {
        Material cropMat = Material.valueOf(crop.getMaterial());
        Biome cropBiome = crop.getBlockCoord().asLocation().getBlock().getBiome();
        long currentTime = System.currentTimeMillis();
        double growthTime = BiomeData.getGrowthTime(cropBiome, cropMat);
        return currentTime - crop.getTimePlanted().getTime() >= growthTime;
    }

    public static void removeCrop(Location location) {
        if (FarmingHandler.PLANTED_CROPS.containsKey(location)) {
            FarmingHandler.PLANTED_CROPS.remove(location);
        }
        else FarmingHandler.PERSISTENT_CROPS.remove(location);
    }

    /**
     * Replaces a seed crop with a fully grown version.
     * @param crop the crop
     */
    public static void growCrop(Crop crop) {
        Block block = crop.getBlockCoord().asLocation().getBlock();
        Material cropType = block.getType();

        if (block.getBlockData() instanceof Ageable ageable) {
            ageable.setAge(ageable.getMaximumAge());
            block.setBlockData(ageable);

            // Check if the block is a mature Melon or Pumpkin stem
            if ((cropType == Material.MELON_STEM || cropType == Material.PUMPKIN_STEM) && ageable.getAge() == ageable.getMaximumAge()) {
                // Find a suitable location adjacent to the stem to place the Melon or Pumpkin block
                for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                    Block adjacentBlock = block.getRelative(face);
                    if (adjacentBlock.getType() == Material.AIR) {
                        // Set the adjacent block to Melon or Pumpkin, depending on the stem type
                        if (cropType == Material.MELON_STEM) {
                            adjacentBlock.setType(Material.MELON);
                        } else if (cropType == Material.PUMPKIN_STEM) {
                            adjacentBlock.setType(Material.PUMPKIN);
                        }
                        break;
                    }
                }
            }
        }
        if (cropType == Material.CACTUS || cropType == Material.SUGAR_CANE) {
            // Find the current height of the stack
            int stackHeight = 1;
            while (block.getRelative(0, stackHeight, 0).getType() == cropType) {
                stackHeight++;
            }
            if (stackHeight < 3 && block.getRelative(0, stackHeight, 0).getType() == Material.AIR) {
                block.getRelative(0, stackHeight, 0).setType(cropType);
            }
        }
    }

    /**
     * Saves all un-grown crops to database as documents.
     */
    public static void saveCropsToDatabase() {
        for (Crop crop : PLANTED_CROPS.values()) {
            Database.CROPS.insertOne(crop);
        }
        for (Crop crop : PERSISTENT_CROPS.values()) {
            Database.CROPS.insertOne(crop);
        }
    }
}
