package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * POJO object that stores data for a block that has been fortified.
 *
 * @author TechnoVision
 */
public class FortifiedBlock {

    public static final int STONE_FORTIFY = 3;
    public static final int IRON_FORTIFY = 5;
    public static final int DIAMOND_FORTIFY = 8;
    public static final int NETHERITE_FORTIFY = 10;

    public static Map<Material, Integer> MATERIALS;
    static {
        MATERIALS = new HashMap<>();
        MATERIALS.put(Material.STONE, STONE_FORTIFY);
        MATERIALS.put(Material.IRON_INGOT, IRON_FORTIFY);
        MATERIALS.put(Material.DIAMOND, DIAMOND_FORTIFY);
        MATERIALS.put(Material.NETHERITE_INGOT, NETHERITE_FORTIFY);
    }

    public static Set<Material> INVALID_BLOCKS = Set.of(
            Material.BEDROCK,
            Material.END_PORTAL_FRAME,
            Material.END_PORTAL,
            Material.NETHER_PORTAL,
            Material.ACACIA_SAPLING,
            Material.BAMBOO_SAPLING,
            Material.OAK_SAPLING,
            Material.SPRUCE_SAPLING,
            Material.BIRCH_SAPLING,
            Material.CHERRY_SAPLING,
            Material.DARK_OAK_SAPLING,
            Material.JUNGLE_SAPLING,
            Material.TALL_GRASS,
            Material.TALL_SEAGRASS,
            Material.DEAD_BUSH,
            Material.PISTON,
            Material.PISTON_HEAD,
            Material.POPPY,
            Material.DANDELION,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.TNT,
            Material.FIRE,
            Material.SNOW,
            Material.ICE,
            Material.CACTUS,
            Material.SUGAR_CANE,
            Material.CAKE,
            Material.VINE,
            Material.NETHER_WART
    );

    private BlockCoord blockCoord;
    private int reinforcements;
    private String material;
    private String group;

    public FortifiedBlock() { }

    public FortifiedBlock(String group, Block block, Material material) {
        this.group = group;
        this.blockCoord = new BlockCoord(block);
        this.reinforcements = calculateReinforcements(material);
        this.material = material.toString();
    }

    public FortifiedBlock(BlockCoord blockCoord, int reinforcements, String material, String group) {
        this.blockCoord = blockCoord;
        this.reinforcements = reinforcements;
        this.material = material;
        this.group = group;
    }

    @BsonIgnore
    public static boolean isValidBlock(Block block) {
        return !INVALID_BLOCKS.contains(block.getType());
    }

    @BsonIgnore
    public static int getReinforcements(Material mat) {
        if (!FortifiedBlock.MATERIALS.containsKey(mat)) return 0;
        return FortifiedBlock.MATERIALS.get(mat);
    }

    public void upgradeMaterial(Material material) {
        this.material = material.toString();
        this.reinforcements = MATERIALS.get(material);
        Database.GROUPS.updateOne(
                Filters.and(
                        Filters.eq("name", group),
                        Filters.elemMatch("fortifiedBlocks", Filters.eq("blockCoord", blockCoord))
                ),
                Updates.combine(
                        Updates.set("fortifiedBlocks.$.reinforcements", reinforcements),
                        Updates.set("fortifiedBlocks.$.material", this.material)
                )
        );
    }

    public void refillReinforcements() {
        this.reinforcements = MATERIALS.get(Material.valueOf(material));
        Database.GROUPS.updateOne(
                Filters.and(
                        Filters.eq("name", group),
                        Filters.elemMatch("fortifiedBlocks", Filters.eq("blockCoord", blockCoord))
                ),
                Updates.set("fortifiedBlocks.$.reinforcements", reinforcements)
        );
    }

    public int calculateReinforcements(Material material) {
        return switch (material) {
            case IRON_INGOT -> IRON_FORTIFY;
            case DIAMOND -> DIAMOND_FORTIFY;
            case NETHERITE_INGOT -> NETHERITE_FORTIFY;
            default -> 0;
        };
    }

    @BsonIgnore
    public int getMaxReinforcements() {
        return FortifiedBlock.MATERIALS.get(Material.valueOf(getMaterial()));
    }

    @BsonIgnore
    public Location findLocation() {
        return new Location(Bukkit.getWorld(blockCoord.getWorldName()), blockCoord.getX(), blockCoord.getY(), blockCoord.getZ());
    }

    public void decrement() {
        reinforcements--;
        if (reinforcements > 0) {
            Bson match = Filters.and(
                    Filters.eq("name", group),
                    Filters.elemMatch("fortifiedBlocks", Filters.eq("blockCoord", blockCoord))
            );
            Bson update = Updates.set("fortifiedBlocks.$.reinforcements", reinforcements);
            Database.GROUPS.updateOne(match, update);
        } else {
            delete();
        }
    }

    /**
     * Deletes a fortified block from a group's list
     */
    public void delete() {
        CKGlobal.removeFortifiedBlock(blockCoord.asLocation());
        Database.GROUPS.updateOne(
                Filters.eq("name", group),
                Updates.pull("fortifiedBlocks", Filters.eq("blockCoord", blockCoord))
        );
    }

    /** Getters */

    public BlockCoord getBlockCoord() {
        return blockCoord;
    }

    public int getReinforcements() {
        return reinforcements;
    }

    public String getGroup() {
        return group;
    }

    public String getMaterial() {
        return material;
    }

    /** Setters */

    public void setBlockCoord(BlockCoord blockCoord) {
        this.blockCoord = blockCoord;
    }

    public void setReinforcements(int reinforcements) {
        this.reinforcements = reinforcements;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check for reference equality.
        if (o == null || getClass() != o.getClass()) return false;

        FortifiedBlock that = (FortifiedBlock) o;

        if (reinforcements != that.reinforcements) return false;
        if (!blockCoord.equals(that.blockCoord)) return false;
        return group.equals(that.group);
    }

    @Override
    public int hashCode() {
        int result = blockCoord.hashCode();
        result = 31 * result + reinforcements;
        result = 31 * result + group.hashCode();
        return result;
    }
}
