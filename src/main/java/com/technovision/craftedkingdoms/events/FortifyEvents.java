package com.technovision.craftedkingdoms.events;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.ChatColor;
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
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        Material itemType = offHandItem.getType();
        if (!FortifiedBlock.MATERIALS.containsKey(itemType)) return;

        Resident res = CKGlobal.getResident(player);
        Group group = CKGlobal.getFortifyGroup(player);
        if (!isValidFortifyGroup(player, res, group)) return;
        if (!canPlayerFortify(player, res, group)) return;
        if (!FortifiedBlock.isValidBlock(event.getBlockPlaced())) {
            MessageUtils.sendError(player, "That block cannot be reinforced!");
            return;
        }

        fortifyBlock(player, group, offHandItem, event.getBlockPlaced());
    }

    /**
     * Fortify a block by right-clicking with item in main hand.
     * @param event fires when a player interacts.
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        Material itemType = mainHandItem.getType();
        if (!FortifiedBlock.MATERIALS.containsKey(itemType)) return;

        Resident res = CKGlobal.getResident(player);
        Group group = CKGlobal.getFortifyGroup(player);
        if (!isValidFortifyGroup(player, res, group)) return;
        if (!canPlayerFortify(player, res, group)) return;
        if (!FortifiedBlock.isValidBlock(event.getClickedBlock())) {
            MessageUtils.sendError(player, "That block cannot be reinforced!");
            return;
        }

        event.setCancelled(true);
        handleBlockInteraction(player, group, mainHandItem, event.getClickedBlock());
    }

    /**
     * Checks if a block is fortified when broken by a player.
     * @param event fires when a player breaks a block.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        FortifiedBlock fortifiedBlock = CKGlobal.getFortifiedBlock(block.getLocation());
        if (fortifiedBlock == null) return;

        fortifiedBlock.decrement();
        if (fortifiedBlock.getReinforcements() <= 0) return;

        event.setCancelled(true);
        MessageUtils.send(event.getPlayer(), String.format(
                "%sThat block is fortified by %s%s%s (%d breaks remaining).",
                ChatColor.GRAY, ChatColor.YELLOW, fortifiedBlock.getGroup(),
                ChatColor.GRAY, fortifiedBlock.getReinforcements()
        ));
    }

    private boolean isValidFortifyGroup(Player player, Resident res, Group group) {
        if (group == null) return false;
        if (!res.isInGroup(group.getName())) {
            MessageUtils.sendError(player, "You are no longer a member of the group you are fortifying for.");
            return false;
        }
        return true;
    }

    private boolean canPlayerFortify(Player player, Resident res, Group group) {
        if (!res.hasPermission(group, Permissions.BLOCKS)) {
            MessageUtils.sendError(player, "You need the " + ChatColor.YELLOW + "BLOCKS" + ChatColor.RED + " permission to fortify blocks.");
            return false;
        }
        return true;
    }

    private void handleBlockInteraction(Player player, Group group, ItemStack item, Block block) {
        // Check if block is already fortified
        FortifiedBlock alreadyFortifiedBlock = CKGlobal.getFortifiedBlock(block.getLocation());
        Material itemType = item.getType();
        if (alreadyFortifiedBlock != null) {

            if (!alreadyFortifiedBlock.getGroup().equals(group.getName())) {
                MessageUtils.sendError(player, "That block has already been reinforced by " + ChatColor.YELLOW + alreadyFortifiedBlock.getGroup());
                return;
            }
            int maxReinforcements = alreadyFortifiedBlock.getMaxReinforcements();
            int reinforcementsFromHand = FortifiedBlock.getReinforcements(itemType);
            String stringAlreadyFortified = StringUtils.stringifyType(Material.valueOf(alreadyFortifiedBlock.getMaterial()), true);

            if (reinforcementsFromHand > maxReinforcements) {
                // Update block to better material
                String msg = String.format("%sUpgraded this block from %s%s%s to %s%s%s reinforcement",
                        ChatColor.GRAY, ChatColor.YELLOW,
                        stringAlreadyFortified,
                        ChatColor.GRAY, ChatColor.YELLOW,
                        StringUtils.stringifyType(itemType, true),
                        ChatColor.GRAY);
                item.setAmount(item.getAmount() - 1);
                alreadyFortifiedBlock.upgradeMaterial(itemType);
                MessageUtils.send(player, msg);
            }
            else if (reinforcementsFromHand == maxReinforcements) {
                if (reinforcementsFromHand > alreadyFortifiedBlock.getReinforcements()) {
                    // Refill reinforcements to max of current material type
                    item.setAmount(item.getAmount() - 1);
                    alreadyFortifiedBlock.refillReinforcements();
                    MessageUtils.send(player, ChatColor.GRAY + "You have refilled this block's reinforcements to its max.");
                } else {
                    // Block is already at max reinforcements for that type
                    MessageUtils.sendError(player, "That block is already reinforced with " + ChatColor.YELLOW + stringAlreadyFortified);
                }
            }
            else {
                // Block is reinforced with a better material
                MessageUtils.sendError(player, "That block is already reinforced with " + ChatColor.YELLOW + stringAlreadyFortified);
            }
        } else {
            fortifyBlock(player, group, item, block);
        }
    }

    private void fortifyBlock(Player player, Group group, ItemStack item, Block block) {
        Material itemType = item.getType();
        item.setAmount(item.getAmount() - 1);
        group.fortifyBlock(block, itemType);
        MessageUtils.send(player, fortifyMessage(block, itemType));
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
