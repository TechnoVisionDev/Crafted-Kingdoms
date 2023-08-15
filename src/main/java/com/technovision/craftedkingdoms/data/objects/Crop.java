package com.technovision.craftedkingdoms.data.objects;

import com.technovision.craftedkingdoms.data.enums.BiomeData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Date;

/**
 * POJO object that stores data for a growable crop
 *
 * @author TechnoVision
 */
public class Crop {

    private BlockCoord blockCoord;
    private String material;
    private Date timePlanted;

    public Crop() { }

    public Crop(Location cropLocation, Material seedForm) {
        this.blockCoord = new BlockCoord(cropLocation);
        this.material = BiomeData.getCropForm(seedForm).toString();
        this.timePlanted = new Date();
    }

    public Crop(Block cropBlock) {
        this.blockCoord = new BlockCoord(cropBlock);
        this.material = cropBlock.getType().toString();
        this.timePlanted = new Date();
    }

    public Crop(BlockCoord blockCoord, String material, Date timePlanted) {
        this.blockCoord = blockCoord;
        this.material = material;
        this.timePlanted = timePlanted;
    }

    /** Getters */

    public BlockCoord getBlockCoord() {
        return blockCoord;
    }

    public String getMaterial() {
        return material;
    }

    public Date getTimePlanted() {
        return timePlanted;
    }

    /** Setters */

    public void setBlockCoord(BlockCoord blockCoord) {
        this.blockCoord = blockCoord;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setTimePlanted(Date timePlanted) {
        this.timePlanted = timePlanted;
    }
}
