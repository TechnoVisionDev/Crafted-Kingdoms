package com.technovision.craftedkingdoms.data.enums;

import com.technovision.craftedkingdoms.data.objects.Crop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.EntityType;

import java.util.*;

/**
 * Stores growth and breeding data for realistic biomes.
 *
 * @author TechnoVision
 */
public class BiomeData {

    /** For each biome, maps a crop to the hours it takes to grow */
    public static final Map<Biome, Map<Material, Double>> CROPS = new HashMap<>();

    /** For each biome, maps an animal to the percentage chance for successfully breeding */
    public static final Map<Biome, Map<EntityType, Double>> ANIMALS = new HashMap<>();

    /** Crops that cannot be placed to grow (may change with future updates). */
    public static Set<Material> BLOCKED_CROPS;
    static {
        BLOCKED_CROPS = new HashSet<>();
        BLOCKED_CROPS.add(Material.BAMBOO_SAPLING);
        BLOCKED_CROPS.add(Material.SWEET_BERRY_BUSH);
        BLOCKED_CROPS.add(Material.TORCHFLOWER_CROP);
        BLOCKED_CROPS.add(Material.PITCHER_CROP);
        BLOCKED_CROPS.add(Material.CAVE_VINES);
        BLOCKED_CROPS.add(Material.KELP);
        BLOCKED_CROPS.add(Material.SEA_PICKLE);
        BLOCKED_CROPS.add(Material.RED_MUSHROOM);
        BLOCKED_CROPS.add(Material.BROWN_MUSHROOM);
    }

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

    public static Map<Material, Double> CROP_LOW_LIGHT_MODIFIER;
    static {
        CROP_LOW_LIGHT_MODIFIER = new HashMap<>();
        CROP_LOW_LIGHT_MODIFIER.put(Material.WHEAT, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.CARROTS, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.POTATOES, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.NETHER_WART_BLOCK, 2.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.MELON_STEM, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.PUMPKIN_STEM, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.MELON, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.PUMPKIN, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.BEETROOTS, 4.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.CACTUS, 2.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.SUGAR_CANE, 2.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.COCOA, 5.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.OAK_SAPLING, 8.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.SPRUCE_SAPLING, 8.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.ACACIA_SAPLING, 8.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.BAMBOO_SAPLING, 8.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.BIRCH_SAPLING, 8.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.CHERRY_SAPLING, 8.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.JUNGLE_SAPLING, 8.0);
        CROP_LOW_LIGHT_MODIFIER.put(Material.DARK_OAK_SAPLING, 8.0);
    }

    public static Map<EntityType, Double> ANIMAL_LOW_LIGHT_MODIFIER;
    static {
        ANIMAL_LOW_LIGHT_MODIFIER = new HashMap<>();
        ANIMAL_LOW_LIGHT_MODIFIER.put(EntityType.COW, 8.0);
        ANIMAL_LOW_LIGHT_MODIFIER.put(EntityType.CHICKEN, 1.0);
        ANIMAL_LOW_LIGHT_MODIFIER.put(EntityType.HORSE, 8.0);
        ANIMAL_LOW_LIGHT_MODIFIER.put(EntityType.PIG, 8.0);
        ANIMAL_LOW_LIGHT_MODIFIER.put(EntityType.SHEEP, 8.0);
        ANIMAL_LOW_LIGHT_MODIFIER.put(EntityType.MUSHROOM_COW, 2.0);
        ANIMAL_LOW_LIGHT_MODIFIER.put(EntityType.RABBIT, 8.0);
    }

    static {
        // Initialize maps
        for (Biome biome : Biome.values()) {
            CROPS.put(biome, new HashMap<>());
        }
        for (Biome biome : Biome.values()) {
            ANIMALS.put(biome, new HashMap<>());
        }

        /** Populate crops & animals map */

        // Greenhouse (uses THE_END biome)
        CROPS.get(Biome.THE_END).put(Material.WHEAT, 2.25);
        CROPS.get(Biome.THE_END).put(Material.CARROTS, 2.25);
        CROPS.get(Biome.THE_END).put(Material.POTATOES, 2.25);
        CROPS.get(Biome.THE_END).put(Material.MELON_STEM, 9.0);
        CROPS.get(Biome.THE_END).put(Material.MELON, 9.0);
        CROPS.get(Biome.THE_END).put(Material.PUMPKIN_STEM, 9.0);
        CROPS.get(Biome.THE_END).put(Material.PUMPKIN, 9.0);
        CROPS.get(Biome.THE_END).put(Material.BEETROOTS, 2.25);
        CROPS.get(Biome.THE_END).put(Material.OAK_SAPLING, 4.5);
        CROPS.get(Biome.THE_END).put(Material.SPRUCE_SAPLING, 4.5);
        CROPS.get(Biome.THE_END).put(Material.ACACIA_SAPLING, 4.5);
        CROPS.get(Biome.THE_END).put(Material.BAMBOO_SAPLING, 4.5);
        CROPS.get(Biome.THE_END).put(Material.BIRCH_SAPLING, 4.5);
        CROPS.get(Biome.THE_END).put(Material.CHERRY_SAPLING, 4.5);
        CROPS.get(Biome.THE_END).put(Material.JUNGLE_SAPLING, 4.5);
        CROPS.get(Biome.THE_END).put(Material.DARK_OAK_SAPLING, 4.5);

        // Badlands
        CROPS.get(Biome.BADLANDS).put(Material.CACTUS, 12.0);
        CROPS.get(Biome.BADLANDS).put(Material.ACACIA_SAPLING, 24.0);
        ANIMALS.get(Biome.BADLANDS).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.BADLANDS).put(EntityType.RABBIT, 0.25);

