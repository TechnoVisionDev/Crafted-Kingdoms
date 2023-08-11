package com.technovision.civilization.data.enums;

/**
 * Enums containing tile yields for each biome type
 *
 * @author TechnoVision.
 */
public enum BiomeYields {
    PLAINS(1, 3, 0.035, 1),
    SUNFLOWER_PLAINS(1.25, 1.25, 0.05, 1.25),
    SNOWY_PLAINS(0.5, 0.5, 0.03, 1.2),
    ICE_SPIKES(0.25, 0, 0.015, 3),
    DESERT(0.5, 0.5, 0.03, 1.2),
    SWAMP(0.25, 1, 0.02, 1.5),
    MANGROVE_SWAMP(0.25, 1, 0.02, 1.75),
    FOREST(1, 1.25, 0.025, 0.5),
    FLOWER_FOREST(1, 1, 0.05, 0.75),
    BIRCH_FOREST(1.5, 0.75, 0.025, 0.5),
    DARK_FOREST(1, 0.75, 0.025, 1),
    OLD_GROWTH_BIRCH_FOREST(1, 1.25, 0.025, 0.5),
    OLD_GROWTH_PINE_TAIGA(1, 1.25, 0.025, 0.5),
    OLD_GROWTH_SPRUCE_TAIGA(1, 1.25, 0.025, 0.5),
    TAIGA(2, 0.5, 0.02, 0.5),
    SNOWY_TAIGA(1.5, 0.25, 0.025, 1.5),
    SAVANNA(1.15, 1.5, 0.03, 1.15),
    SAVANNA_PLATEAU(1.25, 1.25, 0.025, 1),
    WINDSWEPT_GRAVELLY_HILLS(4, 0.25, 0, 0.5),
    WINDSWEPT_FOREST(2, 0.5, 0.02, 0.75),
    WINDSWEPT_SAVANNA(2, 1, 0.03, 1.25),
    JUNGLE(1.5, 1.5, 0.02, 1.25),
    SPARSE_JUNGLE(1.5, 1, 0.05, 1.25),
    BAMBOO_JUNGLE(2, 1.5, 0.05, 1),
    BADLANDS(2, 0, 0.025, 1.5),
    ERODED_BADLANDS(2, 0.5, 0.01, 0.25),
    WOODED_BADLANDS(2, 0.8, 0.05, 0.5),
    MEADOW(1, 2, 0.06, 0.25),
    CHERRY_GROVE(1.5, 1.5, 0.05, 0.25),
    GROVE(1, 2, 0.06, 0.25),
    SNOWY_SLOPES(2, 0.5, 0.05, 1),
    FROZEN_PEAKS(3, 0.25, 0, 0.25),
    JAGGED_PEAKS(3, 0.25, 0.01, 0.25),
    STONY_PEAKS(3.5, 0.25, 0.01, 0.25),
    RIVER(2, 6, 0.03, 0.5),
    FROZEN_RIVER(1.5, 0, 0.04, 3),
    BEACH(0.25, 1.5, 0.06, 1.5),
    SNOWY_BEACH(0.25, 0.5, 0.02, 2),
    STONE_SHORE(3, 0, 0.015, 0.15),
    WARM_OCEAN(1, 0.25, 0.02, 1.25),
    LUKEWARM_OCEAN(1, 1.5, 0.05, 1.25),
    DEEP_LUKEWARM_OCEAN(1, 1.5, 0.05, 1.25),
    OCEAN(1, 0.25, 0.02, 1.25),
    DEEP_OCEAN(1.5, 1.25, 0.02, 1.5),
    COLD_OCEAN(1.5, 0.5, 0.01, 1.5),
    DEEP_COLD_OCEAN(1.5, 1, 0.01, 1.5),
    FROZEN_OCEAN(0, 0, 0, 2),
    DEEP_FROZEN_OCEAN(0, 0, 0, 2),
    MUSHROOM_FIELDS(2, 0.5, 0.03, 2),
    DRIPSTONE_CAVES(1.5, 1, 0.02, 1.5),
    LUSH_CAVES(1.5, 1.5, 0.02, 1),
    DEEP_DARK(1, 0.25, 0.01, 3.5),
    OTHER(0.25, 0.25, 0.01, 0.25);


    private double production;
    private double growth;
    private double happiness;
    private double science;

    BiomeYields(double production, double growth, double happiness, double science) {
        this.production = production;
        this.growth = growth;
        this.happiness = happiness;
        this.science = science;
    }

    public double getProduction() {
        return production;
    }

    public double getGrowth() {
        return growth;
    }

    public double getHappiness() {
        return happiness;
    }

    public double getScience() {
        return science;
    }
}
