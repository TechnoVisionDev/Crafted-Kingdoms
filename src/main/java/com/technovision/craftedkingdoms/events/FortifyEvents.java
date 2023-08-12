package com.technovision.craftedkingdoms.events;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Handles events for fortifying blocks.
 *
 * @author TechnoVision
 */
public class FortifyEvents implements Listener {

    /**
     * Fortify a block by right-clicking with item in offhand.
     * @param event fires when a player interacts.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.OFF_HAND) return;
        ItemStack item = event.getPlayer().getInventory().getItemInOffHand();

        // Get player fortify group (will be null if fortify mode disabled)
        Player player = event.getPlayer();
        Group group = CKGlobal.getFortifyGroup(player);
        if (group == null) return;

        // TODO: Check if player has perms to fortify for group

        // TODO: Check if block is already fortified

        // Fortify block if player has correct item in offhand
        Block block = event.getClickedBlock();
        if (item.getType() == Material.IRON_INGOT) {
            group.fortifyBlock(block, Material.IRON_INGOT);
        }
        else if (item.getType() == Material.DIAMOND) {
            group.fortifyBlock(block, Material.DIAMOND);
        }
        else if (item.getType() == Material.NETHERITE_INGOT) {
            group.fortifyBlock(block, Material.NETHERITE_INGOT);
        }
        else {
            return;
        }

        // Send success message to player
        MessageUtils.send(player, String.format("%sYou fortified a %s%s%s at %s(%d, %d, %d)%s with a %s%s%s.",
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(block.getType()),
                ChatColor.GRAY, ChatColor.YELLOW,
                block.getX(), block.getY(), block.getZ(),
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(item.getType()),
                ChatColor.GRAY
        ));

        item.setAmount(item.getAmount()-1);
    }
}
