package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemHandler implements Listener {

    public static final HashMap<String, ItemStack> items = new HashMap<>();
    public static final List<CraftingRecipe> recipes = new ArrayList<>();

    // T1 Materials
    public static ItemStack crafted_stick;
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
    public static ItemStack crafted_leather;
    public static ItemStack decorative_jewels;
    public static ItemStack clay_molding;
    public static ItemStack sticky_resin;
    public static ItemStack steel_ingot;

    // T3 Materials
    public static ItemStack compacted_stone;
    public static ItemStack hardened_wood;
    public static ItemStack masonry_mortar;

    public ItemHandler() {
        initMaterials();

        // 9 XP Bottles ---> Emerald
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(CraftedKingdoms.namespace, "emerald"), new ItemStack(Material.EMERALD));
        for (int i = 0; i < 9; i++) {
            recipe.addIngredient(Material.EXPERIENCE_BOTTLE);
        }
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);

        // Emerald ---> 9 XP Bottles
        recipe = new ShapelessRecipe(new NamespacedKey(CraftedKingdoms.namespace, "xp"), new ItemStack(Material.EXPERIENCE_BOTTLE, 9));
        recipe.addIngredient(Material.EMERALD);
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);

        // Campfire
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "campfire"), new ItemStack(Material.CAMPFIRE));
        shapedRecipe.shape( " S ",
                "S S",
                "PPP");
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(crafted_stick));
        shapedRecipe.setIngredient('P', new RecipeChoice.ExactChoice(refined_wood));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Furnace
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "furnace"), new ItemStack(Material.FURNACE));
        shapedRecipe.shape( "SSS",
                            "SCS",
                            "SSS");
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(refined_stone));
        shapedRecipe.setIngredient('C', Material.COAL_BLOCK);
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Blast Furnace
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "blast_furnace"), new ItemStack(Material.BLAST_FURNACE));
        shapedRecipe.shape( "III",
                            "CFC",
                            "CCC");
        shapedRecipe.setIngredient('C', new RecipeChoice.ExactChoice(crushed_stone));
        shapedRecipe.setIngredient('F', Material.FURNACE);
        shapedRecipe.setIngredient('I', new RecipeChoice.ExactChoice(steel_ingot));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Smoker
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "smoker"), new ItemStack(Material.SMOKER));
        shapedRecipe.shape( "CCC",
                            "HFH",
                            "HHH");
        shapedRecipe.setIngredient('H', new RecipeChoice.ExactChoice(milled_lumber));
        shapedRecipe.setIngredient('F', Material.FURNACE);
        shapedRecipe.setIngredient('C', new RecipeChoice.ExactChoice(clay_molding));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Brewing Stand
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "brewing_stand"), new ItemStack(Material.BREWING_STAND));
        shapedRecipe.shape( " B ",
                            " L ",
                            "MMM");
        shapedRecipe.setIngredient('B', Material.BLAZE_ROD);
        shapedRecipe.setIngredient('L', new RecipeChoice.ExactChoice(hardened_wood));
        shapedRecipe.setIngredient('M', new RecipeChoice.ExactChoice(masonry_mortar));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Cauldron
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "cauldron"), new ItemStack(Material.CAULDRON));
        shapedRecipe.shape( "S S",
                            "S S",
                            "SSS");
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(steel_ingot));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Stonecutter
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "stonecutter"), new ItemStack(Material.STONECUTTER));
        shapedRecipe.shape( "   ",
                            " S ",
                            "CCC");
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(steel_ingot));
        shapedRecipe.setIngredient('C', new RecipeChoice.ExactChoice(crushed_stone));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Smithing Table
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "smithing_table"), new ItemStack(Material.SMITHING_TABLE));
        shapedRecipe.shape( "SSS",
                            "WWW",
                            "WWW");
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(steel_ingot));
        shapedRecipe.setIngredient('W', new RecipeChoice.ExactChoice(milled_lumber));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Enchanting Table
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "enchanting_table"), new ItemStack(Material.ENCHANTING_TABLE));
        shapedRecipe.shape( " B ",
                            "DMD",
                            "OOO");
        shapedRecipe.setIngredient('B', Material.BOOK);
        shapedRecipe.setIngredient('M', new RecipeChoice.ExactChoice(masonry_mortar));
        shapedRecipe.setIngredient('D', new RecipeChoice.ExactChoice(decorative_jewels));
        shapedRecipe.setIngredient('O', Material.OBSIDIAN);
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Cartography Table
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "cartography_table"), new ItemStack(Material.CARTOGRAPHY_TABLE));
        shapedRecipe.shape( "PPP",
                "LLL",
                "LLL");
        shapedRecipe.setIngredient('L', new RecipeChoice.ExactChoice(milled_lumber));
        shapedRecipe.setIngredient('P', new RecipeChoice.ExactChoice(crafted_leather));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Nether Star
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "nether_star"), new ItemStack(Material.NETHER_STAR));
        shapedRecipe.shape( " W ",
                            "WDW",
                            " W ");
        shapedRecipe.setIngredient('D', new RecipeChoice.ExactChoice(decorative_jewels));
        shapedRecipe.setIngredient('W', Material.WITHER_SKELETON_SKULL);
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Saddle
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "saddle"), new ItemStack(Material.SADDLE));
        shapedRecipe.shape( "LLL",
                            "S S",
                            "   ");
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(steel_ingot));
        shapedRecipe.setIngredient('L', new RecipeChoice.ExactChoice(crafted_leather));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Slimeball
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "slime_ball"), new ItemStack(Material.SLIME_BALL));
        shapedRecipe.shape( "CCC",
                "CMC",
                "CCC");
        shapedRecipe.setIngredient('C', Material.CACTUS);
        shapedRecipe.setIngredient('M', Material.MAGMA_CREAM);
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);
    }

    private static void initMaterials() {
        // Create T1 Materials
        crafted_stick = createMaterial("§aCrafted Stick", Material.FLINT, 1);
        setTextureCode(crafted_stick, 100019);
        createMaterialRecipe("crafted_stick", Material.STICK, crafted_stick);

        refined_sugar = createMaterial("§aRefined Sugar", Material.FLINT, 1);
        setTextureCode(refined_sugar, 100014);
        createMaterialRecipe("refined_sugar", Material.SUGAR, refined_sugar);

        carved_leather = createMaterial("§aCarved Leather", Material.FLINT, 1);
        setTextureCode(carved_leather, 100022);
        createMaterialRecipe("carved_leather", Material.LEATHER, carved_leather);

        refined_sulfur = createMaterial("§aRefined Sulfur", Material.FLINT, 1);
        setTextureCode(refined_sulfur, 100021);
        createMaterialRecipe("refined_sulfur", Material.GUNPOWDER, refined_sulfur);

        crafted_reeds = createMaterial("§aCrafted Reeds", Material.FLINT, 1);
        setTextureCode(crafted_reeds, 100013);
        createMaterialRecipe("crafted_reeds", Material.SUGAR_CANE, crafted_reeds);

        crafted_string = createMaterial("§aCrafted String", Material.FLINT, 1);
        setTextureCode(crafted_string, 100001);
        createMaterialRecipe("crafted_string", Material.STRING, crafted_string);

        refined_stone = createMaterial("§aRefined Stone", Material.FLINT, 1);
        setTextureCode(refined_stone, 100023);
        RecipeChoice.MaterialChoice choices = new RecipeChoice.MaterialChoice(
                Material.COBBLESTONE, Material.STONE, Material.GRANITE, Material.DIORITE,
                Material.ANDESITE, Material.COBBLED_DEEPSLATE, Material.DEEPSLATE
        );
        createMaterialRecipe("refined_stone", choices, refined_stone);

        refined_wood = createMaterial("§aRefined Wood", Material.FLINT, 1);
        setTextureCode(refined_wood, 100015);
        choices = new RecipeChoice.MaterialChoice(
                Material.OAK_LOG, Material.ACACIA_LOG, Material.BIRCH_LOG, Material.CHERRY_LOG,
                Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.MANGROVE_LOG, Material.SPRUCE_LOG
        );
        createMaterialRecipe("refined_wood", choices, refined_wood);

        forged_clay = createMaterial("§aForged Clay", Material.FLINT, 1);
        setTextureCode(forged_clay, 100006);
        createMaterialRecipe("forged_clay", Material.CLAY_BALL, forged_clay);

        refined_slime = createMaterial("§aRefined Slime", Material.FLINT, 1);
        setTextureCode(refined_slime, 100007);
        ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(CraftedKingdoms.namespace, "refined_slime"), refined_slime);
        recipe.addIngredient(Material.SLIME_BALL);
        recipe.addIngredient(Material.SLIME_BALL);
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);

        // Create T2 Materials
        milled_lumber = createMaterial("§bMilled Lumber", Material.FLINT, 2);
        setTextureCode(milled_lumber, 100011);
        createMaterialRecipe("milled_lumber", refined_wood, milled_lumber);

        crushed_stone = createMaterial("§bCrushed Stone", Material.FLINT, 2);
        setTextureCode(crushed_stone, 100012);
        createMaterialRecipe("crushed_stone", refined_stone, crushed_stone);

        crafted_leather = createMaterial("§bCrafted Leather", Material.FLINT, 2);
        setTextureCode(crafted_leather, 100008);
        recipe = new ShapelessRecipe(new NamespacedKey(CraftedKingdoms.namespace, "crafted_leather"), crafted_leather);
        recipe.addIngredient(new RecipeChoice.ExactChoice(crafted_string));
        recipe.addIngredient(new RecipeChoice.ExactChoice(carved_leather));
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);

        decorative_jewels = createMaterial("§bDecorative Jewels", Material.FLINT, 2);
        setTextureCode(decorative_jewels, 100003);
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "decorative_jewels"), decorative_jewels);
        shapedRecipe.shape( "AGA",
                            "GDG",
                            "AGA");
        shapedRecipe.setIngredient('G', Material.GOLD_BLOCK);
        shapedRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
        shapedRecipe.setIngredient('A', Material.AMETHYST_SHARD);
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        clay_molding = createMaterial("§bClay Molding", Material.FLINT, 2);
        setTextureCode(clay_molding, 100002);
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "clay_molding"), clay_molding);
        shapedRecipe.shape( "CCC",
                            "RSR",
                            "S S");
        shapedRecipe.setIngredient('C', new RecipeChoice.ExactChoice(forged_clay));
        shapedRecipe.setIngredient('R', new RecipeChoice.ExactChoice(crafted_reeds));
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(crafted_stick));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        sticky_resin = createMaterial("§bSticky Resin", Material.FLINT, 2);
        setTextureCode(sticky_resin, 100016);
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "sticky_resin"), sticky_resin);
        shapedRecipe.shape( "RRR",
                            " S ",
                            " B ");
        shapedRecipe.setIngredient('R', new RecipeChoice.ExactChoice(refined_slime));
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(refined_sugar));
        shapedRecipe.setIngredient('B', Material.BOWL);
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        steel_ingot = createMaterial("§bSteel Ingot", Material.FLINT, 2);
        setTextureCode(steel_ingot, 100018);
        shapedRecipe = new ShapedRecipe(new NamespacedKey(CraftedKingdoms.namespace, "steel_ingot"), steel_ingot);
        shapedRecipe.shape( "III",
                            "CCC",
                            " S ");
        shapedRecipe.setIngredient('I', Material.IRON_BLOCK);
        shapedRecipe.setIngredient('C', Material.COAL_BLOCK);
        shapedRecipe.setIngredient('S', new RecipeChoice.ExactChoice(refined_sulfur));
        Bukkit.getServer().addRecipe(shapedRecipe);
        recipes.add(shapedRecipe);

        // Create T3 Materials
        compacted_stone = createMaterial("§dCompacted Stone", Material.FLINT, 3);
        setTextureCode(compacted_stone, 100004);
        createMaterialRecipe("compacted_stone", crushed_stone, compacted_stone);

        hardened_wood = createMaterial("§dHardened Wood", Material.FLINT, 3);
        setTextureCode(hardened_wood, 100009);
        createMaterialRecipe("hardened_wood", milled_lumber, hardened_wood);

        masonry_mortar = createMaterial("§dMasonry Mortar", Material.FLINT, 3);
        setTextureCode(masonry_mortar, 100010);
        recipe = new ShapelessRecipe(new NamespacedKey(CraftedKingdoms.namespace, "masonry_mortar"), masonry_mortar);
        recipe.addIngredient(new RecipeChoice.ExactChoice(clay_molding));
        recipe.addIngredient(new RecipeChoice.ExactChoice(clay_molding));
        recipe.addIngredient(new RecipeChoice.ExactChoice(sticky_resin));
        recipe.addIngredient(new RecipeChoice.ExactChoice(sticky_resin));
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);
    }

    private static void setTextureCode(ItemStack item, int code) {
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(code);
        item.setItemMeta(meta);
    }

    private static ItemStack createMaterial(String displayName, Material material, int tier) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(List.of(ChatColor.GRAY+"Tier "+tier));
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
        NamespacedKey namespacedKey = new NamespacedKey(CraftedKingdoms.namespace, key);
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
        recipe.shape("xxx", "xxx", "xxx");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(ingredient));
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);
    }

    private static void createMaterialRecipe(String key, Material ingredient, ItemStack result) {
        NamespacedKey namespacedKey = new NamespacedKey(CraftedKingdoms.namespace, key);
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
        recipe.shape("xxx", "xxx", "xxx");
        recipe.setIngredient('x', ingredient);
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);
    }

    private static void createMaterialRecipe(String key, RecipeChoice.MaterialChoice ingredient, ItemStack result) {
        NamespacedKey namespacedKey = new NamespacedKey(CraftedKingdoms.namespace, key);
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
        recipe.shape("xxx", "xxx", "xxx");
        recipe.setIngredient('x', ingredient);
        Bukkit.getServer().addRecipe(recipe);
        recipes.add(recipe);
    }

    /** Disable Some Vanilla Crafting Recipes */

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Material resultType = event.getRecipe().getResult().getType();

        switch (resultType) {
            case FURNACE -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.COBBLESTONE)) {
                    cancelCrafting(event, "The vanilla furnace recipe is disabled!");
                }
            }
            case BLAST_FURNACE -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.SMOOTH_STONE) &&
                        isCraftedWithMaterial(event.getInventory().getMatrix(), Material.IRON_INGOT)) {
                    cancelCrafting(event, "The vanilla blast furnace recipe is disabled!");
                }
            }
            case SMOKER -> {
                if (isCraftedWithSuffix(event.getInventory().getMatrix(), "_LOG")) {
                    cancelCrafting(event, "The vanilla smoker recipe is disabled!");
                }
            }
            case BREWING_STAND -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.BLAZE_ROD) &&
                        isCraftedWithMaterial(event.getInventory().getMatrix(), Material.COBBLESTONE)) {
                    cancelCrafting(event, "The vanilla brewing stand recipe is disabled!");
                }
            }
            case CAULDRON -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.IRON_INGOT)) {
                    cancelCrafting(event, "The vanilla cauldron recipe is disabled!");
                }
            }
            case STONECUTTER -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.IRON_INGOT) &&
                        isCraftedWithMaterial(event.getInventory().getMatrix(), Material.STONE)) {
                    cancelCrafting(event, "The vanilla stonecutter recipe is disabled!");
                }
            }
            case SMITHING_TABLE -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.IRON_INGOT) &&
                        isCraftedWithSuffix(event.getInventory().getMatrix(), "_PLANKS")) {
                    cancelCrafting(event, "The vanilla smithing table recipe is disabled!");
                }
            }
            case ENCHANTING_TABLE -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.BOOK) &&
                        isCraftedWithMaterial(event.getInventory().getMatrix(), Material.DIAMOND) &&
                        isCraftedWithMaterial(event.getInventory().getMatrix(), Material.OBSIDIAN)) {
                    cancelCrafting(event, "The vanilla enchanting table recipe is disabled!");
                }
            }
            case CAMPFIRE -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.STICK)&&
                        isCraftedWithMaterial(event.getInventory().getMatrix(), Material.COAL)) {
                    cancelCrafting(event, "The vanilla campfire recipe is disabled!");
                }
            }
            case CARTOGRAPHY_TABLE -> {
                if (isCraftedWithMaterial(event.getInventory().getMatrix(), Material.PAPER)) {
                    cancelCrafting(event, "The vanilla cartography table recipe is disabled!");
                }
            }
            default -> { }
        }
    }

    private void cancelCrafting(CraftItemEvent event, String message) {
        event.setCancelled(true);
        MessageUtils.sendError(event.getWhoClicked(), message);
        MessageUtils.sendError(event.getWhoClicked(), "Use " + ChatColor.YELLOW + "/recipes " + ChatColor.RED + "to see new recipes.");
    }

    private boolean isCraftedWithMaterial(ItemStack[] matrix, Material material) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType() == material) {
                return true;
            }
        }
        return false;
    }

    private boolean isCraftedWithSuffix(ItemStack[] matrix, String suffix) {
        for (ItemStack item : matrix) {
            if (item != null && item.getType().toString().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
