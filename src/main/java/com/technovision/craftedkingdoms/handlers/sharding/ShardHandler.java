package com.technovision.craftedkingdoms.handlers.sharding;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Hopper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles prison ender pearl events
 *
 * @author TechnoVision
 */
public class ShardHandler implements Listener {

    public static final int HOURS_PER_WEEK = 168;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public ShardHandler() {
        // Created soulshard upkeep recipe
        NamespacedKey key = new NamespacedKey(CraftedKingdoms.plugin, "soulshard_upkeep");
        ItemStack dummySoulShard = new ItemStack(Material.ENDER_EYE, 1);
        ShapelessRecipe recipe = new ShapelessRecipe(key, dummySoulShard);
        recipe.addIngredient(new RecipeChoice.ExactChoice(EssenceHandler.ESSENCE));
        recipe.addIngredient(Material.ENDER_PEARL);
        Bukkit.addRecipe(recipe);

        // Schedule shard scanner to run every hour
        Bukkit.getScheduler().runTaskTimer(CraftedKingdoms.plugin,
                new ShardUpkeepTask(), 0, 20 * 60 * 60);
    }

    /**
     * Pearls a player if they die while killer has enderpearl in hotbar.
     * @param event Fires when player kills another with enderpearl in hotbar.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killedPlayer = event.getEntity();
        Player killer = killedPlayer.getKiller();
        if (killer == null) {
            return;
        }
        // Check for ender pearl in hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack item = killer.getInventory().getItem(i);
            if (item != null && item.getType() == Material.ENDER_PEARL) {
                // Check if player is already pearled
                if (isSharded(killedPlayer)) {
                    MessageUtils.sendError(killer, "That player's soul is already trapped in a soul shard!");
                    return;
                }
                // Use up the pearl
                if (item.getAmount() == 1) {
                    killer.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
                // Flag player for teleporting on respawn
                Resident resident = CKGlobal.getResident(killedPlayer);
                resident.shardPlayer(killer);

                // Create the shard item
                ItemStack soulShard = createSoulShard(killer, killedPlayer);

                // Attempt to add the shard to the killer's inventory
                HashMap<Integer, ItemStack> remainingItems = killer.getInventory().addItem(soulShard);

                // Check if the shard was successfully added to the inventory
                if (!remainingItems.isEmpty()) {
                    // Drop the shard at the killer's location
                    killer.getWorld().dropItemNaturally(killer.getLocation(), soulShard);
                }
                break;
            }
        }
    }

    /**
     * Makes pearled player respawn in nether.
     * @param event Fires when pearled player respawns.
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Resident res = CKGlobal.getResident(player);
        if (res.getSoulShard() != null) {
            // Spawn at anchor if set
            if (event.isAnchorSpawn()) return;

            // Find safe random nether spawn
            Location respawn = findSafeNetherRespawn(player);
            if (respawn == null) {
                // Try to find safe spawn again
                respawn = findSafeNetherRespawn(player);
                if (respawn == null) {
                    MessageUtils.sendError(player, "Failed to find safe spawn point in Nether! Contact an admin!");
                    return;
                }
            }
            event.setRespawnLocation(respawn);
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack[] matrix = event.getInventory().getMatrix();
        ItemStack soulShard = null;
        boolean hasEnderPearl = false;
        for (ItemStack item : matrix) {
            if (item != null) {
                if (item.getType() == Material.ENDER_EYE && isSoulShard(item)) {
                    soulShard = item;
                } else if (item.getType() == Material.ENDER_PEARL) {
                    hasEnderPearl = true;
                }
            }
        }
        if (soulShard != null && hasEnderPearl) {
            ItemStack result = soulShard.clone();
            addUpkeepHours(result, 42);
            event.getInventory().setResult(result);
        }
    }

    private void addUpkeepHours(ItemStack soulShard, int hours) {
        // Get the PersistentDataContainer of the Exile Pearl
        PersistentDataContainer data = soulShard.getItemMeta().getPersistentDataContainer();

        // Get the current upkeep value
        NamespacedKey key = new NamespacedKey(CraftedKingdoms.plugin, "upkeep");
        int currentUpkeep = data.getOrDefault(key, PersistentDataType.INTEGER, 0);

        // Add the specified number of hours (converted to minutes)
        int newUpkeep = currentUpkeep + hours;
        if (newUpkeep > HOURS_PER_WEEK) newUpkeep = HOURS_PER_WEEK;

        // Save the updated metadata back to the Exile Pearl
        ItemMeta meta = soulShard.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, newUpkeep);

        List<String> lore = meta.getLore();
        long percent = Math.round(((double) newUpkeep / ShardHandler.HOURS_PER_WEEK) * 100);
        lore.set(2, ChatColor.GOLD + "Health: " + ChatColor.GRAY + percent + "%");
        meta.setLore(lore);

        soulShard.setItemMeta(meta);
    }

    public static boolean isSharded(Player player) {
        Resident resident = CKGlobal.getResident(player);
        return resident.getSoulShard() != null;
    }

    public static boolean isSoulShard(ItemStack item) {
        if (item == null || !item.hasItemMeta() || item.getType() != Material.ENDER_EYE) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(
                new NamespacedKey(CraftedKingdoms.namespace, "soulshard"),
                PersistentDataType.STRING
        );
    }

    public static Resident getResidentFromShard(ItemStack shard) {
        if (shard == null || !shard.hasItemMeta()) return null;

        // Get the UUID string stored in the item's metadata
        ItemMeta itemMeta = shard.getItemMeta();
        String uuidString = itemMeta.getPersistentDataContainer().get(
                new NamespacedKey(CraftedKingdoms.plugin, "soulshard"),
                PersistentDataType.STRING
        );

        // Check if the UUID string is not null
        if (uuidString == null || uuidString.isEmpty()) {
            return null;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }

        // Free the resident associated with the UUID
        return CKGlobal.getResident(uuid);
    }

    public void freeSoulShard(ItemStack shard) {
        Resident resident = getResidentFromShard(shard);
        if (resident != null) {
            resident.freePlayer(shard);
        }
    }

    public static Location findSafeNetherRespawn(Player player) {
        World nether = player.getServer().getWorld("world_nether");
        Random random = new Random();
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = random.nextInt(5000) - 2500;
            int z = random.nextInt(5000) - 2500;
            int y = findSafeY(nether, x, z);
            if (y != -1) {
                return new Location(nether, x, y, z);
            }
        }
        return null;
    }

    private static int findSafeY(World world, int x, int z) {
        for (int y = 120; y > 30; y--) {
            if (isSafeSpawn(world, x, y, z)) {
                return y;
            }
        }
        return -1;
    }

    private static boolean isSafeSpawn(World world, int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType().isSolid() &&
                world.getBlockAt(x, y + 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 2, z).getType() == Material.AIR;
    }

    public ItemStack createSoulShard(Player killer, Player killedPlayer) {
        // Create a new ItemStack of Material ENDER_PEARL (1 ender pearl)
        ItemStack soulShard = new ItemStack(Material.ENDER_EYE, 1);
        ItemMeta meta = soulShard.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.ITALIC + killedPlayer.getName());

        // Set meta data
        meta.getPersistentDataContainer().set(
                new NamespacedKey(CraftedKingdoms.namespace, "soulshard"),
                PersistentDataType.STRING,
                killedPlayer.getUniqueId().toString()
        );
        meta.getPersistentDataContainer().set(
                new NamespacedKey(CraftedKingdoms.namespace, "upkeep"),
                PersistentDataType.INTEGER,
                HOURS_PER_WEEK
        );

        // Set item lore
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Soul Shard");
        lore.add(ChatColor.GOLD + "Player: " + ChatColor.GRAY + killedPlayer.getName());
        lore.add(ChatColor.GOLD + "Health: " + ChatColor.GRAY + "100%");
        lore.add(ChatColor.GOLD + "Exiled On: " + ChatColor.GRAY + dateFormat.format(new Date()));
        lore.add(ChatColor.GOLD + "Killed By: " + ChatColor.GRAY + killer.getName());
        lore.add(ChatColor.GOLD + "Upkeep Cost Per Week: " + ChatColor.GRAY + "4 Essence");
        meta.setLore(lore);

        // Add an enchantment to the item
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        soulShard.setItemMeta(meta);

        // Return the soul shard item
        return soulShard;
    }

    /** Restrict sharded players */

