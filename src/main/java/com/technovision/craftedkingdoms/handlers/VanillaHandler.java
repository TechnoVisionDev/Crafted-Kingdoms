package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Event handlers that disable or modify core elements of vanilla Minecraft.
 *
 * @author TechnoVision
 */
public class VanillaHandler implements Listener {

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
     * Disables the wither from being spawned.
     * @param event fires when player creates a wither.
     */
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.WITHER) {
            event.setCancelled(true);
        }
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
}
