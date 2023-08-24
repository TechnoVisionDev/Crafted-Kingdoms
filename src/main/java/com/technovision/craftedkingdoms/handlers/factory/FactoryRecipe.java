package com.technovision.craftedkingdoms.handlers.factory;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Stores data for a factory recipe.
 *
 * @author TechnoVision
 */
public class FactoryRecipe {

    private final ItemStack result;
    private final List<ItemStack> ingredients;

    public FactoryRecipe(ItemStack result, List<ItemStack> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public List<ItemStack> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }
}
