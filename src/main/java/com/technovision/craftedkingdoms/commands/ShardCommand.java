package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.objects.BlockCoord;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.data.objects.SoulShard;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.handlers.sharding.ShardHandler;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

/**
 * Handles soul shard commands to protect blocks.
 *
 * @author TechnoVision
 */
public class ShardCommand extends CommandBase {

    public ShardCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/soulshard";
        displayName = "Soul Shard";

        // Implemented
        commands.put("locate", "Locate your soul shard.");
        commands.put("free", "Free a soul shard.");
    }

    public void locate_cmd() throws CKException {
        Resident resident = getResident();
        SoulShard soulShard = resident.getSoulShard();
        if (soulShard == null) {
            MessageUtils.sendError(sender, "Your soul is not currently trapped in a shard!");
            return;
        }

        UUID holderID = soulShard.getHolder();
        if (holderID != null) {
            Player player = Bukkit.getPlayer(holderID);
            Location loc = player.getLocation();
            String msg = String.format("%sYour soul shard is held by %s%s%s at %s(%s, %s, %s)%s in %s%s",
                    ChatColor.GRAY, ChatColor.YELLOW,
                    player.getName(),
                    ChatColor.GRAY, ChatColor.YELLOW,
                    loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
                    ChatColor.GRAY, ChatColor.YELLOW,
                    loc.getWorld().getName()
            );
            MessageUtils.send(sender, msg);
            return;
        }

        BlockCoord coords = soulShard.getBlockCoord();
        if (coords != null) {
            String msg = String.format("%sYour soul shard is at %s(%s, %s, %s)%s in %s%s",
                    ChatColor.GRAY, ChatColor.YELLOW,
                    coords.getX(), coords.getY(), coords.getZ(),
                    ChatColor.GRAY, ChatColor.YELLOW,
                    coords.getWorldName()
            );
            MessageUtils.send(sender, msg);
        }
    }

    public void free_cmd() throws CKException {
        Player player = getPlayer();
        ItemStack shardItem = player.getInventory().getItemInMainHand();
        if (!ShardHandler.isSoulShard(shardItem)) {
            MessageUtils.sendError(player, "You must be holding a soul shard in your hand!");
            return;
        }

        Resident shardedResident = ShardHandler.getResidentFromShard(shardItem);
        SoulShard shard = shardedResident.getSoulShard();

        UUID holderID = shard.getHolder();
        if (holderID == null) {
            BlockCoord blockCoord = shard.getBlockCoord();
            Location location = blockCoord.asLocation();
            Block block = location.getBlock();

            if (block.getState() instanceof InventoryHolder holder) {
                Inventory inventory = holder.getInventory();

                for (ItemStack item : inventory.getContents()) {
                    if (isShard(shardedResident, item)) {
                        shardedResident.freePlayer(item);
                        MessageUtils.sendError(sender, ChatColor.LIGHT_PURPLE + "You have freed the player in this soul shard!");
                        return;
                    }
                }
            }
        } else {
            Player holder = Bukkit.getPlayer(holderID);
            if (holder != null) {
                for (ItemStack item : holder.getInventory().getContents()) {
                    if (isShard(shardedResident, item)) {
                        shardedResident.freePlayer(item);
                        MessageUtils.sendError(sender, ChatColor.LIGHT_PURPLE + "You have freed the player in this soul shard!");
                        return;
                    }
                }
            }
        }
        MessageUtils.sendError(sender, "Could not find your soul shard! Contact an admin!");
    }

    private boolean isShard(Resident shardedRes, ItemStack item) {
        if (item == null || !ShardHandler.isSoulShard(item)) return false;

        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(CraftedKingdoms.namespace, "soulshard");
        String storedUUIDString = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        if (storedUUIDString == null) return false;
        if (!shardedRes.getPlayerID().toString().equals(storedUUIDString)) return false;
        return true;
    }

    @Override
    public void doDefaultAction() throws CKException {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() throws CKException {
    }
}