        // Bamboo Jungle
        CROPS.get(Biome.BAMBOO_JUNGLE).put(Material.CARROTS, 1.0);
        CROPS.get(Biome.BAMBOO_JUNGLE).put(Material.MELON_STEM, 8.0);
        CROPS.get(Biome.BAMBOO_JUNGLE).put(Material.MELON, 24.0);
        CROPS.get(Biome.BAMBOO_JUNGLE).put(Material.SUGAR_CANE, 8.0);
        CROPS.get(Biome.BAMBOO_JUNGLE).put(Material.BAMBOO_SAPLING, 3.0);
        CROPS.get(Biome.BAMBOO_JUNGLE).put(Material.JUNGLE_SAPLING, 24.0);
        ANIMALS.get(Biome.BAMBOO_JUNGLE).put(EntityType.PIG, 1.0);

        // Basalt Deltas
        CROPS.get(Biome.BASALT_DELTAS).put(Material.NETHER_WART_BLOCK, 5.33);
        CROPS.get(Biome.BASALT_DELTAS).put(Material.CACTUS, 24.0);

        // Beach
        CROPS.get(Biome.BEACH).put(Material.SUGAR_CANE, 24.0);
        ANIMALS.get(Biome.BEACH).put(EntityType.FISHING_HOOK, 1.0);

        // Birch Forest
        CROPS.get(Biome.BIRCH_FOREST).put(Material.WHEAT, 1.33);
        CROPS.get(Biome.BIRCH_FOREST).put(Material.BEETROOTS, 1.33);
        CROPS.get(Biome.BIRCH_FOREST).put(Material.OAK_SAPLING, 3.0);
        CROPS.get(Biome.BIRCH_FOREST).put(Material.BIRCH_SAPLING, 3.0);
        CROPS.get(Biome.BIRCH_FOREST).put(Material.DARK_OAK_SAPLING, 6.0);
        ANIMALS.get(Biome.BIRCH_FOREST).put(EntityType.CHICKEN, 1.0);

