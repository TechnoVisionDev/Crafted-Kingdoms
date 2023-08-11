package com.technovision.civilization.managers;

import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.util.CivColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class ItemManager {

    public static final HashMap<String, ItemStack> items = new HashMap<>();

    // T1 Materials
    public static ItemStack crafted_stick;
    public static ItemStack packed_feathers;
    public static ItemStack refined_sugar;
    public static ItemStack carved_leather;
    public static ItemStack refined_sulfur;
    public static ItemStack crafted_reeds;
    public static ItemStack crafted_string;
    public static ItemStack refined_stone;
    public static ItemStack refined_wood;
    public static ItemStack forged_clay;
    public static ItemStack refined_slime;

    // T2 Materials
    public static ItemStack milled_lumber;
    public static ItemStack crushed_stone;
    public static ItemStack decorative_feathers;
    public static ItemStack decorative_jewels;
    public static ItemStack jewelry_grade_gold;
    public static ItemStack clay_molding;
    public static ItemStack sticky_resin;

    // T3 Materials
    public static ItemStack compacted_stone;
    public static ItemStack hardened_wood;
    public static ItemStack masonry_mortar;

    // Special Items
    public static ItemStack chieftains_headdress;
    public static ItemStack royal_crown;
    public static ItemStack campsite;
    public static ItemStack founders_flag;

    public static void init() {
        initMaterials();

        // Chieftain's Headdress
        chieftains_headdress = createMaterial("§6Chieftain's Headdress", Material.CHAINMAIL_HELMET, List.of(CivColor.LightGray+"Special"));
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(CivilizationPlugin.namespace, "chieftains_headdress"), chieftains_headdress);
        recipe.shape(   "FFF", 
                        "L L");
        recipe.setIngredient('F', new RecipeChoice.ExactChoice(decorative_feathers));
        recipe.setIngredient('L', new RecipeChoice.ExactChoice(carved_leather));
        Bukkit.getServer().addRecipe(recipe);

        // Royal Crown
        royal_crown = createMaterial("§6Royal Crown", Material.GOLDEN_HELMET, List.of(CivColor.LightGray+"Special"));
        recipe = new ShapedRecipe(new NamespacedKey(CivilizationPlugin.namespace, "royal_crown"), royal_crown);
        recipe.shape(   "GGG",
                        "D D");
        recipe.setIngredient('G', new RecipeChoice.ExactChoice(jewelry_grade_gold));
        recipe.setIngredient('D', new RecipeChoice.ExactChoice(decorative_jewels));
        Bukkit.getServer().addRecipe(recipe);

        // Camp
        campsite = createMaterial("§6Campsite", Material.OAK_DOOR, List.of(CivColor.LightGray+"Special", CivColor.Rose+"<Right Click To Use>"));
        recipe = new ShapedRecipe(new NamespacedKey(CivilizationPlugin.namespace, "campsite"), campsite);
        recipe.shape(   "LHL", 
                        "LLL", 
                        "CCC");
        recipe.setIngredient('H', new RecipeChoice.ExactChoice(chieftains_headdress));
        recipe.setIngredient('L', new RecipeChoice.ExactChoice(milled_lumber));
        recipe.setIngredient('C', Material.COAL_BLOCK);
        Bukkit.getServer().addRecipe(recipe);

        // Founder's Flag
        founders_flag = createMaterial("§6Founder's Flag", Material.BLAZE_ROD, List.of(CivColor.LightGray+"Special", CivColor.Rose+"<Right Click To Use>"));
        recipe = new ShapedRecipe(new NamespacedKey(CivilizationPlugin.namespace, "founders_flag"), founders_flag);
        recipe.shape(   "SRS",
                        "SSS",
                        "MMM");
        recipe.setIngredient('R', new RecipeChoice.ExactChoice(royal_crown));
        recipe.setIngredient('S', new RecipeChoice.ExactChoice(compacted_stone));
        recipe.setIngredient('M', new RecipeChoice.ExactChoice(masonry_mortar));
        Bukkit.getServer().addRecipe(recipe);
    }

    private static void initMaterials() {
        // Create T1 Materials
        crafted_stick = createMaterial("§aCrafted Stick", Material.STICK, 1);
        createMaterialRecipe("crafted_stick", Material.STICK, crafted_stick);

        packed_feathers = createMaterial("§aPacked Feathers", Material.QUARTZ, 1);
        createMaterialRecipe("packed_feathers", Material.FEATHER, packed_feathers);
        
        refined_sugar = createMaterial("§aRefined Sugar", Material.SUGAR, 1);
        createMaterialRecipe("refined_sugar", Material.SUGAR, refined_sugar);

        carved_leather = createMaterial("§aCarved Leather", Material.LEATHER, 1);
        createMaterialRecipe("carved_leather", Material.LEATHER, carved_leather);

        refined_sulfur = createMaterial("§aRefined Sulfur", Material.GUNPOWDER, 1);
        createMaterialRecipe("refined_sulfur", Material.GUNPOWDER, refined_sulfur);

        crafted_reeds = createMaterial("§aCrafted Reeds", Material.SUGAR_CANE, 1);
        createMaterialRecipe("crafted_reeds", Material.SUGAR_CANE, crafted_reeds);

        crafted_string = createMaterial("§aCrafted String", Material.STRING, 1);
        createMaterialRecipe("crafted_string", Material.STRING, crafted_string);

        refined_stone = createMaterial("§aRefined Stone", Material.STONE_SLAB, 1);
        RecipeChoice.MaterialChoice choices = new RecipeChoice.MaterialChoice(
                Material.COBBLESTONE, Material.STONE, Material.GRANITE, Material.DIORITE,
                Material.ANDESITE, Material.COBBLED_DEEPSLATE, Material.DEEPSLATE
        );
        createMaterialRecipe("refined_stone", choices, refined_stone);

        refined_wood = createMaterial("§aRefined Wood", Material.OAK_SLAB, 1);
        choices = new RecipeChoice.MaterialChoice(
                Material.OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG, Material.CHERRY_LOG,
                Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.MANGROVE_LOG, Material.SPRUCE_LOG
        );
        createMaterialRecipe("refined_wood", choices, refined_wood);

        forged_clay = createMaterial("§aForged Clay", Material.BRICK, 1);
        createMaterialRecipe("forged_clay", Material.CLAY_BALL, forged_clay);

        refined_slime = createMaterial("§aRefined Slime", Material.LIME_DYE, 1);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(CivilizationPlugin.namespace, "refined_slime"), refined_slime);
        recipe.addIngredient(Material.SLIME_BALL);
        recipe.addIngredient(Material.SLIME_BALL);
        Bukkit.getServer().addRecipe(recipe);

        // Create T2 Materials
        milled_lumber = createMaterial("§bMilled Lumber", Material.OAK_STAIRS, 2);
        createMaterialRecipe("milled_lumber", refined_wood, milled_lumber);

        crushed_stone = createMaterial("§bCrushed Stone", Material.STONE_STAIRS, 2);
        createMaterialRecipe("crushed_stone", refined_stone, crushed_stone);

        decorative_feathers = createMaterial("§bDecorative Feathers", Material.FEATHER, 2);
        recipe = new ShapelessRecipe(new NamespacedKey(CivilizationPlugin.namespace, "decorative_feathers"), decorative_feathers);
        recipe.addIngredient(new RecipeChoice.ExactChoice(packed_feathers));
        recipe.addIngredient(new RecipeChoice.ExactChoice(packed_feathers));
        Bukkit.getServer().addRecipe(recipe);

        decorative_jewels = createMaterial("§bDecorative Jewels", Material.NETHER_WART, 2);
        createMaterialRecipe("decorative_jewels", Material.DIAMOND, decorative_jewels);

        jewelry_grade_gold = createMaterial("§bJewelry Grade Gold", Material.GOLDEN_APPLE, 2);
        createMaterialRecipe("jewelry_grade_gold", Material.GOLD_INGOT, jewelry_grade_gold);

        clay_molding = createMaterial("§bClay Molding", Material.BRICK_STAIRS, 2);
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(CivilizationPlugin.namespace, "clay_molding"), clay_molding);
        shapedRecipe.shape( "CCC",
                            "RSR",
                            "S S");
        shapedRecipe.setIngredient('C', new RecipeChoice.ExactChoice(forged_clay));
        shapedRecipe.setIngredient('R', new RecipeChoice.ExactChoice(crafted_reeds));
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(crafted_stick));
        Bukkit.getServer().addRecipe(shapedRecipe);

        sticky_resin = createMaterial("§bSticky Resin", Material.GREEN_DYE, 2);
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CivilizationPlugin.namespace, "sticky_resin"), sticky_resin);
        shapedRecipe.shape( "RRR",
                            " S ",
                            " B ");
        shapedRecipe.setIngredient('R', new RecipeChoice.ExactChoice(refined_slime));
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(refined_sugar));
        shapedRecipe.setIngredient('B', Material.BOWL);
        Bukkit.getServer().addRecipe(shapedRecipe);

        // Create T3 Materials
        compacted_stone = createMaterial("§dCompacted Stone", Material.STONE_BRICKS, 3);
        createMaterialRecipe("compacted_stone", crushed_stone, compacted_stone);

        hardened_wood = createMaterial("§dHardened Wood", Material.OAK_WOOD, 3);
        createMaterialRecipe("hardened_wood", milled_lumber, hardened_wood);

        masonry_mortar = createMaterial("§dMasonry Mortar", Material.BRICKS, 3);
        recipe = new ShapelessRecipe(new NamespacedKey(CivilizationPlugin.namespace, "masonry_mortar"), masonry_mortar);
        recipe.addIngredient(new RecipeChoice.ExactChoice(clay_molding));
        recipe.addIngredient(new RecipeChoice.ExactChoice(clay_molding));
        recipe.addIngredient(new RecipeChoice.ExactChoice(sticky_resin));
        recipe.addIngredient(new RecipeChoice.ExactChoice(sticky_resin));
        Bukkit.getServer().addRecipe(recipe);
    }

    private static ItemStack createMaterial(String displayName, Material material, int tier) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(List.of(CivColor.LightGray+"Tier "+tier));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        items.put(displayName, item);
        return item;
    }

    private static ItemStack createMaterial(String displayName, Material material, List<String> lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        items.put(displayName, item);
        return item;
    }

    private static void createMaterialRecipe(String key, ItemStack ingredient, ItemStack result) {
        NamespacedKey namespacedKey = new NamespacedKey(CivilizationPlugin.namespace, key);
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
        recipe.shape("xxx", "xxx", "xxx");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(ingredient));
        Bukkit.getServer().addRecipe(recipe);
    }

    private static void createMaterialRecipe(String key, Material ingredient, ItemStack result) {
        NamespacedKey namespacedKey = new NamespacedKey(CivilizationPlugin.namespace, key);
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
        recipe.shape("xxx", "xxx", "xxx");
        recipe.setIngredient('x', ingredient);
        Bukkit.getServer().addRecipe(recipe);
    }

    private static void createMaterialRecipe(String key, RecipeChoice.MaterialChoice ingredient, ItemStack result) {
        NamespacedKey namespacedKey = new NamespacedKey(CivilizationPlugin.namespace, key);
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
        recipe.shape("xxx", "xxx", "xxx");
        recipe.setIngredient('x', ingredient);
        Bukkit.getServer().addRecipe(recipe);
    }
}
