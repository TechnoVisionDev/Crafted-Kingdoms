package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles fortify commands to protect blocks.
 *
 * @author TechnoVision
 */
public class FortifyCommand extends CommandBase {

    public FortifyCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/fortify";
        displayName = "Fortify";

        // Implemented
        commands.put("enable", "<group> - Enables fortify mode to reinforce blocks.");
        commands.put("disable", "Disables fortify mode.");
        commands.put("inspect", "Toggle inspect mode");
        commands.put("materials", "Show the materials used for fortifying blocks.");
    }

    public void enable_cmd() throws CKException {
        // Get group from args
        if (args.length < 2) {
            throw new CKException("You must specify a group name!");
        }
        Group group = CKGlobal.getGroup(args[1]);
        if (group == null) {
            throw new CKException("The group " + ChatColor.YELLOW + args[1] + ChatColor.RED + " doesn't exist!");
        }

        // Check that player has perms to protect land for this group
        Resident senderRes = getResident();
        if (!senderRes.isInGroup(group.getName())) {
            throw new CKException("You are not a member of that group!");
        }
        if (!senderRes.hasPermission(group, Permissions.BLOCKS)) {
            throw new CKException("You need the "+ChatColor.YELLOW+"BLOCKS"+ChatColor.RED+" permission to fortify blocks.");
        }

        // Enable fortify mode
        Player player = getPlayer();
        CKGlobal.addFortifyGroup(player, group.getName());
        MessageUtils.send(player, ChatColor.GRAY+"You have enabled fortify mode for the group "+ChatColor.YELLOW+group.getName()+ChatColor.GRAY+".");
    }

    public void disable_cmd() throws CKException {
        Player player = getPlayer();
        CKGlobal.removeFortifyGroup(player);
        MessageUtils.send(player, ChatColor.GRAY+"You have disabled fortify mode.");
    }

    public void inspect_cmd() throws CKException {
        Resident res = getResident();
        res.toggleInspectMode();
        String mode;
        if (res.isInspectMode()) {
            mode = "enabled";
        } else {
            mode = "disabled";
        }
        MessageUtils.send(getPlayer(), ChatColor.GRAY+"You have "+ ChatColor.GREEN + mode + ChatColor.GRAY + " inspect mode for fortified blocks.");
    }

    public void materials_cmd() throws CKException {
        Player player = getPlayer();
        Inventory inventory = Bukkit.createInventory(null, 27, "Fortify Materials");

        // Sort the OVERWORLD_MATERIALS entries from lowest to highest
        List<Map.Entry<Material, Integer>> sortedOverworldMaterials = FortifiedBlock.OVERWORLD_MATERIALS.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        // Populate the first row with the sorted items, colored names, and lore with unique numbers
        int slot = 0;
        for (Map.Entry<Material, Integer> entry : sortedOverworldMaterials) {
            ItemStack item = new ItemStack(entry.getKey(), 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + StringUtils.stringifyType(entry.getKey()));
            meta.setLore(Arrays.asList(
                            ChatColor.GREEN + "Health: " + entry.getValue(),
                            ChatColor.GRAY + "Return Chance: 100%",
                            ChatColor.GREEN + "Overworld Only"
            ));
            item.setItemMeta(meta);
            inventory.setItem(slot++, item);
        }

        // Sort the NETHER_MATERIALS entries from lowest to highest
        List<Map.Entry<Material, Integer>> sortedNetherMaterials = FortifiedBlock.NETHER_MATERIALS.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        // Populate the third row with the sorted items, colored names, and lore with unique numbers
        slot = 18; // Start from the first slot of the third row
        for (Map.Entry<Material, Integer> entry : sortedNetherMaterials) {
            ItemStack item = new ItemStack(entry.getKey(), 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.AQUA + StringUtils.stringifyType(entry.getKey()));
            meta.setLore(Arrays.asList(
                    ChatColor.GREEN + "Health: " + entry.getValue(),
                    ChatColor.GRAY + "Return Chance: 100%",
                    ChatColor.RED + "Nether Only"
            ));
            item.setItemMeta(meta);
            inventory.setItem(slot++, item);
        }
        player.openInventory(inventory);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = super.onTabComplete(sender, cmd, alias, args);
        if (args.length == 2 && args[0].equalsIgnoreCase("enable")) {
            Set<String> groupNames = CKGlobal.getResident((Player) sender).getGroups();
            return groupNames.stream().filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }
        return completions;
    }

    @Override
    public void doDefaultAction() throws CKException {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() throws CKException {
    }
}
