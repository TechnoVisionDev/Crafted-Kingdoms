package com.technovision.civilization.events;

import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.managers.ItemManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemEvents implements Listener {

    private final CivilizationPlugin plugin;
    private static final long COOLDOWN = 500; // Cooldown in milliseconds.
    private final Map<Player, Long> cooldowns = new HashMap<>();

    public ItemEvents(CivilizationPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Block clickedBlock = event.getClickedBlock();

        // Check if player is not sneaking and clicked block is interactable
        boolean isBlockInteractable = clickedBlock != null && clickedBlock.getType().isInteractable();
        if (isBlockInteractable && !player.isSneaking()) {
            return;
        }

        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player) && cooldowns.get(player) > now) {
            // The player is still in cooldown.
            return;
        }

        // Setup civ if holding founders flag
        else if (itemInHand.isSimilar(ItemManager.founders_flag)) {
            event.setCancelled(true);
            cooldowns.put(player, now + COOLDOWN);
            CivGlobal.createCivilization(player);
        }

        // Check for custom item
        else if (itemInHand.hasItemMeta() && ItemManager.items.containsKey(itemInHand.getItemMeta().getDisplayName())) {
            event.setCancelled(true);
            cooldowns.put(player, now + COOLDOWN);
        }
    }

}
