package com.technovision.craftedkingdoms.handlers.sharding;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.objects.BlockCoord;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.data.objects.SoulShard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class ShardUpkeepTask implements Runnable {

    @Override
    public void run() {
        for (Resident res : CKGlobal.RESIDENTS.values()) {
            SoulShard shard = res.getSoulShard();
            if (shard == null) continue;

            UUID holderID = shard.getHolder();

            if (holderID == null) {
                BlockCoord blockCoord = shard.getBlockCoord();
                Location location = blockCoord.asLocation();
                Block block = location.getBlock();

                if (block.getState() instanceof InventoryHolder) {
                    InventoryHolder holder = (InventoryHolder) block.getState();
                    Inventory inventory = holder.getInventory();

                    for (ItemStack item : inventory.getContents()) {
                        processShardItem(item, null, res);
                    }
                }
            } else {
                Player holder = Bukkit.getPlayer(holderID);
                if (holder != null) {
                    for (ItemStack item : holder.getInventory().getContents()) {
                        processShardItem(item, holder, res);
                    }
                }
            }
        }
    }

    private void processShardItem(ItemStack item, Player holder, Resident shardedRes) {
        if (item == null || !ShardHandler.isSoulShard(item)) return;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(CraftedKingdoms.namespace, "soulshard");
        String storedUUIDString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (storedUUIDString == null) return;

        if (holder == null || shardedRes.getPlayerID().toString().equals(storedUUIDString)) {
            NamespacedKey upkeepKey = new NamespacedKey(CraftedKingdoms.namespace, "upkeep");
            int upkeep = meta.getPersistentDataContainer().getOrDefault(upkeepKey, PersistentDataType.INTEGER, 0);
            upkeep--;

            if (upkeep <= 0) {
                shardedRes.freePlayer(item);
            } else {
                // Calculate shard health
                long percent = Math.round(((double) upkeep / ShardHandler.HOURS_PER_WEEK) * 100);

                meta.getPersistentDataContainer().set(upkeepKey, PersistentDataType.INTEGER, upkeep);
                List<String> lore = meta.getLore();
                lore.set(2, ChatColor.GOLD + "Health: " + ChatColor.GRAY + percent + "%");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }
}
