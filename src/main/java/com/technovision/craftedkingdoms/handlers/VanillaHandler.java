package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Event handlers that disable or modify core elements of vanilla Minecraft.
 *
 * @author TechnoVision
 */
public class VanillaHandler implements Listener {

    private static final Random random = new Random();
    private static final World overworld = Bukkit.getWorld("world");

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        event.setQuitMessage(ChatColor.WHITE + playerName + ChatColor.YELLOW + " left the game");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String deathMessage = event.getDeathMessage();

        // Send the death message directly to the player
        if (deathMessage != null && !deathMessage.isEmpty()) {
            event.getEntity().sendMessage(ChatColor.GRAY + deathMessage);
        }
        // Prevent the death message from being broadcasted to all players
        event.setDeathMessage(null);
    }

    /**
     * Makes player spawn in random spot if no bed is set.
     * @param event Fires when player respawns.
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // Check if the respawn is not due to a bed
        Resident res = CKGlobal.getResident(event.getPlayer());
        if (res.getSoulShard() == null && !event.isBedSpawn()) {
            Location randomSpawnLocation = getRandomSpawnLocation(overworld);
            event.setRespawnLocation(randomSpawnLocation);
            MessageUtils.send(event.getPlayer(), ChatColor.GRAY + "You wake up in a strange and mysterious place...");
        }
    }

    /**
     * Makes player spawn in random spot when first joining.
     * @param event Fires when player joins server for first time.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(ChatColor.WHITE + player.getName() + ChatColor.YELLOW + " joined the game");
        if (!player.hasPlayedBefore()) {
            // Player has never played before, set a random spawn location
            Location randomSpawnLocation = getRandomSpawnLocation(overworld);
            player.teleport(randomSpawnLocation);
            MessageUtils.send(event.getPlayer(), ChatColor.GRAY + "You wake up in a strange and mysterious place...");
        }
    }

    /**
     * Finds a save spot to teleport for random spawn.
     * @param world the world to teleport in.
     * @return the safe spawn location.
     */
    private Location getRandomSpawnLocation(World world) {
        Location location = null;
        do {
            // Randomly calculate the X and Z coordinates within a 5000x5000 block area (from -5000 to 5000)
            int x = random.nextInt(10001) - 5000;
            int z = random.nextInt(10001) - 5000;

            // Assume the world's sea level is 64. Adjust as needed or use a more dynamic height detection.
            int y = world.getHighestBlockYAt(x, z); // This gets the highest non-air block at the x, z location
            location = new Location(world, x, y, z);

        } while (isOceanBiome(location));
        return location;
    }

    private boolean isOceanBiome(Location location) {
        return switch (location.getBlock().getBiome()) {
            case RIVER, OCEAN, DEEP_OCEAN, WARM_OCEAN, LUKEWARM_OCEAN, COLD_OCEAN, DEEP_LUKEWARM_OCEAN, DEEP_COLD_OCEAN, FROZEN_OCEAN ->
                    true;
            default -> false;
        };
    }

    /**
     * Makes the overworld and nether ratio 1:1 for portal travel.
     * @param event fires when player travels through nether portal
     */
    @EventHandler
    public void onPortalTravel(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            Location from = event.getFrom();
            Location to = (from.getWorld().getEnvironment() == World.Environment.NETHER) ?
                    Bukkit.getServer().getWorld("world").getSpawnLocation() :
                    Bukkit.getServer().getWorld("world_nether").getSpawnLocation();

            to.setX(from.getX());
            to.setY(from.getY());
            to.setZ(from.getZ());
            event.setTo(to);
        }
    }

    /**
     * Disables creating portals to nether and end (they will be manually placed).
     * @param event fires when player creates a portal.
     */
    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Disables villager trading.
     * @param event fires when player right clicks on villager.
     */
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getPlayer(), "Villager trading has been disabled!");
        }
    }

    /**
     * Disables enderman grabbing blocks.
     * @param event fires when player right clicks on villager.
     */
    @EventHandler
    public void onEndermanChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Enderman) {
            event.setCancelled(true);
        }
    }

    /**
     * Disable ender chest crafting.
     * @param event Fires when ender chest is crafted.
     */
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack item = event.getRecipe().getResult();

        // Check if the item is an Ender Chest
        if (item.getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getWhoClicked(), "Ender chests have been disabled!");
        }
    }

    /**
     * Disables the wither, dragon, pillager, patrols, & phantom from being spawned.
     * @param event fires when player creates a wither.
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.PATROL) {
            event.setCancelled(true);
        }
        else if (event.getEntityType() == EntityType.WITHER
                || event.getEntityType() == EntityType.PHANTOM
                || event.getEntityType() == EntityType.ENDER_DRAGON
        ) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent pillager raids.
     * @param event Fires when pillager raid begins.
     */
    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        event.setCancelled(true);
    }

    /**
     * Disables the use of elytras to fly or glide.
     * @param event fires when player tries to use an elytra.
     */
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        ItemStack chestplate = player.getInventory().getChestplate();

        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getPlayer(), "Elytra use has been disabled!");
        }
    }

    /**
     * Makes enderpearls much weaker by lowering their velocity when thrown.
     * @param event fires when player throws an enderpearl.
     */
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            EnderPearl pearl = (EnderPearl) event.getEntity();

            // Reduce the velocity by 60%
            Vector velocity = pearl.getVelocity();
            pearl.setVelocity(velocity.multiply(0.40));
        }
    }

    /**
     * Disables chorus fruit teleportation.
     * @param event Fires when player uses chorus fruit to teleport.
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents players from destroying end portal frames.
     * @param event Fires when player tries to break end portal frame.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.END_PORTAL_FRAME) {
            event.setCancelled(true);
            MessageUtils.sendError(event.getPlayer(), "You cannot break end portal frames!");
        }
    }

    /**
     * Makes god apples act as normal golden apples.
     * @param event Fires when player eats a god apple.
     */
    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            Bukkit.getScheduler().runTaskLater(CraftedKingdoms.plugin, () -> {
                // Cancel the default effects of the enchanted golden apple
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.ABSORPTION);

                // Apply the effects of a regular golden apple
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0));
            }, 1L);
        }
    }

    /** Disable Vanilla XP & Mob Farming */

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if (event.getItem().getType() == Material.EXPERIENCE_BOTTLE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        if (event.getItem().getType() == Material.BOOK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onUseXPBottle(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.EXPERIENCE_BOTTLE) {
                event.setCancelled(true);
                item.setAmount(item.getAmount()-1);
                event.getPlayer().giveExp(10);
                MessageUtils.send(event.getPlayer(), ChatColor.GREEN + "You have gained " + ChatColor.YELLOW + "10 Exp");
            }
        }
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);
    }

    @EventHandler
    public void disableOreXP(BlockBreakEvent event) {
        event.setExpToDrop(0);
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        event.setExpToDrop(0);
    }

}
