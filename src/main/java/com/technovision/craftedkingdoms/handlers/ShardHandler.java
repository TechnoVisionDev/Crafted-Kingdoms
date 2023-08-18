package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles prison ender pearl events
 *
 * @author TechnoVision
 */
public class ShardHandler implements Listener {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

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
                if (isPearled(killedPlayer)) {
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
                resident.pearlPlayer();

                // Create the shard item
                ItemStack exilePearl = createExilePearl(killer, killedPlayer);

                // Attempt to add the shard to the killer's inventory
                HashMap<Integer, ItemStack> remainingItems = killer.getInventory().addItem(exilePearl);

                // Check if the shard was successfully added to the inventory
                if (!remainingItems.isEmpty()) {
                    // Drop the shard at the killer's location
                    killer.getWorld().dropItemNaturally(killer.getLocation(), exilePearl);
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
        if (res.isPearled()) {
            Location respawn = findSafeNetherRespawn(player);
            if (respawn == null) {
                MessageUtils.sendError(player, "Failed to find safe spawn point in Nether! Contact an admin!");
                return;
            }
            event.setRespawnLocation(respawn);
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

    public static boolean isPearled(Player player) {
        Resident resident = CKGlobal.getResident(player);
        return resident.isPearled();
    }

    public boolean isSoulShard(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(
                new NamespacedKey(CraftedKingdoms.namespace, "soulshard"),
                PersistentDataType.STRING
        );
    }

    public void freeSoulShard(ItemStack exilePearl) {
        if (exilePearl == null || !exilePearl.hasItemMeta()) return;

        // Get the UUID string stored in the item's metadata
        ItemMeta itemMeta = exilePearl.getItemMeta();
        String uuidString = itemMeta.getPersistentDataContainer().get(
                new NamespacedKey(CraftedKingdoms.plugin, "soulshard"),
                PersistentDataType.STRING
        );

        // Check if the UUID string is not null
        if (uuidString == null || uuidString.isEmpty()) {
            return;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        // Free the resident associated with the UUID
        Resident resident = CKGlobal.getResident(uuid);
        if (resident == null) return;
        resident.freePlayer();
    }

    public Location findSafeNetherRespawn(Player player) {
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

    private int findSafeY(World world, int x, int z) {
        for (int y = 120; y > 30; y--) {
            if (isSafeSpawn(world, x, y, z)) {
                return y;
            }
        }
        return -1;
    }

    private boolean isSafeSpawn(World world, int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType().isSolid() &&
                world.getBlockAt(x, y + 1, z).getType() == Material.AIR &&
                world.getBlockAt(x, y + 2, z).getType() == Material.AIR;
    }

    public ItemStack createExilePearl(Player killer, Player killedPlayer) {
        // Create a new ItemStack of Material ENDER_PEARL (1 ender pearl)
        ItemStack exilePearl = new ItemStack(Material.ENDER_EYE, 1);
        ItemMeta meta = exilePearl.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.ITALIC + killedPlayer.getName());

        // Set meta data
        meta.getPersistentDataContainer().set(
                new NamespacedKey(CraftedKingdoms.namespace, "soulshard"),
                PersistentDataType.STRING,
                killedPlayer.getUniqueId().toString()
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
        exilePearl.setItemMeta(meta);

        // Return the exilePearl item
        return exilePearl;
    }
}
