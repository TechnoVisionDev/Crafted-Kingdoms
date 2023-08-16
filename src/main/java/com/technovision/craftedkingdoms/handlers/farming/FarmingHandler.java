package com.technovision.craftedkingdoms.handlers.farming;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.BiomeData;
import com.technovision.craftedkingdoms.data.objects.Crop;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles crop growth and animal breeding for realistic biomes.
 *
 * @author TechnoVision
 */
public class FarmingHandler implements Listener {

    public static final Map<Location, Crop> PLANTED_CROPS = new ConcurrentHashMap<>();

    public FarmingHandler() {
        // Get crops from database
        for (Crop crop : Database.CROPS.find()) {
            Location cropLocation = crop.getBlockCoord().asLocation();
            PLANTED_CROPS.put(cropLocation, crop);
        }
        Database.CROPS.deleteMany(new Document());

        // Start crop scanner to run every 10 minutes
        Bukkit.getServer().getScheduler().runTaskTimer(
                CraftedKingdoms.plugin,
                new CropGrowTask(), 0, 20 * 60 * 10);
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
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getItem();
            Block clickedBlock = event.getClickedBlock();

            if (!BiomeData.isSeed(itemInHand.getType())) return;
            Block blockAbove = clickedBlock.getRelative(0, 1, 0);
            Crop crop = new Crop(blockAbove.getLocation(), itemInHand.getType());
            double growthTime = BiomeData.getGrowthTime(crop) / 60 / 60 / 1000;

            String itemName = itemInHand.getItemMeta().getDisplayName();
            if (growthTime <= 0) {
                MessageUtils.send(event.getPlayer(), ChatColor.GOLD + itemName + " will not grow here!");
            } else {
                MessageUtils.send(event.getPlayer(), ChatColor.GOLD + itemName + " will grow here within " + growthTime + " hours");
            }
        }

        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
        if (itemType == Material.SWEET_BERRIES
                || itemType == Material.SWEET_BERRY_BUSH
                || itemType == Material.GLOW_BERRIES
                || itemType == Material.KELP
                || itemType == Material.BAMBOO
                || itemType == Material.CHORUS_PLANT
        ) return;

        if (itemType == Material.CACTUS || itemType == Material.SUGAR_CANE) {
            while (clickedBlock.getRelative(BlockFace.DOWN).getType() == itemType) {
                clickedBlock = clickedBlock.getRelative(BlockFace.DOWN);
            }
            Location cropLocation = clickedBlock.getLocation();
            PLANTED_CROPS.put(cropLocation, new Crop(cropLocation, itemType));
        }
    }

    /**
     * Handles pumpkin and melon stem placement when removing fruit.
     * @param event Fires when player breaks a pumpkin or melon attached to a stem.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        Material brokenBlockType = brokenBlock.getType();
        if (brokenBlockType != Material.MELON && brokenBlockType != Material.PUMPKIN) return;

        // Check adjacent blocks for a stem
        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            Block adjacentBlock = brokenBlock.getRelative(face);
            Material adjacentBlockType = adjacentBlock.getType();

            // Check if the adjacent block is a stem
            if (adjacentBlockType != Material.ATTACHED_MELON_STEM && adjacentBlockType != Material.ATTACHED_PUMPKIN_STEM) continue;

            // Check the direction of the stem
            BlockData blockData = adjacentBlock.getBlockData();
            if (blockData instanceof Directional directional) {

                if (directional.getFacing().getOppositeFace() == face) {
                    Crop crop = new Crop(adjacentBlock.getLocation(), adjacentBlockType);
                    PLANTED_CROPS.put(adjacentBlock.getLocation(), crop);
                    return;
                }
            }
        }
    }

    /**
     * Grows any plants with persistence when a chunk loads.
     * @param event Fires when a chunk is loaded.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Iterator<Map.Entry<Location, Crop>> iterator = FarmingHandler.PLANTED_CROPS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Location, Crop> entry = iterator.next();
            Location location = entry.getKey();
            Crop crop = entry.getValue();

            if (chunk.equals(location.getChunk())) continue;
            Material cropMaterial = Material.valueOf(crop.getMaterial());

            if (cropMaterial == Material.SUGAR_CANE || cropMaterial == Material.CACTUS) {
                long currentTime = System.currentTimeMillis();
                double growthTime = BiomeData.getGrowthTime(crop);
                if (growthTime <= 0) continue;
                int elapsedCycles = (int) ((currentTime - crop.getTimePlanted().getTime()) / growthTime);

                if (elapsedCycles > 0) {
                    for (int i = 0; i < elapsedCycles && i < 2; i++) {
                        FarmingHandler.growCrop(crop);
                    }
                }
                crop.setTimePlanted(new Date());
                FarmingHandler.PLANTED_CROPS.put(location, crop);
            }
            else if (FarmingHandler.isReadyToGrow(crop)) {
                FarmingHandler.growCrop(crop);
                if (cropMaterial == Material.PUMPKIN_STEM) {
                    crop = new Crop(location, Material.PUMPKIN_STEM);
                    FarmingHandler.PLANTED_CROPS.put(location, crop);
                } else if (cropMaterial == Material.MELON_STEM) {
                    crop = new Crop(location, Material.MELON_STEM);
                    FarmingHandler.PLANTED_CROPS.put(location, crop);
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
        long currentTime = System.currentTimeMillis();
        double growthTime = BiomeData.getGrowthTime(crop);
        return currentTime - crop.getTimePlanted().getTime() >= growthTime;
    }

    public static void removeCrop(Location location) {
        FarmingHandler.PLANTED_CROPS.remove(location);
    }

    /**
     * Replaces a seed crop with a fully grown version.
     * @param crop the crop
     */
    public static void growCrop(Crop crop) {
        Block block = crop.getBlockCoord().asLocation().getBlock();
        Material cropType = block.getType();

        if (block.getBlockData() instanceof Ageable ageable) {
            // Check if the block is a mature Melon or Pumpkin stem
            if ((cropType == Material.MELON_STEM || cropType == Material.PUMPKIN_STEM) && ageable.getAge() == ageable.getMaximumAge()) {
                // Find a suitable location adjacent to the stem to place the Melon or Pumpkin block
                for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                    Block adjacentBlock = block.getRelative(face);
                    if (adjacentBlock.getType() == Material.AIR) {
                        // Grow fruit and turn stem to attached
                        if (cropType == Material.MELON_STEM) {
                            adjacentBlock.setType(Material.MELON);
                            block.setType(Material.ATTACHED_MELON_STEM);
                        } else {
                            adjacentBlock.setType(Material.PUMPKIN);
                            block.setType(Material.ATTACHED_PUMPKIN_STEM);
                        }
                        // Set direction for stem
                        BlockData blockData = block.getBlockData();
                        if (blockData instanceof Directional directional) {
                            directional.setFacing(face);
                            block.setBlockData(directional);
                        }
                        break;
                    }
                }
            } else {
                ageable.setAge(ageable.getMaximumAge());
                block.setBlockData(ageable);
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
    }
}
