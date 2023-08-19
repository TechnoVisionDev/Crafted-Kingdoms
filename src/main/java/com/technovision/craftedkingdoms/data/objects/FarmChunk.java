package com.technovision.craftedkingdoms.data.objects;

import org.bukkit.Chunk;

import java.util.HashSet;
import java.util.Set;

/**
 * POJO object that stores the set of crops for a chunk of land.
 *
 * @author TechnoVision
 */
public class FarmChunk {

    private ChunkCoord chunkCoord;
    private Set<Crop> crops;

    public FarmChunk() { }

    public FarmChunk(Chunk chunk) {
        this.chunkCoord = new ChunkCoord(chunk);
        this.crops = new HashSet<>();
    }

    public FarmChunk(Chunk chunk, Set<Crop> crops) {
        this.chunkCoord = new ChunkCoord(chunk);
        this.crops = crops;
    }

    public FarmChunk(ChunkCoord chunkCoord, Set<Crop> crops) {
        this.chunkCoord = chunkCoord;
        this.crops = crops;
    }

    /** Getters */

    public ChunkCoord getChunkCoord() {
        return chunkCoord;
    }

    public Set<Crop> getCrops() {
        return crops;
    }

    /** Setters */

    public void setChunkCoord(ChunkCoord chunkCoord) {
        this.chunkCoord = chunkCoord;
    }

    public void setCrops(Set<Crop> crops) {
        this.crops = crops;
    }
}