        // Cherry Grove
        CROPS.get(Biome.CHERRY_GROVE).put(Material.WHEAT, 2.0);
        CROPS.get(Biome.CHERRY_GROVE).put(Material.BEETROOTS, 2.0);
        CROPS.get(Biome.CHERRY_GROVE).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.CHERRY_GROVE).put(Material.BIRCH_SAPLING, 12.0);
        CROPS.get(Biome.CHERRY_GROVE).put(Material.DARK_OAK_SAPLING, 6.0);
        ANIMALS.get(Biome.CHERRY_GROVE).put(EntityType.HORSE, 0.10);
        ANIMALS.get(Biome.CHERRY_GROVE).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.CHERRY_GROVE).put(EntityType.RABBIT, 1.0);

        // Oceans
        ANIMALS.get(Biome.OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.COLD_OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.DEEP_COLD_OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.DEEP_LUKEWARM_OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.DEEP_OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.DEEP_FROZEN_OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.FROZEN_OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.WARM_OCEAN).put(EntityType.FISHING_HOOK, 1.0);
        ANIMALS.get(Biome.LUKEWARM_OCEAN).put(EntityType.FISHING_HOOK, 1.0);

        // Crimson Forest (Nether)
        CROPS.get(Biome.CRIMSON_FOREST).put(Material.NETHER_WART_BLOCK, 5.33);
        CROPS.get(Biome.CRIMSON_FOREST).put(Material.CACTUS, 24.0);

        // Dark Forest
        CROPS.get(Biome.DARK_FOREST).put(Material.WHEAT, 2.0);
        CROPS.get(Biome.DARK_FOREST).put(Material.PUMPKIN_STEM, 16.0);
        CROPS.get(Biome.DARK_FOREST).put(Material.PUMPKIN, 48.0);
        CROPS.get(Biome.DARK_FOREST).put(Material.BEETROOTS, 2.0);
        CROPS.get(Biome.DARK_FOREST).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.DARK_FOREST).put(Material.DARK_OAK_SAPLING, 3.0);
        ANIMALS.get(Biome.DARK_FOREST).put(EntityType.CHICKEN, 0.50);
        ANIMALS.get(Biome.DARK_FOREST).put(EntityType.HORSE, 0.10);
        ANIMALS.get(Biome.DARK_FOREST).put(EntityType.SHEEP, 0.50);

        // Deep Dark
        CROPS.get(Biome.DEEP_DARK).put(Material.NETHER_WART, 10.67);

        // Desert
        CROPS.get(Biome.DESERT).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.DESERT).put(Material.CARROTS, 10.0);
        CROPS.get(Biome.DESERT).put(Material.NETHER_WART_BLOCK, 21.33);
        CROPS.get(Biome.DESERT).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.DESERT).put(Material.CACTUS, 12.0);
        CROPS.get(Biome.DESERT).put(Material.ACACIA_SAPLING, 3.0);
        ANIMALS.get(Biome.DESERT).put(EntityType.COW, 0.10);
        ANIMALS.get(Biome.DESERT).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.DESERT).put(EntityType.RABBIT, 0.25);

        // Eroded Badlands
        CROPS.get(Biome.ERODED_BADLANDS).put(Material.CACTUS, 12.0);
        CROPS.get(Biome.ERODED_BADLANDS).put(Material.ACACIA_SAPLING, 24.0);
        ANIMALS.get(Biome.ERODED_BADLANDS).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.ERODED_BADLANDS).put(EntityType.RABBIT, 0.25);

        // Flower Forest
        CROPS.get(Biome.FLOWER_FOREST).put(Material.WHEAT, 1.33);
        CROPS.get(Biome.FLOWER_FOREST).put(Material.BEETROOTS, 1.33);
        CROPS.get(Biome.FLOWER_FOREST).put(Material.OAK_SAPLING, 3.0);
        CROPS.get(Biome.FLOWER_FOREST).put(Material.BIRCH_SAPLING, 3.0);
        CROPS.get(Biome.FLOWER_FOREST).put(Material.DARK_OAK_SAPLING, 6.0);
        ANIMALS.get(Biome.FLOWER_FOREST).put(EntityType.CHICKEN, 1.0);

        // Forest
        CROPS.get(Biome.FOREST).put(Material.WHEAT, 1.33);
        CROPS.get(Biome.FOREST).put(Material.BEETROOTS, 1.33);
        CROPS.get(Biome.FOREST).put(Material.OAK_SAPLING, 3.0);
        CROPS.get(Biome.FOREST).put(Material.BIRCH_SAPLING, 3.0);
        CROPS.get(Biome.FOREST).put(Material.DARK_OAK_SAPLING, 6.0);
        ANIMALS.get(Biome.FOREST).put(EntityType.CHICKEN, 1.0);

        // Frozen Peaks
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.SPRUCE_SAPLING, 8.0);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.RABBIT, 0.25);

        // Frozen River
        CROPS.get(Biome.FROZEN_RIVER).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.FROZEN_RIVER).put(Material.SPRUCE_SAPLING, 12.0);
        CROPS.get(Biome.FROZEN_RIVER).put(Material.ACACIA_SAPLING, 12.0);
        CROPS.get(Biome.FROZEN_RIVER).put(Material.BIRCH_SAPLING, 12.0);
        CROPS.get(Biome.FROZEN_RIVER).put(Material.DARK_OAK_SAPLING, 12.0);

        // Grove
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.FROZEN_PEAKS).put(Material.SPRUCE_SAPLING, 8.0);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.FROZEN_PEAKS).put(EntityType.RABBIT, 0.25);

        // Ice Spikes
        CROPS.get(Biome.ICE_SPIKES).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.ICE_SPIKES).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.ICE_SPIKES).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.ICE_SPIKES).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.ICE_SPIKES).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.ICE_SPIKES).put(Material.SPRUCE_SAPLING, 8.0);
        ANIMALS.get(Biome.ICE_SPIKES).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.ICE_SPIKES).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.ICE_SPIKES).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.ICE_SPIKES).put(EntityType.RABBIT, 0.25);

        // Jagged Peaks
        ANIMALS.get(Biome.JAGGED_PEAKS).put(EntityType.SHEEP, 0.25);

        // Jungle
        CROPS.get(Biome.JUNGLE).put(Material.CARROTS, 1.0);
        CROPS.get(Biome.JUNGLE).put(Material.MELON_STEM, 8.0);
        CROPS.get(Biome.JUNGLE).put(Material.MELON, 24.0);
        CROPS.get(Biome.JUNGLE).put(Material.SUGAR_CANE, 12.0);
        CROPS.get(Biome.JUNGLE).put(Material.COCOA, 2.82);
        CROPS.get(Biome.JUNGLE).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.JUNGLE).put(Material.JUNGLE_SAPLING, 6.0);
        ANIMALS.get(Biome.JUNGLE).put(EntityType.COW, 0.10);
        ANIMALS.get(Biome.JUNGLE).put(EntityType.HORSE, 0.10);
        ANIMALS.get(Biome.JUNGLE).put(EntityType.PIG, 1.0);

        // Lush Caves
        CROPS.get(Biome.LUSH_CAVES).put(Material.NETHER_WART, 10.67);

        // Mangrove Swamp
        CROPS.get(Biome.MANGROVE_SWAMP).put(Material.CARROTS, 2.0);
        CROPS.get(Biome.MANGROVE_SWAMP).put(Material.MELON_STEM, 4.0);
        CROPS.get(Biome.MANGROVE_SWAMP).put(Material.MELON, 12.0);
        CROPS.get(Biome.MANGROVE_SWAMP).put(Material.SUGAR_CANE, 24.0);
        CROPS.get(Biome.MANGROVE_SWAMP).put(Material.BAMBOO_SAPLING, 12.0);
        CROPS.get(Biome.MANGROVE_SWAMP).put(Material.JUNGLE_SAPLING, 12.0);
        CROPS.get(Biome.MANGROVE_SWAMP).put(Material.DARK_OAK_SAPLING, 3.0);
        ANIMALS.get(Biome.MANGROVE_SWAMP).put(EntityType.PIG, 1.0);

        // Meadow
        CROPS.get(Biome.MEADOW).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.MEADOW).put(Material.CARROTS, 1.0);
        CROPS.get(Biome.MEADOW).put(Material.POTATOES, 2.0);
        CROPS.get(Biome.MEADOW).put(Material.MELON_STEM, 16.0);
        CROPS.get(Biome.MEADOW).put(Material.MELON, 48.0);
        CROPS.get(Biome.MEADOW).put(Material.PUMPKIN_STEM, 16.0);
        CROPS.get(Biome.MEADOW).put(Material.PUMPKIN, 48.0);
        CROPS.get(Biome.MEADOW).put(Material.BEETROOTS, 1.0);
        CROPS.get(Biome.MEADOW).put(Material.OAK_SAPLING, 24.0);
        CROPS.get(Biome.MEADOW).put(Material.BIRCH_SAPLING, 24.0);
        ANIMALS.get(Biome.MEADOW).put(EntityType.COW, 1.0);
        ANIMALS.get(Biome.MEADOW).put(EntityType.CHICKEN, 1.0);
        ANIMALS.get(Biome.MEADOW).put(EntityType.HORSE, 0.50);
        ANIMALS.get(Biome.MEADOW).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.MEADOW).put(EntityType.RABBIT, 1.0);

        // Mushroom Fields
        CROPS.get(Biome.MUSHROOM_FIELDS).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.MUSHROOM_FIELDS).put(Material.NETHER_WART_BLOCK, 10.67);
        CROPS.get(Biome.MUSHROOM_FIELDS).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.MUSHROOM_FIELDS).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.MUSHROOM_FIELDS).put(Material.SPRUCE_SAPLING, 12.0);
        CROPS.get(Biome.MUSHROOM_FIELDS).put(Material.BIRCH_SAPLING, 12.0);
        ANIMALS.get(Biome.MUSHROOM_FIELDS).put(EntityType.COW, 1.0);
        ANIMALS.get(Biome.MUSHROOM_FIELDS).put(EntityType.CHICKEN, 1.0);
        ANIMALS.get(Biome.MUSHROOM_FIELDS).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.MUSHROOM_FIELDS).put(EntityType.PIG, 1.0);
        ANIMALS.get(Biome.MUSHROOM_FIELDS).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.MUSHROOM_FIELDS).put(EntityType.MUSHROOM_COW, 1.0);

        // Nether Wastes (Nether)
        CROPS.get(Biome.NETHER_WASTES).put(Material.NETHER_WART_BLOCK, 5.33);
        CROPS.get(Biome.NETHER_WASTES).put(Material.CACTUS, 24.0);

        // Old Growth Birch Forest
        CROPS.get(Biome.OLD_GROWTH_BIRCH_FOREST).put(Material.WHEAT, 1.33);
        CROPS.get(Biome.OLD_GROWTH_BIRCH_FOREST).put(Material.BEETROOTS, 1.33);
        CROPS.get(Biome.OLD_GROWTH_BIRCH_FOREST).put(Material.OAK_SAPLING, 3.0);
        CROPS.get(Biome.OLD_GROWTH_BIRCH_FOREST).put(Material.BIRCH_SAPLING, 3.0);
        CROPS.get(Biome.OLD_GROWTH_BIRCH_FOREST).put(Material.DARK_OAK_SAPLING, 6.0);
        ANIMALS.get(Biome.OLD_GROWTH_BIRCH_FOREST).put(EntityType.CHICKEN, 1.0);

        // Old Growth Pine Taiga
        CROPS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(Material.SPRUCE_SAPLING, 8.0);
        CROPS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(Material.DARK_OAK_SAPLING, 12.0);
        ANIMALS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(EntityType.SHEEP, 1.0);
        ANIMALS.get(Biome.OLD_GROWTH_PINE_TAIGA).put(EntityType.RABBIT, 0.25);

        // Old Growth Spruce Taiga
        CROPS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(Material.SPRUCE_SAPLING, 8.0);
        CROPS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(Material.DARK_OAK_SAPLING, 12.0);
        ANIMALS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(EntityType.SHEEP, 1.0);
        ANIMALS.get(Biome.OLD_GROWTH_SPRUCE_TAIGA).put(EntityType.RABBIT, 0.25);

        // Plains
        CROPS.get(Biome.PLAINS).put(Material.WHEAT, 1.0);
        CROPS.get(Biome.PLAINS).put(Material.CARROTS, 4.0);
        CROPS.get(Biome.PLAINS).put(Material.POTATOES, 2.0);
        CROPS.get(Biome.PLAINS).put(Material.MELON_STEM, 16.0);
        CROPS.get(Biome.PLAINS).put(Material.MELON, 48.0);
        CROPS.get(Biome.PLAINS).put(Material.PUMPKIN_STEM, 16.0);
        CROPS.get(Biome.PLAINS).put(Material.PUMPKIN, 48.0);
        CROPS.get(Biome.PLAINS).put(Material.BEETROOTS, 1.0);
        CROPS.get(Biome.PLAINS).put(Material.OAK_SAPLING, 24.0);
        CROPS.get(Biome.PLAINS).put(Material.ACACIA_SAPLING, 6.0);
        CROPS.get(Biome.PLAINS).put(Material.BIRCH_SAPLING, 24.0);
        ANIMALS.get(Biome.PLAINS).put(EntityType.COW, 1.0);
        ANIMALS.get(Biome.PLAINS).put(EntityType.CHICKEN, 0.50);
        ANIMALS.get(Biome.PLAINS).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.PLAINS).put(EntityType.PIG, 0.50);
        ANIMALS.get(Biome.PLAINS).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.PLAINS).put(EntityType.RABBIT, 1.0);

        // River
        CROPS.get(Biome.RIVER).put(Material.SUGAR_CANE, 24.0);
        CROPS.get(Biome.RIVER).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.RIVER).put(Material.SPRUCE_SAPLING, 12.0);
        CROPS.get(Biome.RIVER).put(Material.ACACIA_SAPLING, 12.0);
        CROPS.get(Biome.RIVER).put(Material.BIRCH_SAPLING, 12.0);
        CROPS.get(Biome.RIVER).put(Material.DARK_OAK_SAPLING, 12.0);
        ANIMALS.get(Biome.RIVER).put(EntityType.FISHING_HOOK, 1.0);

        // Savanna
        CROPS.get(Biome.SAVANNA).put(Material.CARROTS, 4.0);
        CROPS.get(Biome.SAVANNA).put(Material.POTATOES, 2.0);
        CROPS.get(Biome.SAVANNA).put(Material.MELON_STEM, 16.0);
        CROPS.get(Biome.SAVANNA).put(Material.MELON, 48.0);
        CROPS.get(Biome.SAVANNA).put(Material.PUMPKIN_STEM, 16.0);
        CROPS.get(Biome.SAVANNA).put(Material.PUMPKIN, 48.0);
        CROPS.get(Biome.SAVANNA).put(Material.ACACIA_SAPLING, 6.0);
        ANIMALS.get(Biome.SAVANNA).put(EntityType.COW, 1.0);
        ANIMALS.get(Biome.SAVANNA).put(EntityType.CHICKEN, 0.50);
        ANIMALS.get(Biome.SAVANNA).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.SAVANNA).put(EntityType.PIG, 0.50);
        ANIMALS.get(Biome.SAVANNA).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.SAVANNA).put(EntityType.RABBIT, 1.0);

        // Savanna Plateau
        CROPS.get(Biome.SAVANNA_PLATEAU).put(Material.CARROTS, 4.0);
        CROPS.get(Biome.SAVANNA_PLATEAU).put(Material.POTATOES, 2.0);
        CROPS.get(Biome.SAVANNA_PLATEAU).put(Material.MELON_STEM, 16.0);
        CROPS.get(Biome.SAVANNA_PLATEAU).put(Material.MELON, 48.0);
        CROPS.get(Biome.SAVANNA_PLATEAU).put(Material.PUMPKIN_STEM, 16.0);
        CROPS.get(Biome.SAVANNA_PLATEAU).put(Material.PUMPKIN, 48.0);
        CROPS.get(Biome.SAVANNA_PLATEAU).put(Material.ACACIA_SAPLING, 6.0);
        ANIMALS.get(Biome.SAVANNA_PLATEAU).put(EntityType.COW, 1.0);
        ANIMALS.get(Biome.SAVANNA_PLATEAU).put(EntityType.CHICKEN, 0.50);
        ANIMALS.get(Biome.SAVANNA_PLATEAU).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.SAVANNA_PLATEAU).put(EntityType.PIG, 0.50);
        ANIMALS.get(Biome.SAVANNA_PLATEAU).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.SAVANNA_PLATEAU).put(EntityType.RABBIT, 1.0);

        // Snowy Beach
        ANIMALS.get(Biome.SNOWY_BEACH).put(EntityType.FISHING_HOOK, 1.0);

        // Snowy Plains
        CROPS.get(Biome.SNOWY_PLAINS).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.SNOWY_PLAINS).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.SNOWY_PLAINS).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.SNOWY_PLAINS).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.SNOWY_PLAINS).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.SNOWY_PLAINS).put(Material.SPRUCE_SAPLING, 12.0);
        ANIMALS.get(Biome.SNOWY_PLAINS).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.SNOWY_PLAINS).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.SNOWY_PLAINS).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.SNOWY_PLAINS).put(EntityType.RABBIT, 0.50);

        // Snowy Slopes
        ANIMALS.get(Biome.SNOWY_SLOPES).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.SNOWY_SLOPES).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.SNOWY_SLOPES).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.SNOWY_SLOPES).put(EntityType.RABBIT, 0.50);

        // Snowy Taiga
        CROPS.get(Biome.SNOWY_TAIGA).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.SNOWY_TAIGA).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.SNOWY_TAIGA).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.SNOWY_TAIGA).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.SNOWY_TAIGA).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.SNOWY_TAIGA).put(Material.SPRUCE_SAPLING, 8.0);
        CROPS.get(Biome.SNOWY_TAIGA).put(Material.DARK_OAK_SAPLING, 12.0);
        ANIMALS.get(Biome.SNOWY_TAIGA).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.SNOWY_TAIGA).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.SNOWY_TAIGA).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.SNOWY_TAIGA).put(EntityType.RABBIT, 0.25);

        // Soul Sand Valley (Nether)
        CROPS.get(Biome.SOUL_SAND_VALLEY).put(Material.NETHER_WART_BLOCK, 5.33);
        CROPS.get(Biome.SOUL_SAND_VALLEY).put(Material.CACTUS, 24.0);

        // Sparse Jungle
        CROPS.get(Biome.SPARSE_JUNGLE).put(Material.CARROTS, 1.0);
        CROPS.get(Biome.SPARSE_JUNGLE).put(Material.MELON_STEM, 8.0);
        CROPS.get(Biome.SPARSE_JUNGLE).put(Material.MELON, 24.0);
        CROPS.get(Biome.SPARSE_JUNGLE).put(Material.SUGAR_CANE, 12.0);
        CROPS.get(Biome.SPARSE_JUNGLE).put(Material.COCOA, 2.82);
        CROPS.get(Biome.SPARSE_JUNGLE).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.SPARSE_JUNGLE).put(Material.JUNGLE_SAPLING, 6.0);
        ANIMALS.get(Biome.SPARSE_JUNGLE).put(EntityType.COW, 0.10);
        ANIMALS.get(Biome.SPARSE_JUNGLE).put(EntityType.HORSE, 0.10);
        ANIMALS.get(Biome.SPARSE_JUNGLE).put(EntityType.PIG, 1.0);

        // Stony Peaks
        ANIMALS.get(Biome.STONY_PEAKS).put(EntityType.SHEEP, 0.25);

        // Stony Shore
        CROPS.get(Biome.STONY_SHORE).put(Material.SUGAR_CANE, 32.0);
        ANIMALS.get(Biome.STONY_SHORE).put(EntityType.FISHING_HOOK, 1.0);

        // Sunflower Plains
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.CARROTS, 1.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.POTATOES, 2.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.MELON_STEM, 16.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.MELON, 48.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.PUMPKIN_STEM, 16.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.PUMPKIN, 48.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.BEETROOTS, 1.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.OAK_SAPLING, 24.0);
        CROPS.get(Biome.SUNFLOWER_PLAINS).put(Material.BIRCH_SAPLING, 24.0);
        ANIMALS.get(Biome.SUNFLOWER_PLAINS).put(EntityType.COW, 1.0);
        ANIMALS.get(Biome.SUNFLOWER_PLAINS).put(EntityType.CHICKEN, 1.0);
        ANIMALS.get(Biome.SUNFLOWER_PLAINS).put(EntityType.HORSE, 0.50);
        ANIMALS.get(Biome.SUNFLOWER_PLAINS).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.SUNFLOWER_PLAINS).put(EntityType.RABBIT, 1.0);

        // Swamp
        CROPS.get(Biome.SWAMP).put(Material.CARROTS, 2.0);
        CROPS.get(Biome.SWAMP).put(Material.MELON_STEM, 4.0);
        CROPS.get(Biome.SWAMP).put(Material.MELON, 12.0);
        CROPS.get(Biome.SWAMP).put(Material.SUGAR_CANE, 24.0);
        CROPS.get(Biome.SWAMP).put(Material.BAMBOO_SAPLING, 12.0);
        CROPS.get(Biome.SWAMP).put(Material.JUNGLE_SAPLING, 12.0);
        CROPS.get(Biome.SWAMP).put(Material.DARK_OAK_SAPLING, 3.0);
        ANIMALS.get(Biome.SWAMP).put(EntityType.PIG, 1.0);

        // Taiga
        CROPS.get(Biome.TAIGA).put(Material.WHEAT, 4.0);
        CROPS.get(Biome.TAIGA).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.TAIGA).put(Material.PUMPKIN_STEM, 4.0);
        CROPS.get(Biome.TAIGA).put(Material.PUMPKIN, 12.0);
        CROPS.get(Biome.TAIGA).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.TAIGA).put(Material.SPRUCE_SAPLING, 8.0);
        CROPS.get(Biome.TAIGA).put(Material.DARK_OAK_SAPLING, 12.0);
        ANIMALS.get(Biome.TAIGA).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.TAIGA).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.TAIGA).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.TAIGA).put(EntityType.RABBIT, 0.25);

        // Warped Forest (Nether)
        CROPS.get(Biome.WARPED_FOREST).put(Material.NETHER_WART_BLOCK, 5.33);
        CROPS.get(Biome.WARPED_FOREST).put(Material.CACTUS, 24.0);

        // Windswept Forest
        CROPS.get(Biome.WINDSWEPT_FOREST).put(Material.WHEAT, 2.0);
        CROPS.get(Biome.WINDSWEPT_FOREST).put(Material.POTATOES, 4.0);
        CROPS.get(Biome.WINDSWEPT_FOREST).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.WINDSWEPT_FOREST).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.WINDSWEPT_FOREST).put(Material.SPRUCE_SAPLING, 8.0);
        CROPS.get(Biome.WINDSWEPT_FOREST).put(Material.DARK_OAK_SAPLING, 12.0);
        ANIMALS.get(Biome.WINDSWEPT_FOREST).put(EntityType.COW, 0.50);
        ANIMALS.get(Biome.WINDSWEPT_FOREST).put(EntityType.HORSE, 0.25);
        ANIMALS.get(Biome.WINDSWEPT_FOREST).put(EntityType.SHEEP, 1.0);
        ANIMALS.get(Biome.WINDSWEPT_FOREST).put(EntityType.RABBIT, 1.0);

        // Windswept Hills
        CROPS.get(Biome.WINDSWEPT_HILLS).put(Material.BEETROOTS, 4.0);
        CROPS.get(Biome.WINDSWEPT_HILLS).put(Material.OAK_SAPLING, 12.0);
        CROPS.get(Biome.WINDSWEPT_HILLS).put(Material.SPRUCE_SAPLING, 12.0);
        ANIMALS.get(Biome.WINDSWEPT_HILLS).put(EntityType.COW, 0.25);
        ANIMALS.get(Biome.WINDSWEPT_HILLS).put(EntityType.HORSE, 0.50);
        ANIMALS.get(Biome.WINDSWEPT_HILLS).put(EntityType.SHEEP, 0.50);
        ANIMALS.get(Biome.WINDSWEPT_HILLS).put(EntityType.RABBIT, 0.25);

        // Windswept Savanna
        CROPS.get(Biome.WINDSWEPT_SAVANNA).put(Material.CARROTS, 4.0);
        CROPS.get(Biome.WINDSWEPT_SAVANNA).put(Material.POTATOES, 2.0);
        CROPS.get(Biome.WINDSWEPT_SAVANNA).put(Material.MELON_STEM, 16.0);
        CROPS.get(Biome.WINDSWEPT_SAVANNA).put(Material.MELON, 48.0);
        CROPS.get(Biome.WINDSWEPT_SAVANNA).put(Material.PUMPKIN_STEM, 16.0);
        CROPS.get(Biome.WINDSWEPT_SAVANNA).put(Material.PUMPKIN, 48.0);
        CROPS.get(Biome.WINDSWEPT_SAVANNA).put(Material.ACACIA_SAPLING, 6.0);
        ANIMALS.get(Biome.WINDSWEPT_SAVANNA).put(EntityType.COW, 1.0);
        ANIMALS.get(Biome.WINDSWEPT_SAVANNA).put(EntityType.CHICKEN, 0.50);
        ANIMALS.get(Biome.WINDSWEPT_SAVANNA).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.WINDSWEPT_SAVANNA).put(EntityType.PIG, 0.50);
        ANIMALS.get(Biome.WINDSWEPT_SAVANNA).put(EntityType.SHEEP, 0.25);
        ANIMALS.get(Biome.WINDSWEPT_SAVANNA).put(EntityType.RABBIT, 1.0);

        // Wooded Badlands
        CROPS.get(Biome.WOODED_BADLANDS).put(Material.WHEAT, 8.0);
        CROPS.get(Biome.WOODED_BADLANDS).put(Material.CACTUS, 24.0);
        CROPS.get(Biome.WOODED_BADLANDS).put(Material.ACACIA_SAPLING, 3.0);
        ANIMALS.get(Biome.WOODED_BADLANDS).put(EntityType.CHICKEN, 0.25);
        ANIMALS.get(Biome.WOODED_BADLANDS).put(EntityType.HORSE, 1.0);
        ANIMALS.get(Biome.WOODED_BADLANDS).put(EntityType.RABBIT, 0.50);
    }

    /**
     * Gets the time in milliseconds that it takes to grow a specific crop in a specific biome.
     * @param crop the crop object
     * @return the time in milliseconds for the crop to grow in the biome.
     */
    public static double getGrowthTime(Crop crop) {
        // Get base growth time
        Material cropType = Material.valueOf(crop.getMaterial());
        Location cropLocation = crop.getBlockCoord().asLocation();
        Biome biome = cropLocation.getBlock().getBiome();
        double growthTime = CROPS.getOrDefault(biome, Collections.emptyMap()).getOrDefault(cropType, -1.0);
        if (growthTime == 0) return 0;
        if (growthTime < 0) return -1;

        // Apply low light modifier
        if (!hasFullSunlight(cropLocation)) {
            double modifier = CROP_LOW_LIGHT_MODIFIER.get(cropType);
            if (modifier > 0) {
                if (cropType == Material.NETHER_WART_BLOCK) {
                    growthTime /= modifier;
                } else {
                    growthTime *= modifier;
                }
            }
        }

        // Check if block is adjacent to lamp or glowstone
        if (isGreenhouseCrop(cropLocation)) {
            double greenhouseRate = CROPS.getOrDefault(Biome.THE_END, Collections.emptyMap()).getOrDefault(cropType, -1.0);
            if (greenhouseRate > -1 && greenhouseRate < growthTime) {
                growthTime = greenhouseRate;
            }
        }

        // Apply fertilizer bonus
        int fertilizer = countFertilizerBlocks(crop);
        if (fertilizer > 0) {
            double discountFactor = 1 + (0.25 * fertilizer);
            growthTime = growthTime / discountFactor;
        }

        // Return growth time in hours
        return 60 * 60 * 1000 * growthTime;
    }

    public static int countFertilizerBlocks(Crop crop) {
        // default fertilizer block material
        Material cropMaterial = Material.valueOf(crop.getMaterial());
        Material targetMaterialToCount = Material.CLAY;

        if (cropMaterial == Material.NETHER_WART_BLOCK) {
            // Nether Wart uses Soul Sand as fertilizer
            targetMaterialToCount = Material.SOUL_SAND;
        } else if (cropMaterial == Material.COCOA) {
            // Cocoa uses Vine as fertilizer
            targetMaterialToCount = Material.VINE;
        }

        // Start one block below the crop and loop to
        Block currentBlock = crop.getBlockCoord().asLocation().getBlock().getRelative(0, -1, 0);
        if (cropMaterial == Material.COCOA) {
            currentBlock = crop.getBlockCoord().asLocation().getBlock();
        }

        // Attempt to find up to 4 fertilizer blocks
        int blockCount = 0;
        for (int i = 0; i < 4; i++) {
            currentBlock = currentBlock.getRelative(0, -1, 0);
            if (currentBlock.getType() == targetMaterialToCount) {
                blockCount++;
            } else {
                break;
            }
        }
        return blockCount;
    }

    public static boolean isValidCropLocation(Crop crop, Block clickedBlock) {
        Material type = Material.valueOf(crop.getMaterial());
        Block soil = crop.getBlockCoord().asLocation().getBlock().getRelative(0, -1, 0);

        if (type == Material.NETHER_WART_BLOCK) {
            return soil.getType() == Material.SOUL_SAND;
        }
        else if (type == Material.COCOA) {
            return clickedBlock.getType() == Material.JUNGLE_LOG;
        }
        return (soil.getType() == Material.FARMLAND);
    }

    public static boolean isSapling(Material itemType) {
        String materialName = itemType.name();
        return materialName.endsWith("_SAPLING");
    }

    /**
     * Checks if a greenhouse block (redstone lamp or glowstone) is adjacent to crop.
     * @param location the location of the crop block.
     * @return
     */
    public static boolean isGreenhouseCrop(Location location) {
        Block block = location.getBlock();
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

        for (BlockFace face : faces) {
            Block adjacentBlock = block.getRelative(face);
            Material adjacentType = adjacentBlock.getType();

            if (adjacentType == Material.GLOWSTONE) {
                return true;
            }

            if (adjacentType == Material.REDSTONE_LAMP) {
                // Check if the Redstone Lamp is 'on'
                BlockData blockData = adjacentBlock.getBlockData();
                if (blockData instanceof Lightable lightable) {
                    if (lightable.isLit()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks the light level that the sun provides to a crop location.
     * @param location the location of a crop block.
     * @return true if the crop has full access, otherwise false.
     */
    public static boolean hasFullSunlight(Location location) {
        int lightLevel = location.getBlock().getLightFromSky();
        return lightLevel == 15;
    }

    public static double getBreedingChance(Biome biome, EntityType animal) {
        return ANIMALS.getOrDefault(biome, Collections.emptyMap()).getOrDefault(animal, 0.0);
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