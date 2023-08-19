package com.technovision.craftedkingdoms.handlers.sharding;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.objects.Resident;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Handles essence rewards for player activity.
 *
 * @author TechnoVision
 */
public class EssenceHandler implements Listener {

    public static ItemStack ESSENCE;
    public static final HashMap<UUID, Long> loginTime = new HashMap<>();

    public EssenceHandler() {
        // Create a new ItemStack of Material ENDER_PEARL (1 ender pearl)
        ESSENCE = new ItemStack(Material.LIME_DYE, 1);
        ItemMeta meta = ESSENCE.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Essence");
        ESSENCE.setItemMeta(meta);

        Bukkit.getScheduler().runTaskTimer(CraftedKingdoms.plugin, () -> {
            for (UUID playerID : loginTime.keySet()) {
                // Check if last reward was atleast 24 hours ago (or is null)
                Resident resident = CKGlobal.getResident(playerID);
                Date lastRewardDate = resident.getRewardDate();
                Date now = new Date();
                if (lastRewardDate == null || now.getTime() - lastRewardDate.getTime() >= 24 * 60 * 60 * 1000) {

                    // If player has played for 30 consecutive minutes, give diamond
                    long currentTime = System.currentTimeMillis();
                    long loginTimestamp = loginTime.get(resident.getPlayerID());

                    if (currentTime - loginTimestamp >= 30 * 60 * 1000) {
                        Player player = Bukkit.getPlayer(resident.getPlayerID());
                        ItemStack essence = ESSENCE.clone();
                        essence.setAmount(4);
                        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(essence);
                        if (!remainingItems.isEmpty()) {
                            // Drop the essence at the player's location if inventory is full
                            player.getWorld().dropItemNaturally(player.getLocation(), essence);
                        }
                        // Update last reward date
                        resident.setRewardDate(now);
                    }
                }
            }
        }, 0, 20 * 60 * 5);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        loginTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        loginTime.remove(event.getPlayer().getUniqueId());
    }
}
