package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.SnitchEvent;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO Object that stores data for a snitch block (noteblock or jukebox).
 *
 * @author TechnoVision
 */
public class Snitch {

    @BsonId
    private ObjectId id;
    private BlockCoord blockCoord;
    private String group;
    private List<String> log;
    private boolean isJukebox;

    public Snitch() { }

    public Snitch(String group, Block block, boolean isJukebox) {
        this.id = new ObjectId();
        this.blockCoord = new BlockCoord(block);;
        this.group = group;
        this.log = new ArrayList<>();
        this.isJukebox = isJukebox;
    }

    public Snitch(ObjectId id, BlockCoord blockCoord, String group, List<String> log, boolean isJukebox) {
        this.id = id;
        this.blockCoord = blockCoord;
        this.group = group;
        this.log = log;
        this.isJukebox = isJukebox;
    }

    public boolean isImmune(Player player) {
        Group group = CKGlobal.getGroup(this.group);
        if (group == null) return true;
        return group.isResident(player.getUniqueId());
    }

    public void logEvent(Player player, SnitchEvent event) {
        if (isImmune(player)) return;
        switch(event) {
            case ENTER, EXIT -> {
                String msg = ChatColor.GOLD + String.format(event.getMessage(), player.getName(), "a snitch");
                if (isJukebox) {
                    updateLog(msg);
                }
                notifyGroup(msg);
            }
            default -> {
                if (isJukebox) {
                    String msg = ChatColor.GOLD + String.format(event.getMessage(), player.getName(), "a snitch");
                    updateLog(msg);
                    notifyGroup(msg);
                }
            }
        }
    }

    private void updateLog(String message) {
        log.add(message);
        Bson update = Updates.push("log", message);
        Database.SNITCHES.updateOne(Filters.eq("_id", id), update);
    }

    public void notifyGroup(String msg) {
        Group group = CKGlobal.getGroup(this.group);
        if (group == null) return;
        MessageUtils.sendGroup(group, msg);
    }

    public void delete() {
        CKGlobal.removeSnitch(blockCoord.asLocation());

        // Remove snitch from group
        Bson update = Updates.pull("snitches", id);
        Database.GROUPS.updateOne(Filters.eq("name", group), update);

        // Delete snitch from database
        Database.SNITCHES.deleteOne(Filters.eq("_id", id));
    }

    /** Getters */

    public ObjectId getId() {
        return id;
    }

    public BlockCoord getBlockCoord() {
        return blockCoord;
    }

    public String getGroup() {
        return group;
    }

    public List<String> getLog() {
        return log;
    }

    public boolean isJukebox() {
        return isJukebox;
    }

    /** Setters */

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setBlockCoord(BlockCoord blockCoord) {
        this.blockCoord = blockCoord;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public void setJukebox(boolean jukebox) {
        isJukebox = jukebox;
    }
}
