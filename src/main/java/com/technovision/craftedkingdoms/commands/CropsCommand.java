package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.data.enums.BiomeData;
import com.technovision.craftedkingdoms.handlers.ItemHandler;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CropsCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Biome biome = player.getLocation().getBlock().getBiome();
        Map<Material, Double> crops = BiomeData.CROPS.get(biome);
        if (crops == null) {
            MessageUtils.send(sender, "You cannot grow any crops in this biome!");
            return true;
        }

        String title = "Crops: " + biome.name();
        Inventory gui = Bukkit.createInventory(null, getSuitableInventorySize(crops.size()), title);
        for (Map.Entry<Material, Double> crop : crops.entrySet()) {
            ItemStack item = new ItemStack(getItemForm(crop.getKey()));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_GREEN + StringUtils.stringifyType(item.getType()));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_AQUA + "Time: " + ChatColor.GRAY + crop.getValue() + " h");
            meta.setLore(lore);
            item.setItemMeta(meta);
            gui.addItem(item);
        }

        // Open inventory
        player.openInventory(gui);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        String title = event.getView().getTitle();
        if (title.startsWith("Crops: ")) {
            event.setCancelled(true);
        }
    }

    private int getSuitableInventorySize(int size) {
        return (size + 8) / 9 * 9;  // Rounds up to the nearest multiple of 9
    }

    private Material getItemForm(Material type) {
        if (type == Material.WHEAT) type = Material.WHEAT_SEEDS;
        else if (type == Material.POTATOES) type = Material.POTATO;
        else if (type == Material.CARROTS) type = Material.CARROT;
        else if (type == Material.BEETROOTS) type = Material.BEETROOT;
        else if (type == Material.MELON_STEM) type = Material.MELON_SEEDS;
        else if (type == Material.PUMPKIN_STEM) type = Material.PUMPKIN_SEEDS;
        else if (type == Material.COCOA) type = Material.COCOA_BEANS;
        return type;
    }
}
