package com.technovision.craftedkingdoms.data.objects;

/**
 * POJO object which stores chunk coordinates.
 *
 * @author TechnoVision
 */
public class ChunkCoord {

    private String worldName;
    private int x;
    private int z;

    public ChunkCoord() { }

    public ChunkCoord(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    /** Getters */

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
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

    public void setZ(int z) {
        this.z = z;
    }
}
