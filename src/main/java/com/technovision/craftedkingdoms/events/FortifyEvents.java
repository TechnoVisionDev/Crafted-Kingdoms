package com.technovision.craftedkingdoms.events;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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

        //Check if player has perms to fortify for group
        Resident res = CKGlobal.getResident(player);
        if (!res.isInGroup(group.getName())) {
            MessageUtils.sendError(player, "You are no longer a member of the group you are fortifying for.");
            return;
        }
        if (!res.hasPermission(group, Permissions.BLOCKS)) {
            MessageUtils.sendError(player, "You need the "+ChatColor.YELLOW+"BLOCKS"+ChatColor.RED+" permission to fortify blocks.");
        }

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

    /**
     * Checks if a block is fortified when broken by a player.
     * @param event fires when a player breaks a block.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(loc);
        if (fortifiedBlock != null) {
            fortifiedBlock.decrement();
            if (fortifiedBlock.getFortifications() > 0) {
                // cancel the event to prevent the block from actually breaking
                event.setCancelled(true);
                MessageUtils.send(event.getPlayer(), String.format("%sThat block is fortified by %s%s%s (%d breaks remaining).",
                        ChatColor.GRAY, ChatColor.YELLOW,
                        fortifiedBlock.getGroup(),
                        ChatColor.GRAY,
                        fortifiedBlock.getFortifications()
                        ));
            } else {
                // remove from map and database when health reaches 0
                CKGlobal.removeFortifiedBlock(loc);
            }
        }
    }
}
