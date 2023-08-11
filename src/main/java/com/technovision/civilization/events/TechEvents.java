package com.technovision.civilization.events;

import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.data.objects.Civilization;
import com.technovision.civilization.data.enums.Technology;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.PlayerInventory;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.*;

public class TechEvents implements Listener {

    private final CivilizationPlugin plugin;
    private final Map<Material, Technology> lockedRecipes;

    public TechEvents(CivilizationPlugin plugin) {
        this.plugin = plugin;
        this.lockedRecipes = new HashMap<>();
        // Ancient Era (1)
        lockedRecipes.put(Material.FISHING_ROD, Technology.FISHING);
        lockedRecipes.put(Material.BOW, Technology.ARCHERY);
        lockedRecipes.put(Material.ARROW, Technology.ARCHERY);
        lockedRecipes.put(Material.LEATHER_HELMET, Technology.ARCHERY);
        lockedRecipes.put(Material.LEATHER_CHESTPLATE, Technology.ARCHERY);
        lockedRecipes.put(Material.LEATHER_LEGGINGS, Technology.ARCHERY);
        lockedRecipes.put(Material.LEATHER_BOOTS, Technology.ARCHERY);
        lockedRecipes.put(Material.IRON_PICKAXE, Technology.MINING);
        lockedRecipes.put(Material.IRON_SHOVEL, Technology.MINING);
        lockedRecipes.put(Material.IRON_AXE, Technology.WOODCUTTING);
        lockedRecipes.put(Material.BREWING_STAND, Technology.BREWING);
        lockedRecipes.put(Material.CAULDRON, Technology.BREWING);
        lockedRecipes.put(Material.IRON_HOE, Technology.AGRICULTURE);

        // Classical Era (1)
        lockedRecipes.put(Material.IRON_SWORD, Technology.IRON_WORKING);
        lockedRecipes.put(Material.IRON_HELMET, Technology.IRON_WORKING);
        lockedRecipes.put(Material.IRON_CHESTPLATE, Technology.IRON_WORKING);
        lockedRecipes.put(Material.IRON_LEGGINGS, Technology.IRON_WORKING);
        lockedRecipes.put(Material.IRON_BOOTS, Technology.IRON_WORKING);
        lockedRecipes.put(Material.BIRCH_BOAT, Technology.SAILING);
        lockedRecipes.put(Material.ACACIA_BOAT, Technology.SAILING);
        lockedRecipes.put(Material.CHERRY_BOAT, Technology.SAILING);
        lockedRecipes.put(Material.DARK_OAK_BOAT, Technology.SAILING);
        lockedRecipes.put(Material.JUNGLE_BOAT, Technology.SAILING);
        lockedRecipes.put(Material.OAK_BOAT, Technology.SAILING);
        lockedRecipes.put(Material.MANGROVE_BOAT, Technology.SAILING);
        lockedRecipes.put(Material.SPRUCE_BOAT, Technology.SAILING);

        // Medieval Era (1)
        lockedRecipes.put(Material.BIRCH_CHEST_BOAT, Technology.NAVIGATION);
        lockedRecipes.put(Material.ACACIA_CHEST_BOAT, Technology.NAVIGATION);
        lockedRecipes.put(Material.CHERRY_CHEST_BOAT, Technology.NAVIGATION);
        lockedRecipes.put(Material.DARK_OAK_CHEST_BOAT, Technology.NAVIGATION);
        lockedRecipes.put(Material.JUNGLE_CHEST_BOAT, Technology.NAVIGATION);
        lockedRecipes.put(Material.OAK_CHEST_BOAT, Technology.NAVIGATION);
        lockedRecipes.put(Material.MANGROVE_CHEST_BOAT, Technology.NAVIGATION);
        lockedRecipes.put(Material.SPRUCE_CHEST_BOAT, Technology.NAVIGATION);
    }

