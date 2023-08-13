package com.technovision.craftedkingdoms.data.objects;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * POJO object representing the coordinate location of a block.
 *
 * @author TechnoVision
 */
public class BlockCoord {

    private String worldName;
    private int x;
    private int y;
    private int z;

    public BlockCoord() { }

    public BlockCoord(String worldName, int x, int y, int z) {
        this.setWorldName(worldName);
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public BlockCoord(Location location) {
        this.setWorldName(location.getWorld().getName());
        this.setX(location.getBlockX());
        this.setY(location.getBlockY());
        this.setZ(location.getBlockZ());
    }

    public BlockCoord(Block block) {
        this.setWorldName(block.getWorld().getName());
        this.setX(block.getX());
        this.setY(block.getY());
        this.setZ(block.getZ());
    }

    @Override
    public String toString() {
        return this.worldName+","+this.x+","+this.y+","+this.z;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof BlockCoord) {
            BlockCoord otherCoord = (BlockCoord)other;
            if (otherCoord.worldName.equals(worldName)) {
                if ((otherCoord.getX()) == x && (otherCoord.getY() == y) &&
                        (otherCoord.getZ() == z)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Getters */

    @BsonIgnore
    public Location asLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /** Setters */

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }
}

