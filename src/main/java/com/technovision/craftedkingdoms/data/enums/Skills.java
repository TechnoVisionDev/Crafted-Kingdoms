package com.technovision.craftedkingdoms.data.enums;

import org.bukkit.Material;

public enum Skills {
    // Farming
    AGRICULTURE("Agriculture", Material.WHEAT, null),
    IRRIGATION("Irrigation", Material.WATER_BUCKET, AGRICULTURE),
    GREENHOUSES("Greenhouses", Material.REDSTONE_LAMP, IRRIGATION),

    // Archery
    ARCHERY("Archery", Material.BOW, null),
    CROSSBOWS("Crossbows", Material.CROSSBOW, ARCHERY),
    BALLISTICS("Ballistics", Material.SPECTRAL_ARROW, CROSSBOWS),

    // Enchanting
    MYSTICISM("Mysticism", Material.ENCHANTING_TABLE, null),
    ASTROLOGY("Astrology", Material.NETHER_STAR, MYSTICISM),
    DARK_ARTS("Dark Arts", Material.WITHER_SKELETON_SKULL, ASTROLOGY),

    // Brewing
    BREWING("Brewing", Material.BARREL, null),
    ALCHEMY("Alchemy", Material.POTION, BREWING),
    CHEMISTRY("Chemistry", Material.LINGERING_POTION, ALCHEMY),

    // Animals
    ANIMAL_HUSBANDRY("Animal Husbandry", Material.PIG_SPAWN_EGG, null),
    HORSEBACK_RIDING("Horseback Riding", Material.HAY_BLOCK, ANIMAL_HUSBANDRY),
    ANIMAL_ARMORS("Animal Armors", Material.DIAMOND_HORSE_ARMOR, HORSEBACK_RIDING),

    // Sailing
    FISHING("Fishing", Material.TROPICAL_FISH, null),
    NAVIGATION("Navigation", Material.OAK_BOAT, FISHING),
    RAILROAD("Railroad", Material.MINECART, NAVIGATION),

    // Blacksmithing
    SMELTING("Smelting", Material.FURNACE, null),
    JEWELCRAFTING("Jewelcrafting", Material.DIAMOND_SWORD, SMELTING),
    ADVANCED_WEAPONRY("Advanced Weaponry", Material.NETHERITE_SWORD, JEWELCRAFTING);

    private String name;
    private Material icon;
    private Skills required;

    Skills(String name, Material icon, Skills required) {
        this.name = name;
        this.icon = icon;
        this.required = required;
    }

    /** Getters */

    public String getName() {
        return name;
    }

    public Material getIcon() {
        return icon;
    }

    public Skills getRequired() {
        return required;
    }

    /** Setters */

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public void setRequired(Skills required) {
        this.required = required;
    }
}
