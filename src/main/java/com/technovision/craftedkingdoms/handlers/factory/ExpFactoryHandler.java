package com.technovision.craftedkingdoms.handlers.factory;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.objects.FortifiedBlock;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Handles the Exp Cauldron Factory
 *
 * @author TechnoVision
 */
public class ExpFactoryHandler implements Listener {

    private static final long COOLDOWN_IN_MILLIS = 500;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public static final String GUI_TITLE = "Exp Factory";
    private final Map<Integer, FactoryRecipe> slotToRecipe = new HashMap<>();

    public ExpFactoryHandler() {
        // Tier 1 Recipes
        slotToRecipe.put(1, new FactoryRecipe("4 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 4),List.of(
                new ItemStack(Material.ENDER_PEARL, 4),
                new ItemStack(Material.HAY_BLOCK, 3),
                new ItemStack(Material.GLASS_BOTTLE, 4),
                new ItemStack(Material.CHARCOAL, 40)
        )));
        slotToRecipe.put(3, new FactoryRecipe("4 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 4),List.of(
                new ItemStack(Material.CACTUS, 32),
                new ItemStack(Material.NETHER_WART, 25),
                new ItemStack(Material.GLASS_BOTTLE, 4),
                new ItemStack(Material.CHARCOAL, 40)
        )));
        slotToRecipe.put(5, new FactoryRecipe("4 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 4),List.of(
                new ItemStack(Material.CARROT, 64),
                new ItemStack(Material.SOUL_SAND, 64),
                new ItemStack(Material.GLASS_BOTTLE, 4),
                new ItemStack(Material.CHARCOAL, 40)
        )));
        slotToRecipe.put(7, new FactoryRecipe("4 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 4),List.of(
                new ItemStack(Material.GLOWSTONE, 16),
                new ItemStack(Material.POTATO, 64),
                new ItemStack(Material.GLASS_BOTTLE, 4),
                new ItemStack(Material.CHARCOAL, 40)
        )));

        // Tier 2 Recipes
        slotToRecipe.put(19, new FactoryRecipe("32 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 32),List.of(
                new ItemStack(Material.GLASS_BOTTLE, 32),
                new ItemStack(Material.CARROT, 512),
                new ItemStack(Material.CACTUS, 16),
                new ItemStack(Material.COCOA_BEANS, 64),
                new ItemStack(Material.MELON, 4),
                new ItemStack(Material.CHARCOAL, 1)
        )));
        slotToRecipe.put(21, new FactoryRecipe("32 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 32),List.of(
                new ItemStack(Material.GLASS_BOTTLE, 32),
                new ItemStack(Material.WHEAT, 256),
                new ItemStack(Material.BAKED_POTATO, 256),
                new ItemStack(Material.SUGAR_CANE, 16),
                new ItemStack(Material.PUMPKIN, 8),
                new ItemStack(Material.CHARCOAL, 1)
        )));
        slotToRecipe.put(23, new FactoryRecipe("32 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 32),List.of(
                new ItemStack(Material.GLASS_BOTTLE, 32),
                new ItemStack(Material.CARROTS, 512),
                new ItemStack(Material.BAKED_POTATO, 256),
                new ItemStack(Material.SUGAR_CANE, 16),
                new ItemStack(Material.MELON, 4),
                new ItemStack(Material.CHARCOAL, 1)
        )));
        slotToRecipe.put(25, new FactoryRecipe("32 Exp Bottles", new ItemStack(Material.EXPERIENCE_BOTTLE, 32),List.of(
                new ItemStack(Material.GLASS_BOTTLE, 32),
                new ItemStack(Material.WHEAT, 256),
                new ItemStack(Material.CACTUS, 16),
                new ItemStack(Material.COCOA_BEANS, 64),
                new ItemStack(Material.PUMPKIN, 8),
                new ItemStack(Material.CHARCOAL, 1)
        )));
    }

    /**
     * Handles cauldron XP factory
     * @param event Fires when player right clicks cauldron with stick
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Check for cooldown
        if(cooldowns.containsKey(playerId) && (System.currentTimeMillis() - cooldowns.get(playerId)) < COOLDOWN_IN_MILLIS) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.STICK) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        if (clickedBlock.getType() != Material.CAULDRON
                && clickedBlock.getType() != Material.WATER_CAULDRON
                && clickedBlock.getType() != Material.LAVA_CAULDRON
                && clickedBlock.getType() != Material.POWDER_SNOW_CAULDRON
        ) return;

        FortifiedBlock cauldron = CKGlobal.getFortifiedBlock(clickedBlock.getLocation());
        if (cauldron == null || CKGlobal.getGroup(cauldron.getGroup()) == null) {
            MessageUtils.sendError(player, "That block must be reinforced to access factory recipes!");
            cooldowns.put(playerId, System.currentTimeMillis());  // Set cooldown
            return;
        }

        Resident resident = CKGlobal.getResident(player);
        if (!resident.isInGroup(cauldron.getGroup())) {
            MessageUtils.sendError(player, "You must be a member of " + ChatColor.YELLOW + cauldron.getGroup() + ChatColor.RED + " to use that factory!");
            cooldowns.put(playerId, System.currentTimeMillis());  // Set cooldown
            return;
        }

        openXPFactory(player);
        cooldowns.put(playerId, System.currentTimeMillis());  // Set cooldown
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;

            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getSlot();

            // Retrieve the recipe based on the slot
            FactoryRecipe matchingRecipe = slotToRecipe.get(clickedSlot);
            if (matchingRecipe != null) {
                if (hasRequiredIngredients(player, matchingRecipe)) {
                    removeIngredients(player, matchingRecipe);
                    player.getInventory().addItem(matchingRecipe.getResult().clone());
                    player.sendMessage(String.format("%sYou crafted %s%d Exp Bottles",
                            ChatColor.GREEN,
                            ChatColor.YELLOW,
                            matchingRecipe.getResult().getAmount()
                    ));
                } else {
                    player.sendMessage(ChatColor.RED + "You don't have the required ingredients!");
                }
            }
        }
    }

    private boolean hasRequiredIngredients(Player player, FactoryRecipe recipe) {
        for (ItemStack ingredient : recipe.getIngredients()) {
            if (!player.getInventory().containsAtLeast(ingredient, ingredient.getAmount())) {
                return false;
            }
        }
        return true;
    }

    private void removeIngredients(Player player, FactoryRecipe recipe) {
        for (ItemStack ingredient : recipe.getIngredients()) {
            player.getInventory().removeItem(new ItemStack(ingredient.getType(), ingredient.getAmount()));
        }
    }

    private void openXPFactory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);
        for (Map.Entry<Integer, FactoryRecipe> entry : slotToRecipe.entrySet()) {
            gui.setItem(entry.getKey(), createRecipeItem(entry.getValue()));
        }
        player.openInventory(gui);
    }

    private ItemStack createRecipeItem(FactoryRecipe recipe) {
        ItemStack item = recipe.getResult().clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + recipe.getTitle());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "--------------");
        for (ItemStack ingredient : recipe.getIngredients()) {
            lore.add(ChatColor.GRAY+"- "+ChatColor.GREEN+ingredient.getAmount()
                    +" "+StringUtils.stringifyType(ingredient.getType())
            );
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
