package com.technovision.craftedkingdoms.handlers.factory;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Stores data for a factory recipe.
 *
 * @author TechnoVision
 */
public class FactoryRecipe {

    private final String title;
    private final ItemStack result;
    private final List<ItemStack> ingredients;

    public FactoryRecipe(String title, ItemStack result, List<ItemStack> ingredients) {
        this.title = title;
        this.result = result;
        this.ingredients = ingredients;
    }

    public String getTitle() {
        return title;
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }
}