    /**
     * Handles techs that involves brewing and potions.
     */
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory() instanceof BrewerInventory) ||
                !event.getRawSlots().contains(3) ||
                !(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        handleBrewerInteraction(player, event.getOldCursor().getType(), event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof BrewerInventory && event.getSlot() == 3) {
            if (event.getWhoClicked() instanceof Player player) {
                Material type = (event.getCursor() != null) ? event.getCursor().getType() : null;
                handleBrewerInteraction(player, type, event);
            }
        } else if (event.isShiftClick() && event.getClickedInventory() instanceof PlayerInventory) {
            if (event.getWhoClicked().getOpenInventory().getTopInventory() instanceof BrewerInventory) {
                if (event.getWhoClicked() instanceof Player player) {
                    handleBrewerInteraction(player, event.getCurrentItem().getType(), event);
                }
            }
        }
    }

    private void handleBrewerInteraction(Player player, Material type, Cancellable event) {
        if (type == Material.NETHER_WART || type == Material.GLISTERING_MELON_SLICE) {
            checkTechRequirement(player, Technology.BREWING, event);
        } else if (type != null) {
            event.setCancelled(true);
            CivMessage.sendError(player, "You do not have the right tech to use that ingredient!");
        }
    }

    /**
     * Handles techs that involve placing double chests
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() != Material.CHEST) return;
        Player player = event.getPlayer();
        Block placedBlock = event.getBlockPlaced();
        BlockFace playerFacing = player.getFacing();
        BlockFace left;
        BlockFace right;
        switch (playerFacing) {
            case NORTH -> {
                left = BlockFace.WEST;
                right = BlockFace.EAST;
            }
            case SOUTH -> {
                left = BlockFace.EAST;
                right = BlockFace.WEST;
            }
            case EAST -> {
                left = BlockFace.NORTH;
                right = BlockFace.SOUTH;
            }
            case WEST -> {
                left = BlockFace.SOUTH;
                right = BlockFace.NORTH;
            }
            default -> {
                return;
            }
        }
        if (placedBlock.getRelative(left).getType() == Material.CHEST || placedBlock.getRelative(right).getType() == Material.CHEST) {
            checkTechRequirement(event.getPlayer(), Technology.POTTERY, event);
        }
    }

    /**
     * Handles techs that involve using higher tier materials
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material toolType = event.getPlayer().getInventory().getItemInMainHand().getType();
        if (toolType == Material.AIR) return;
        if (lockedRecipes.containsKey(toolType)) {
            checkTechRequirement(event.getPlayer(), lockedRecipes.get(toolType), event);
        }
    }

    /**
     * Handles techs that involve crafting items.
     */
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        Material itemType = event.getRecipe().getResult().getType();
        if (lockedRecipes.containsKey(itemType)) {
            if (event.getWhoClicked() instanceof Player player) {
                checkTechRequirement(player, lockedRecipes.get(itemType), event);
            }
        }
    }

    /**
     * Handles horseback riding and mounting of horses.
     */
    @EventHandler
    public void onMount(EntityMountEvent event) {
        if (event.getMount() instanceof Player player) {
            EntityType entityType = event.getEntityType();
            if (entityType == EntityType.HORSE || entityType == EntityType.DONKEY || entityType == EntityType.MULE || entityType == EntityType.CAMEL) {
                checkTechRequirement(player, Technology.HORSEBACK_RIDING, event);
            }
        }
    }

    /**
     * Handles techs that involve breeding animals and horse taming.
     */
    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        EntityType entityType = event.getRightClicked().getType();
        Material itemType = player.getInventory().getItemInMainHand().getType();

        if (itemType == Material.WHEAT) {
            if (entityType == EntityType.SHEEP || entityType == EntityType.COW || entityType == EntityType.MUSHROOM_COW) {
                checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
            }
            if (entityType == EntityType.HORSE || entityType == EntityType.DONKEY || entityType == EntityType.MULE) {
                checkTechRequirement(player, Technology.HORSEBACK_RIDING, event);
            }
        }
        else if (itemType == Material.CARROT || itemType == Material.POTATO || itemType == Material.BEETROOT) {
            if (entityType == EntityType.PIG || entityType == EntityType.RABBIT) {
                checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
            }
        }
        else if (itemType == Material.DANDELION || itemType == Material.GOLDEN_CARROT) {
            if (entityType == EntityType.RABBIT) {
                checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
            }
            if (entityType == EntityType.HORSE || entityType == EntityType.DONKEY || entityType == EntityType.MULE) {
                checkTechRequirement(player, Technology.HORSEBACK_RIDING, event);
            }
        }
        else if (itemType == Material.BAMBOO) {
            if (entityType == EntityType.PANDA) {
                checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
            }
        }
        else if (itemType == Material.SEAGRASS) {
            if (entityType == EntityType.TURTLE) {
                checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
            }
        }
        else if (itemType == Material.SWEET_BERRIES) {
            if (entityType == EntityType.FOX) {
                checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
            }
        }
        else if (entityType == EntityType.BEE) {
            checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
        }
        else if (itemType == Material.HAY_BLOCK || itemType == Material.SUGAR || itemType == Material.APPLE || itemType == Material.GOLDEN_APPLE || itemType == Material.ENCHANTED_GOLDEN_APPLE) {
            if (entityType == EntityType.HORSE || entityType == EntityType.DONKEY || entityType == EntityType.MULE) {
                checkTechRequirement(player, Technology.HORSEBACK_RIDING, event);
            }
        }
        else if (itemType == Material.CACTUS) {
            if (entityType == EntityType.CAMEL) {
                checkTechRequirement(player, Technology.ANIMAL_HUSBANDRY, event);
            }
        }
    }

    /**
     * Handles techs that involve interacting with items and blocks.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material itemType = player.getInventory().getItemInMainHand().getType();
        Block clickedBlock = event.getClickedBlock();

        if (itemType == Material.BOW) {
            checkTechRequirement(player, Technology.ARCHERY, event);
        }
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && clickedBlock != null) {
            // Handle agriculture tech
            if (clickedBlock.getType() == Material.FARMLAND) {
                if (itemType == Material.WHEAT_SEEDS
                        || itemType == Material.CARROT || itemType == Material.POTATO
                        || itemType == Material.MELON || itemType == Material.PUMPKIN
                        || itemType == Material.BEETROOT_SEEDS || itemType == Material.TORCHFLOWER_SEEDS) {
                    checkTechRequirement(player, Technology.AGRICULTURE, event);
                }
            }
            // Handle wheel tech
            else if (clickedBlock.getType() == Material.GRASS_BLOCK) {
                if (itemType.name().endsWith("_SHOVEL")) {
                    checkTechRequirement(player, Technology.THE_WHEEL, event);
                }
            }
            // Handle navigation tech
            else if (lockedRecipes.containsKey(itemType) && lockedRecipes.get(itemType) == Technology.NAVIGATION) {
                checkTechRequirement(player, Technology.NAVIGATION, event);
            }
            // Handle sailing tech
            else if (lockedRecipes.containsKey(itemType) && lockedRecipes.get(itemType) == Technology.SAILING) {
                checkTechRequirement(player, Technology.SAILING, event);
            }
        }
    }

    /**
     * Requires 'fishing' technology for player to fish.
     */
    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        checkTechRequirement(player, Technology.FISHING, event);
    }

    private void checkTechRequirement(Player player, Technology requiredTech, Cancellable event) {
        Civilization civ = CivGlobal.getCivByPlayer(player);
        if (!civ.hasResearched(requiredTech)) {
            event.setCancelled(true);
            CivMessage.sendError(player, String.format(
                    "%sYou must research %s%s%s to do that.",
                    CivColor.Rose,
                    CivColor.Yellow,
                    requiredTech.getName(),
                    CivColor.Rose
            ));
        }
    }
}
