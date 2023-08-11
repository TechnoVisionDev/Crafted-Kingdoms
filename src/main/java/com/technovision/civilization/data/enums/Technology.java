package com.technovision.civilization.data.enums;

import org.bukkit.Material;

import java.util.List;

/**
 * Enums containing data for each researchable technology.
 *
 * @author TechnoVision.
 */
public enum Technology {
    // Ancient Era (1)
    MINING("Mining", 1000, 60, 1, Material.IRON_PICKAXE, List.of()),
    WOODCUTTING("Woodcutting", 1000, 60, 1, Material.IRON_AXE, List.of()),
    AGRICULTURE("Agriculture", 1000, 60, 1, Material.WHEAT, List.of()),
    ASTROLOGY("Astrology", 1000, 60, 1, Material.NETHER_STAR, List.of()),
    POTTERY("Pottery", 1000, 60, 1, Material.FLOWER_POT, List.of()),
    WRITING("Writing", 2500, 100, 1, Material.BOOK, List.of(POTTERY)),
    FISHING("Fishing", 1000, 60, 1, Material.TROPICAL_FISH, List.of()),
    THE_WHEEL("The Wheel", 2500, 100, 1, Material.WATER, List.of(MINING)),
    ARCHERY("Archery", 2500, 100, 1, Material.BOW, List.of(WOODCUTTING)),
    ANIMAL_HUSBANDRY("Animal Husbandry", 2500, 100, 1, Material.EGG, List.of(AGRICULTURE)),
    BREWING("Brewing", 2500, 100, 1, Material.GLASS_BOTTLE, List.of(AGRICULTURE, ASTROLOGY)),
    TRADING("Trading", 2500, 100, 1, Material.GOLD_NUGGET, List.of(POTTERY)),

    // Classical Era (2)
    MASONRY("Masonry", 5000, 150, 2, Material.STONE_BRICK_STAIRS, List.of(MINING)),
    IRON_WORKING("Iron Working", 5000, 150, 2, Material.IRON_SWORD, List.of(THE_WHEEL, WOODCUTTING)),
    HORSEBACK_RIDING("Horseback Riding", 8000, 180, 2, Material.LEAD, List.of(ANIMAL_HUSBANDRY, ARCHERY)),
    FERMENTATION("Fermentation", 0, 0, 2, Material.POISONOUS_POTATO, List.of(BREWING)),
    DARK_ARTS("Dark Arts", 0, 0, 2, Material.CRYING_OBSIDIAN, List.of(ASTROLOGY)),
    CURRENCY("Iron Working", 0, 0, 2, Material.GOLD_INGOT, List.of(TRADING, WRITING)),
    CODE_OF_LAWS("Code of Laws", 0, 0, 2, Material.BOOKSHELF, List.of(WRITING)),
    SAILING("Sailing", 0, 0, 2, Material.OAK_BOAT, List.of(FISHING, ASTROLOGY)),

    // Medieval Era (3)
    ENGINEERING("Engineering", 0, 0, 2, Material.PISTON, List.of(MASONRY)),
    BLACKSMITHING("Blacksmithing", 0, 0, 2, Material.ANVIL, List.of(MASONRY, IRON_WORKING)),
    DIVINE_RIGHT("Divine Right", 0, 0, 2, Material.GOLDEN_HELMET, List.of(IRON_WORKING, HORSEBACK_RIDING)),
    ALCHEMY("Alchemy", 0, 0, 2, Material.LAVA_BUCKET, List.of(FERMENTATION, DARK_ARTS)),
    LITERATURE("Literature", 0, 0, 2, Material.ENCHANTED_BOOK, List.of(CODE_OF_LAWS, DARK_ARTS)),
    NAVIGATION("Navigation", 0, 0, 3, Material.COMPASS, List.of(SAILING, WRITING));

    private String name;
    private int moneyCost;
    private int scienceCost;
    private int age;
    private Material icon;
    private List<Technology> required;

    Technology(String name, int moneyCost, int scienceCost, int age, Material icon, List<Technology> required) {
        this.name = name;
        this.moneyCost = moneyCost;
        this.scienceCost = scienceCost;
        this.age = age;
        this.icon = icon;
        this.required = required;
    }

    public static Technology getByName(String name) {
        for (Technology tech : Technology.values()) {
            if (tech.name.equalsIgnoreCase(name)) {
                return tech;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoneyCost() {
        return moneyCost;
    }

    public void setMoneyCost(int moneyCost) {
        this.moneyCost = moneyCost;
    }

    public int getScienceCost() {
        return scienceCost;
    }

    public void setScienceCost(int scienceCost) {
        this.scienceCost = scienceCost;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public List<Technology> getRequired() {
        return required;
    }

    public void setRequired(List<Technology> required) {
        this.required = required;
    }
}
