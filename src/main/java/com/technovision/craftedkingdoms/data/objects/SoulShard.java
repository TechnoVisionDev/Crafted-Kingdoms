package com.technovision.craftedkingdoms.data.objects;

import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class SoulShard {

    private Date dateCreated;
    private BlockCoord blockCoord;
    private UUID holder;

    public SoulShard() { }

    public SoulShard(Player killer) {
        this.dateCreated = new Date();
        this.holder = killer.getUniqueId();
        this.blockCoord = null;
    }

    public SoulShard(Date dateCreated, BlockCoord blockCoord, UUID holder) {
        this.dateCreated = dateCreated;
        this.blockCoord = blockCoord;
        this.holder = holder;
    }

    /** Getters */

    public Date getDateCreated() {
        return dateCreated;
    }

    public BlockCoord getBlockCoord() {
        return blockCoord;
    }

    public UUID getHolder() {
        return holder;
    }

    /** Setters */

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setBlockCoord(BlockCoord blockCoord) {
        this.blockCoord = blockCoord;
    }

    public void setHolder(UUID holder) {
        this.holder = holder;
    }
}
