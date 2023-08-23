package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.handlers.ItemHandler;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

import java.util.List;
import java.util.Map;

public class RecipesCommand implements CommandExecutor, Listener {

    private final String MAIN_GUI_TITLE = "Crafting Recipes";
    private final String RECIPE_GUI_TITLE = "Recipe Details";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        openMainGUI(player);
        return true;
    }

    public void openMainGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, getSuitableInventorySize(ItemHandler.recipes.size()), MAIN_GUI_TITLE);
        for (CraftingRecipe recipe : ItemHandler.recipes) {
            gui.addItem(recipe.getResult());
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals(MAIN_GUI_TITLE) || title.equals(RECIPE_GUI_TITLE)) {
            event.setCancelled(true);
            for (CraftingRecipe recipe : ItemHandler.recipes) {
                if (recipe.getResult().equals(event.getCurrentItem())) {
                    openRecipeDetailsGUI(player, recipe);
                    break;
                }
            }
        }
    }

    public void openRecipeDetailsGUI(Player player, Recipe recipe) {
        Inventory gui = Bukkit.createInventory(null, 27, RECIPE_GUI_TITLE); // Using a 3x9 inventory for simplicity

        // If the recipe is a ShapedRecipe, we can get its ingredients.
        if (recipe instanceof ShapedRecipe shaped) {
            Map<Character, ItemStack> ingredientMap = shaped.getIngredientMap();

            String[] shape = shaped.getShape();
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length(); col++) {
                    char ingredientChar = shape[row].charAt(col);
                    ItemStack ingredient = ingredientMap.getOrDefault(ingredientChar, new ItemStack(Material.AIR));

                    int slotIndex = row * 9 + col; // Convert 2D coordinates to 1D slot index
                    gui.setItem(slotIndex, ingredient);
                }
            }
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            List<ItemStack> ingredients = shapeless.getIngredientList();

            // Fit ingredients into the 3x3 grid and use air blocks for remaining spots
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int slotIndex = row * 9 + col; // Convert 2D coordinates to 1D slot index
                    int ingredientIndex = row * 3 + col;

                    ItemStack ingredient = (ingredientIndex < ingredients.size())
                            ? ingredients.get(ingredientIndex)
                            : new ItemStack(Material.AIR);
                    gui.setItem(slotIndex, ingredient);
                }
            }
        }

        // Add result to the right side
        gui.setItem(15, recipe.getResult());
        player.openInventory(gui);
    }

    private int getSuitableInventorySize(int size) {
        return (size + 8) / 9 * 9;  // Rounds up to the nearest multiple of 9
    }
}
