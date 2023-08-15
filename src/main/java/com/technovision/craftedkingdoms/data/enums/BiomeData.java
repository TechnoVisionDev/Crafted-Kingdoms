package com.technovision.craftedkingdoms.data.enums;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import java.util.*;

/**
 * Stores growth and breeding data for realistic biomes.
 *
 * @author TechnoVision
 */
public class BiomeData {

    /** For each biome, maps a crop to the hours it takes to grow */
    private static final Map<Biome, Map<Material, Double>> CROPS = new HashMap<>();

    /** For each biome, maps an animal to the percentage chance for successfully breeding */
    private static final Map<Biome, Map<EntityType, Double>> ANIMALS = new HashMap<>();

    /** Maps the material of a seed to the material of it's crop in block form */
    public static Map<Material, Material> SEED_TO_CROP;
    static {
        SEED_TO_CROP = new HashMap<>();
        SEED_TO_CROP.put(Material.WHEAT_SEEDS, Material.WHEAT);
        SEED_TO_CROP.put(Material.CARROT, Material.CARROTS);
        SEED_TO_CROP.put(Material.POTATO, Material.POTATOES);
        SEED_TO_CROP.put(Material.NETHER_WART, Material.NETHER_WART_BLOCK);
        SEED_TO_CROP.put(Material.MELON_SEEDS, Material.MELON_STEM);
        SEED_TO_CROP.put(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM);
        SEED_TO_CROP.put(Material.MELON_STEM, Material.MELON);
        SEED_TO_CROP.put(Material.PUMPKIN_STEM, Material.PUMPKIN);
        SEED_TO_CROP.put(Material.ATTACHED_MELON_STEM, Material.MELON);
        SEED_TO_CROP.put(Material.ATTACHED_PUMPKIN_STEM, Material.PUMPKIN);
        SEED_TO_CROP.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        SEED_TO_CROP.put(Material.CACTUS, Material.CACTUS);
        SEED_TO_CROP.put(Material.SUGAR_CANE, Material.SUGAR_CANE);
        SEED_TO_CROP.put(Material.COCOA_BEANS, Material.COCOA);
        SEED_TO_CROP.put(Material.OAK_SAPLING, Material.OAK_SAPLING);
        SEED_TO_CROP.put(Material.SPRUCE_SAPLING, Material.SPRUCE_SAPLING);
        SEED_TO_CROP.put(Material.ACACIA_SAPLING, Material.ACACIA_SAPLING);
        SEED_TO_CROP.put(Material.BAMBOO_SAPLING, Material.BAMBOO_SAPLING);
        SEED_TO_CROP.put(Material.BIRCH_SAPLING, Material.BIRCH_SAPLING);
        SEED_TO_CROP.put(Material.CHERRY_SAPLING, Material.CHERRY_SAPLING);
        SEED_TO_CROP.put(Material.JUNGLE_SAPLING, Material.JUNGLE_SAPLING);
        SEED_TO_CROP.put(Material.DARK_OAK_SAPLING, Material.DARK_OAK_SAPLING);
    }

    static {
        // Initialize maps
        for (Biome biome : Biome.values()) {
            CROPS.put(biome, new HashMap<>());
        }
        for (Biome biome : Biome.values()) {
            ANIMALS.put(biome, new HashMap<>());
        }

        // Populate crop map
        CROPS.get(Biome.DESERT).put(Material.WHEAT, 0.0);
        CROPS.get(Biome.DESERT).put(Material.CARROTS, 0.0);
        CROPS.get(Biome.DESERT).put(Material.POTATOES, 0.0);

        CROPS.get(Biome.FOREST).put(Material.WHEAT, 5.0);
        CROPS.get(Biome.FOREST).put(Material.CARROTS, 4.0);
        CROPS.get(Biome.FOREST).put(Material.POTATOES, 3.0);

        CROPS.get(Biome.PLAINS).put(Material.WHEAT, 7.0);
        CROPS.get(Biome.PLAINS).put(Material.CARROTS, 6.0);
        CROPS.get(Biome.PLAINS).put(Material.POTATOES, 5.0);

        CROPS.get(Biome.BADLANDS).put(Material.COCOA, 10.0);
        CROPS.get(Biome.BADLANDS).put(Material.BEETROOTS, 10.0);
        CROPS.get(Biome.BADLANDS).put(Material.WHEAT, 30.0);
        CROPS.get(Biome.BADLANDS).put(Material.CARROTS, 60.0);
        CROPS.get(Biome.BADLANDS).put(Material.POTATOES, 180.0);
        CROPS.get(Biome.BADLANDS).put(Material.SUGAR_CANE, 10.0);
        CROPS.get(Biome.BADLANDS).put(Material.CACTUS, 20.0);
        CROPS.get(Biome.BADLANDS).put(Material.MELON_STEM, 10.0);
        CROPS.get(Biome.BADLANDS).put(Material.PUMPKIN_STEM, 20.0);
        CROPS.get(Biome.BADLANDS).put(Material.MELON, 10.0);
        CROPS.get(Biome.BADLANDS).put(Material.PUMPKIN, 20.0);
        
        // Populate animal map

    }

    /**
     * Gets the time in milliseconds that it takes to grow a specific crop in a specific biome.
     * @param biome the biome the crop is growing in.
     * @param crop the material type of the crop.
     * @return the time in milliseconds for the crop to grow in the biome.
     */
    public static double getGrowthTime(Biome biome, Material crop) {
        return 1000 * CROPS.getOrDefault(biome, Collections.emptyMap()).getOrDefault(crop, 0.0);
    }

    public static double getBreedingRate(Biome biome, EntityType animal) {
        return ANIMALS.getOrDefault(biome, Collections.emptyMap()).getOrDefault(animal, 100.0);
    }

    public static Material getCropForm(Material seedForm) {
        return SEED_TO_CROP.get(seedForm);
    }

    public static boolean isSeed(Material seedForm) {
        return SEED_TO_CROP.containsKey(seedForm);
    }

    public static boolean isCrop(Material cropForm) {
        return SEED_TO_CROP.containsValue(cropForm);
    }
}