    @EventHandler
    public void onPlayerUsePortal(PlayerPortalEvent event) {
        Resident resident = CKGlobal.getResident(event.getPlayer());
        if (resident.getSoulShard() != null) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getPlayer(), "You can't leave the nether until your soul is freed!");
        }
    }

    /** Handle soul shard movement */

    @EventHandler
    public void preventThrowingShard(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getType() == Material.ENDER_EYE) {
            if (isSoulShard(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        // Check if the entity being damaged is a player
        if (event.getEntity() instanceof Player) {
            Player damaged = (Player) event.getEntity();

            // Check if the damager is also a player (i.e., PvP)
            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();

                // If both entities are in the Overworld, cancel the event
                if (damaged.getWorld().getEnvironment() == World.Environment.NORMAL) {

                    Resident res = CKGlobal.getResident(damager);
                    if (res.getSoulShard() == null) return;

                    event.setCancelled(true);
                    MessageUtils.sendError(damager, "You cannot PVP here while sharded!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            if (isSoulShard(item)) {
                // Drop the Exile Pearl at the player's location
                Location dropLocation = player.getLocation();
                player.getWorld().dropItemNaturally(dropLocation, item);

                // Remove the Exile Pearl from the player's inventory
                player.getInventory().remove(item);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Item) {
            ItemStack item = ((Item) event.getEntity()).getItemStack();
            if (isSoulShard(item)) {
                freeSoulShard(item);
            }
        }
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        ItemStack item = event.getEntity().getItemStack();
        if (isSoulShard(item)) {
            freeSoulShard(item);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack item = event.getItem().getItemStack();
        if (!isSoulShard(item)) return;

        Resident resident = getResidentFromShard(item);
        resident.moveShardToPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Item droppedItem = event.getItemDrop();
        ItemStack itemStack = droppedItem.getItemStack();
        if (!isSoulShard(itemStack)) return;

        Resident resident = getResidentFromShard(itemStack);
        new BukkitRunnable() {
            int secondsPassed = 0; // Counter to keep track of time
            @Override
            public void run() {
                // Check if the item has landed or despawned
                if (resident.getSoulShard() == null || secondsPassed >= 60) {
                    this.cancel();
                }
                else if (droppedItem.isOnGround() || droppedItem.isDead()) {
                    // If the item has landed or despawned, record its location and then cancel the task
                    resident.moveShardToLocation(droppedItem.getLocation());
                    this.cancel();
                }
                secondsPassed++; // Increment the counter
            }
        }.runTaskTimer(CraftedKingdoms.plugin, 20L, 20L); // Start after 20 ticks (1 second), repeat every 20 ticks (1 second)
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        boolean isCurrentItemShard = isSoulShard(currentItem);
        boolean isCursorItemShard = isSoulShard(cursorItem);

        Resident resident = null;
        if (isCurrentItemShard) {
            resident = getResidentFromShard(currentItem);
        } else if (isCursorItemShard) {
            resident = getResidentFromShard(cursorItem);
        }

        if (resident == null) return;

        // Handling manual movement
        if (isCursorItemShard) {
            if (clickedInventory.getType() != InventoryType.PLAYER) {
                InventoryHolder holder = clickedInventory.getHolder();
                if (holder instanceof Container) {
                    Block block = ((Container) holder).getBlock();
                    Location blockLocation = block.getLocation();
                    resident.moveShardToLocation(blockLocation);
                }
            } else if (clickedInventory.getType() == InventoryType.PLAYER) {
                resident.moveShardToPlayer(player.getUniqueId());
            }
        }
        // Handling shift-click movement
        else if (isCurrentItemShard && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            Inventory topInventory = event.getView().getTopInventory();
            InventoryHolder holder = topInventory.getHolder();

            if (clickedInventory.getType() == InventoryType.PLAYER && holder instanceof Container) {
                Block block = ((Container) holder).getBlock();
                Location blockLocation = block.getLocation();
                resident.moveShardToLocation(blockLocation);
            } else {
                resident.moveShardToPlayer(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        ItemStack item = event.getItem();
        if (!isSoulShard(item)) return;

        Resident resident = getResidentFromShard(item);
        resident.moveShardToLocation(event.getDestination().getLocation());
    }

    /**
     * Prevent hoppers from picking up soul shards.
     * @param event Fires when a hopper tries to pick up a soul shard.
     */
    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Hopper) {
            ItemStack itemStack = event.getItem().getItemStack();
            if (isSoulShard(itemStack)) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Prevent mobs from picking up soul shards
     * @param event Fires when a mob picks up an item.
     */
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            ItemStack item = event.getItem().getItemStack();
            if (isSoulShard(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeathDrop(PlayerDeathEvent event) {
        Player player = event.getEntity();
        for (ItemStack item : event.getDrops()) {
            if (!isSoulShard(item)) continue;

            Resident resident = getResidentFromShard(item);
            resident.moveShardToLocation(player.getLocation());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Container container)) return;

        for (ItemStack item : container.getInventory().getContents()) {
            if (item == null || !isSoulShard(item)) continue;

            Resident resident = getResidentFromShard(item);
            resident.moveShardToLocation(block.getLocation());
        }
    }
}
