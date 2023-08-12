package com.technovision.craftedkingdoms.data.objects;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * POJO object that stores data for a block that has been fortified.
 *
 * @author TechnoVision
 */
public class FortifiedBlock {

    public static final int IRON_FORTIFY = 5;
    public static final int DIAMOND_FORTIFY = 10;
    public static final int NETHERITE_FORTIFY = 20;

    private BlockCoord blockCoord;
    private int fortifications;
    private String group;

    public FortifiedBlock() { }

    public FortifiedBlock(String group, Block block, Material material) {
        this.group = group;
        this.blockCoord = new BlockCoord(block);
        this.fortifications = calculateFortifications(material);
    }

    public FortifiedBlock(BlockCoord blockCoord, int fortifications, String group) {
        this.blockCoord = blockCoord;
        this.fortifications = fortifications;
        this.group = group;
    }

    public int calculateFortifications(Material material) {
        return switch (material) {
            case IRON_INGOT -> IRON_FORTIFY;
            case DIAMOND -> DIAMOND_FORTIFY;
            case NETHERITE_INGOT -> NETHERITE_FORTIFY;
            default -> 0;
        };
    }

    /** Getters */

    public BlockCoord getBlockCoord() {
        return blockCoord;
    }

    public int getFortifications() {
        return fortifications;
    }

    public String getGroup() {
        return group;
    }

    /** Setters */

    public void setBlockCoord(BlockCoord blockCoord) {
        this.blockCoord = blockCoord;
    }

    public void setFortifications(int fortifications) {
        this.fortifications = fortifications;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
