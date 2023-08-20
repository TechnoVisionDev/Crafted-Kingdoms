package com.technovision.craftedkingdoms.handlers.farming;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.BiomeData;
import com.technovision.craftedkingdoms.data.objects.Crop;
import com.technovision.craftedkingdoms.data.objects.FarmChunk;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.handlers.FortifyHandler;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

    private final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private final long COOLDOWN_TIME_MS = 500; // 0.5 seconds

    public static final Map<Chunk, Map<Location, Crop>> PLANTED_CROPS = new ConcurrentHashMap<>();

    public FarmingHandler() {
        // Get crops from database
        for (FarmChunk farmChunk : Database.CROPS.find()) {
            for (Crop crop : farmChunk.getCrops()) {
                Location cropLocation = crop.getBlockCoord().asLocation();
                addCrop(cropLocation, crop);
            }
        }
        Database.CROPS.deleteMany(new Document());

        // Start crop scanner to run every 10 minutes
        Bukkit.getServer().getScheduler().runTaskTimer(
                CraftedKingdoms.plugin,
                new CropGrowTask(), 0, 20 * 60 * 10);
    }

    /**
     * Handles custom breeding mechanics.
     * @param event Fires when a creature is born from breeding.
     */
    @EventHandler
    public void onAnimalBreeding(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BREEDING) return;
        Biome biome = event.getEntity().getLocation().getBlock().getBiome();
        EntityType type = event.getEntityType();

        double breedingChance = BiomeData.getBreedingChance(biome, type);
        if (breedingChance <= 0) {
            event.setCancelled(true);
            return;
        }

        Random random = new Random();
        double randomNumber = random.nextDouble();

        if (randomNumber > breedingChance) {
            // Breeding is unsuccessful
            event.setCancelled(true);
        }
    }

    /**
     * Removes experience from breeding animals.
     * @param event Fires when breeding is performed.
     */
    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        event.setExperience(0);
    }

    /**
     * Requires select biomes for fishing & removes treasure.
     * @param event Fires when fishing is performed.
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        Biome biome = event.getPlayer().getLocation().getBlock().getBiome();
        double chance = BiomeData.getBreedingChance(biome, EntityType.FISHING_HOOK);

        if (chance < 1.0) {
            event.setCancelled(true);
            MessageUtils.sendError(player, "You cannot catch fish in this biome!");
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item caughtItem) {
            ItemStack item = caughtItem.getItemStack();

            // Check if the caught item is a fish
            switch (item.getType()) {
                case COD, SALMON, TROPICAL_FISH, PUFFERFISH -> { }
                default -> {
                    // Give random fish instead of junk or treasure
                    caughtItem.setItemStack(new ItemStack(Material.KELP));
                }
            }
        }
    }

    /**
     * Cancels vanilla growth mechanics (except for vines).
     * @param event Fires when crop is supposed to grow.
     */
    @EventHandler
    public void onCropGrow(BlockGrowEvent event) {
        if (event.getNewState().getType() != Material.VINE) {
            event.setCancelled(true);
        }
    }

    /**
     * Right-clicking an animal with a stick will tell you the breeding chance.
     * @param event Fires when player right clicks animal with a stick.
     */
    @EventHandler
    public void onAnimalRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.STICK && event.getRightClicked() instanceof Animals) {
            // Check if the player is in the cooldown map and if the cooldown has expired
            if (COOLDOWNS.containsKey(playerUUID) && System.currentTimeMillis() - COOLDOWNS.get(playerUUID) < COOLDOWN_TIME_MS) {
                return;
            }
            EntityType type = event.getRightClicked().getType();
            double breedingChance = BiomeData.getBreedingChance(player.getLocation().getBlock().getBiome(), type);

            // Send message and cooldown player
            player.sendMessage(ChatColor.GOLD + "That animal has a " + (breedingChance * 100) + "% breeding rate in this biome.");
            COOLDOWNS.put(playerUUID, System.currentTimeMillis());
        }
    }

    /**
     * Adds a crop to crop scanner and database when planted.
     * @param event Fires when player plants a crop.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getItem();
            if (itemInHand == null) return;

            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;
            if (!BiomeData.isSeed(itemInHand.getType())) return;

            Block targetBlock;
            if (itemInHand.getType() == Material.COCOA_BEANS) {
                // Find the face of the log that is closest to the player
                BlockFace closestFace = getClosestFace(clickedBlock, event.getPlayer());
                targetBlock = clickedBlock.getRelative(closestFace);
            } else {
                // For other crops, assume they would be planted in the block above the clicked block
                targetBlock = clickedBlock.getRelative(0, 1, 0);
            }

            // Calculate growth time for crop
            Crop crop = new Crop(targetBlock.getLocation(), itemInHand.getType());
            double growthTimeInHours = BiomeData.getGrowthTime(crop) / 60.0 / 60.0 / 1000.0;
            int hours = (int) growthTimeInHours;
            int minutes = (int) Math.round((growthTimeInHours - hours) * 60);
            if (minutes == 60) {
                hours++;
                minutes = 0;
            }

            // Send message to player
            String itemName = StringUtils.stringifyType(itemInHand.getType());
            if (growthTimeInHours <= 0) {
                MessageUtils.send(event.getPlayer(), ChatColor.GOLD + itemName + " can't grow here!");
            } else {
                MessageUtils.send(event.getPlayer(), ChatColor.GOLD + itemName + " would grow here in " + hours + "h " + minutes + "m");
            }
        }
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getItem();
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null || itemInHand == null) return;

            // Plant new crop
            Material itemType = itemInHand.getType();
            if (BiomeData.isSeed(itemType)) {
                if (itemType != Material.CACTUS && itemType != Material.SUGAR_CANE && itemType != Material.COCOA_BEANS) {
                    Location cropLocation = clickedBlock.getRelative(BlockFace.UP).getLocation();
                    Crop crop = new Crop(cropLocation, itemType);
                    if (BiomeData.isValidCropLocation(crop, clickedBlock)) {
                        addCrop(cropLocation, crop);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerUseStickOrBone(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack itemInHand = event.getItem();
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || itemInHand == null) return;

        Location blockLocation = clickedBlock.getLocation();
        Chunk chunk = blockLocation.getChunk();

        Map<Location, Crop> chunkCrops = PLANTED_CROPS.get(chunk);

        // Check crop stats
        if (itemInHand.getType() == Material.STICK) {
            if (chunkCrops == null) return;
            Crop crop = chunkCrops.get(blockLocation);
            if (crop == null) return;
            MessageUtils.send(event.getPlayer(), ChatColor.GOLD + "This crop will be ready to harvest in " + getTimeRemaining(crop));
        }
        // Handle bone meal on crop
        else if (itemInHand.getType() == Material.BONE_MEAL) {
            if (!BiomeData.isCrop(clickedBlock.getType())) return;
            event.setCancelled(true);
            //TODO: Add custom bone meal function
        }
    }

    private BlockFace getClosestFace(Block block, Player player) {
        Location blockLocation = block.getLocation().add(0.5, 0.5, 0.5); // Center of the block
        Location playerLocation = player.getLocation();

        org.bukkit.util.Vector blockToPlayer = playerLocation.toVector().subtract(blockLocation.toVector());
        double maxDot = -Double.MAX_VALUE;
        BlockFace closestFace = BlockFace.SELF;

        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            org.bukkit.util.Vector faceDirection = new org.bukkit.util.Vector(face.getModX(), face.getModY(), face.getModZ());
            double dot = blockToPlayer.normalize().dot(faceDirection);
            if (dot > maxDot) {
                maxDot = dot;
                closestFace = face;
            }
        }
        return closestFace;
    }

    /**
     * Adds block crops (cactus and sugar cane) to crop scanner and database when planted.
     * @param event Fires when player places cactus or sugar cane.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();
        Block placedBlock = event.getBlockPlaced();

        Material blockType = placedBlock.getType();
        if (BiomeData.BLOCKED_CROPS.contains(blockType)) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getPlayer(), "You cannot grow that crop!");
            return;
        }

        Material itemType = itemInHand.getType();
        if (itemType == Material.CACTUS || itemType == Material.SUGAR_CANE || itemType == Material.COCOA_BEANS) {
            while (placedBlock.getRelative(BlockFace.DOWN).getType() == itemType) {
                placedBlock = placedBlock.getRelative(BlockFace.DOWN);
            }
            Location cropLocation = placedBlock.getLocation();
            addCrop(cropLocation, new Crop(cropLocation, itemType));
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
                    addCrop(adjacentBlock.getLocation(), crop);
                    return;
                }
            }
        }
    }

    /**
     * Prevents crops from being trampled by entity.
     * @param event Fires when entity tramples a crop.
     */
    @EventHandler
    public void onEntityTrampleCrop(EntityChangeBlockEvent event) {
        if (event.getBlock().getType() != Material.FARMLAND) return;
        event.setCancelled(true);
    }

    /**
     * Remove crops that have faded away
     * @param event Fires when crop fades.
     */
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        Location location = event.getBlock().getLocation();
        Crop crop = getCrop(location.add(0, 1, 0));
        if (crop == null) return;
        removeCrop(location);
    }

    /**
     * Grows any plants with persistence when a chunk loads.
     * @param event Fires when a chunk is loaded.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Map<Location, Crop> chunkCrops = PLANTED_CROPS.get(chunk);
        if (chunkCrops == null) return;

        Iterator<Map.Entry<Location, Crop>> iterator = chunkCrops.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Location, Crop> entry = iterator.next();
            Location location = entry.getKey();
            Crop crop = entry.getValue();

            // Check if the location is in the chunk of the event
            int locChunkX = location.getBlockX() >> 4;
            int locChunkZ = location.getBlockZ() >> 4;
            if (locChunkX != chunk.getX() || locChunkZ != chunk.getZ()) continue;

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
                addCrop(location, crop);
            }
            else if (FarmingHandler.isReadyToGrow(crop)) {
                FarmingHandler.growCrop(crop);
                if (cropMaterial == Material.PUMPKIN_STEM) {
                    crop = new Crop(location, Material.PUMPKIN_STEM);
                    addCrop(location, crop);
                } else if (cropMaterial == Material.MELON_STEM) {
                    crop = new Crop(location, Material.MELON_STEM);
                    addCrop(location, crop);
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
        if (growthTime < 0) return false;
        return currentTime - crop.getTimePlanted().getTime() >= growthTime;
    }

    /**
     * Gets the time remaining for a crop to grow.
     * @param crop the crop to check.
     * @return String of hours and minutes remaining to grow.
     */
    public static String getTimeRemaining(Crop crop) {
        long currentTime = System.currentTimeMillis();
        double growthTime = BiomeData.getGrowthTime(crop);
        long timePlanted = crop.getTimePlanted().getTime();

        // Calculate the remaining time in milliseconds
        long remainingTime = (long) (growthTime - (currentTime - timePlanted));
        // Check if the remaining time is negative, in which case the crop is ready to grow
        if (remainingTime <= 0) {
            return "0h 0m";
        }
        // Convert remaining time to minutes, rounding up if necessary
        long remainingMinutes = (remainingTime + 59999) / 60000;

        // Convert the remaining minutes into hours and minutes
        long hours = remainingMinutes / 60;
        long minutes = remainingMinutes % 60;
        return hours + "h " + minutes + "m";
    }

    public static Crop getCrop(Location location) {
        Chunk chunk = location.getChunk();
        Map<Location,Crop> crops = PLANTED_CROPS.get(chunk);
        if (crops == null) return null;
        return crops.get(location);
    }

    public static void addCrop(Location location, Crop crop) {
        Chunk chunk = location.getChunk();
        Map<Location, Crop> chunkCrops = FarmingHandler.PLANTED_CROPS.computeIfAbsent(chunk, k -> new HashMap<>());
        chunkCrops.put(location, crop);

        FortifiedBlock belowBlock = CKGlobal.getFortifiedBlock(location.add(0, -1, 0));
        if (belowBlock == null) return;
        FortifyHandler.fortifyCrop(belowBlock, location.add(0, 1, 0));
    }

    public static void removeCrop(Location location) {
        Chunk chunk = location.getChunk();
        Map<Location, Crop> chunkCrops = FarmingHandler.PLANTED_CROPS.get(chunk);
        if (chunkCrops != null) {
            chunkCrops.remove(location);
            if (chunkCrops.isEmpty()) {
                FarmingHandler.PLANTED_CROPS.remove(chunk);
            }
        }
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
        for (Map.Entry<Chunk, Map<Location, Crop>> chunkEntry : FarmingHandler.PLANTED_CROPS.entrySet()) {

            Chunk chunk = chunkEntry.getKey();
            Set<Crop> crops = new HashSet<>();
            for (Map.Entry<Location, Crop> locationCropEntry : chunkEntry.getValue().entrySet()) {
                crops.add(locationCropEntry.getValue());
            }

            FarmChunk farmChunk = new FarmChunk(chunk, crops);
            Database.CROPS.insertOne(farmChunk);
        }
    }
}
