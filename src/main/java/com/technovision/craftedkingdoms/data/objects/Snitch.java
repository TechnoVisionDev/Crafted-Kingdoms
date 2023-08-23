package com.technovision.craftedkingdoms.data.objects;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import com.technovision.craftedkingdoms.data.enums.SnitchEvent;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private List<SnitchLog> log;
    private boolean isJukebox;
    private String name;

    public Snitch() { }

    public Snitch(String group, Block block, boolean isJukebox) {
        this.id = new ObjectId();
        this.blockCoord = new BlockCoord(block);;
        this.group = group;
        this.log = new ArrayList<>();
        this.isJukebox = isJukebox;
        this.name = null;
    }

    public Snitch(ObjectId id, BlockCoord blockCoord, String group, List<SnitchLog> log, boolean isJukebox, String name) {
        this.id = id;
        this.blockCoord = blockCoord;
        this.group = group;
        this.log = log;
        this.isJukebox = isJukebox;
        this.name = name;
    }

    public boolean isImmune(Player player) {
        Group group = CKGlobal.getGroup(this.group);
        if (group == null) return true;
        Ranks rank = group.findRank(player.getUniqueId());
        if (rank == null) {
            return false;
        }
        return group.hasPermission(rank, Permissions.SNITCH_IMMUNE);
    }

    public void logPlayerEvent(Player player, SnitchEvent event) {
        if (isImmune(player)) return;
        String snitchName = findName();
        switch(event) {
            case ENTER, EXIT -> {
                String msg = ChatColor.GOLD + String.format(event.getMessage(), player.getName(), snitchName);
                updateLog(msg);
                notifyGroup(msg);
            }
            case CHEST_OPEN, CONTAINER_OPEN -> {
                if (isJukebox) {
                    String msg = ChatColor.GOLD + String.format(event.getMessage(), player.getName(), snitchName);
                    updateLog(msg);
                    notifyGroup(msg);
                }
            }
        }
    }

    public void logBlockEvent(Player player, Block block, SnitchEvent event) {
        if (!isJukebox) return;
        if (isImmune(player)) return;

        String snitchName = findName();
        String blockName = StringUtils.stringifyType(block.getType());
        String msg;

        if (event == SnitchEvent.BLOCK_BREAK) {
            msg = ChatColor.GOLD + String.format(event.getMessage(), player.getName(), blockName, snitchName);
        } else if (event == SnitchEvent.BLOCK_PLACE) {
            msg = ChatColor.GOLD + String.format(event.getMessage(), player.getName(), blockName, snitchName);
        } else { return; }

        updateLog(msg);
        notifyGroup(msg);
    }

    public void logEntityEvent(Player player, EntityType entity, SnitchEvent event) {
        if (!isJukebox) return;
        if (isImmune(player)) return;

        String snitchName = findName();
        String entityName = StringUtils.stringifyType(entity);

        if (event == SnitchEvent.ENTITY_KILLED) {
            String msg = ChatColor.GOLD + String.format(event.getMessage(), player.getName(), entityName, snitchName);
            updateLog(msg);
            notifyGroup(msg);
        }
    }

    private void updateLog(String message) {
        SnitchLog snitchLog = new SnitchLog(message);
        log.add(snitchLog);
        Bson update = Updates.push("log", snitchLog);
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

    public String findName() {
        String snitchName = "a snitch";
        if (name != null) snitchName = name;
        return snitchName;
    }

    public void assignName(String name) {
        this.name = name;
        Bson update = Updates.set("name", name);
        Database.SNITCHES.updateOne(Filters.eq("_id", id), update);
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

    public List<SnitchLog> getLog() {
        return log;
    }

    public String getName() {
        return name;
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

    public void setLog(List<SnitchLog> log) {
        this.log = log;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJukebox(boolean jukebox) {
        isJukebox = jukebox;
    }
}
