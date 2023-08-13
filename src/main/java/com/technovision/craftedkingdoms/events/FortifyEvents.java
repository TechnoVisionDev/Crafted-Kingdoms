package com.technovision.craftedkingdoms.events;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
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
import org.bukkit.event.block.BlockPlaceEvent;
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
     * Auto fortify placed blocks if material is in offhand slot.
     * @param event fires when a player places a block with main hand.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // Get item in player's offhand
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        ItemStack item = event.getPlayer().getInventory().getItemInOffHand();
        Material itemType = item.getType();
        if (!FortifiedBlock.MATERIALS.containsKey(itemType)) return;

        // Get player fortify group (will be null if fortify mode disabled)
        Player player = event.getPlayer();
        Group group = CKGlobal.getFortifyGroup(player);
        if (group == null) return;

        // Check if player has perms to fortify for group
        Resident res = CKGlobal.getResident(player);
        if (!res.isInGroup(group.getName())) {
            MessageUtils.sendError(player, "You are no longer a member of the group you are fortifying for.");
            return;
        }
        if (!res.hasPermission(group, Permissions.BLOCKS)) {
            MessageUtils.sendError(player, "You need the "+ChatColor.YELLOW+"BLOCKS"+ChatColor.RED+" permission to fortify blocks.");
        }

        // Check if block can be reinforced
        Block block = event.getBlockPlaced();
        if (!FortifiedBlock.isValidBlock(block)) {
            MessageUtils.sendError(player, "That block cannot be reinforced!");
            return;
        }

        // Fortify block if player has correct item in offhand
        item.setAmount(item.getAmount()-1);
        group.fortifyBlock(block, itemType);
        MessageUtils.send(player, fortifyMessage(block, itemType));
    }

    /**
     * Fortify a block by right-clicking with item in main hand.
     * @param event fires when a player interacts.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        Material itemType = item.getType();
        if (!FortifiedBlock.MATERIALS.containsKey(itemType)) return;

        // Get player fortify group (will be null if fortify mode disabled)
        Player player = event.getPlayer();
        Group group = CKGlobal.getFortifyGroup(player);
        if (group == null) return;

        // Check if player has perms to fortify for group
        Resident res = CKGlobal.getResident(player);
        if (!res.isInGroup(group.getName())) {
            MessageUtils.sendError(player, "You are no longer a member of the group you are fortifying for.");
            return;
        }
        if (!res.hasPermission(group, Permissions.BLOCKS)) {
            MessageUtils.sendError(player, "You need the "+ChatColor.YELLOW+"BLOCKS"+ChatColor.RED+" permission to fortify blocks.");
        }

        // Check if block can be reinforced
        Block block = event.getClickedBlock();
        if (!FortifiedBlock.isValidBlock(block)) {
            MessageUtils.sendError(player, "That block cannot be reinforced!");
            return;
        }

        // Check if block is already fortified
        FortifiedBlock alreadyFortifiedBlock = CKGlobal.getFortifiedBlock(block.getLocation());
        if (alreadyFortifiedBlock != null) {
            if (!alreadyFortifiedBlock.getGroup().equals(group.getName())) {
                MessageUtils.sendError(player, "That block has already been reinforced by " + ChatColor.YELLOW + alreadyFortifiedBlock.getGroup());
                return;
            }
            String stringAlreadyFortified = StringUtils.stringifyType(Material.valueOf(alreadyFortifiedBlock.getMaterial()), true);
            String stringItem = StringUtils.stringifyType(itemType, true);

            int maxReinforcements = alreadyFortifiedBlock.getMaxReinforcements();
            int reinforcementsFromHand = FortifiedBlock.getReinforcements(itemType);

            if (reinforcementsFromHand > maxReinforcements) {
                // Update block to better material
                String msg = String.format("%sUpgraded this block from %s%s%s to %s%s%s reinforcement",
                        ChatColor.GRAY, ChatColor.YELLOW,
                        stringAlreadyFortified,
                        ChatColor.GRAY, ChatColor.YELLOW,
                        stringItem,
                        ChatColor.GRAY
                );
                item.setAmount(item.getAmount()-1);
                alreadyFortifiedBlock.upgradeMaterial(itemType);
                MessageUtils.send(player, msg);
                return;
            }
            else if (reinforcementsFromHand == maxReinforcements) {
                if (reinforcementsFromHand > alreadyFortifiedBlock.getReinforcements()) {
                    // Refill reinforcements to max of current material type
                    item.setAmount(item.getAmount()-1);
                    alreadyFortifiedBlock.refillReinforcements();
                    MessageUtils.send(player, ChatColor.GRAY + "You have refilled this block's reinforcements to it's max.");
                } else {
                    MessageUtils.sendError(player, "That block is already reinforced with " + ChatColor.YELLOW + stringAlreadyFortified);
                }
                return;
            }
            else {
                // Block is reinforced with a better material
                MessageUtils.sendError(player, "That block is already reinforced with " + ChatColor.YELLOW + stringItem);
                return;
            }
        }

        // Fortify block if player has correct item in main hand
        item.setAmount(item.getAmount()-1);
        group.fortifyBlock(block, itemType);
        MessageUtils.send(player, fortifyMessage(block, item.getType()));
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
            fortifiedBlock.decrement(); // auto deletes at 0
            if (fortifiedBlock.getReinforcements() > 0) {
                // cancel the event to prevent the block from actually breaking
                event.setCancelled(true);
                MessageUtils.send(event.getPlayer(), String.format("%sThat block is fortified by %s%s%s (%d breaks remaining).",
                        ChatColor.GRAY, ChatColor.YELLOW,
                        fortifiedBlock.getGroup(),
                        ChatColor.GRAY,
                        fortifiedBlock.getReinforcements()
                        ));
            }
        }
    }

    private String fortifyMessage(Block block, Material material) {
        return String.format("%sYou fortified a %s%s%s at %s(%d, %d, %d)%s with a %s%s%s.",
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(block.getType()),
                ChatColor.GRAY, ChatColor.YELLOW,
                block.getX(), block.getY(), block.getZ(),
                ChatColor.GRAY, ChatColor.YELLOW,
                StringUtils.stringifyType(material),
                ChatColor.GRAY
        );
    }
}
