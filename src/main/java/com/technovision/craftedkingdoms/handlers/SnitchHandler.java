package com.technovision.craftedkingdoms.handlers;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.data.enums.SnitchEvent;
import com.technovision.craftedkingdoms.data.objects.Snitch;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

/**
 * Handles logging events for snitch blocks.
 *
 * @author TechnoVision
 */
public class SnitchHandler implements Listener {

    public static final int RADIUS = 11;
    private static final long COOLDOWN_IN_MILLIS = 1000;
    private final HashMap<Player, Long> COOLDOWNS = new HashMap<>();
    private final HashMap<Player, Location> PLAYER_IN_RADIUS = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Check if the player has moved to a new block
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null
                || (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        // Check if player is on cooldown
        Player player = event.getPlayer();
        if (isInCooldown(player)) return;
        COOLDOWNS.put(player, System.currentTimeMillis());

        // Check if player has entered snitch radius
        Snitch snitch = getNearbySnitch(to);
        Snitch prevSnitch;
        Location prevSnitchLoc = PLAYER_IN_RADIUS.get(player);
        if (prevSnitchLoc == null) prevSnitch = null;
        else prevSnitch = CKGlobal.getSnitch(prevSnitchLoc);

        if (snitch != null && !snitch.equals(prevSnitch)) {
            PLAYER_IN_RADIUS.put(player, snitch.getBlockCoord().asLocation());
            snitch.logEvent(player, SnitchEvent.ENTER);
        } else if (snitch == null && prevSnitch != null) {
            PLAYER_IN_RADIUS.remove(player);
            prevSnitch.logEvent(player, SnitchEvent.EXIT);
        }
    }

    private Snitch getNearbySnitch(Location playerLocation) {
        for (Location snitchLocation : CKGlobal.SNITCHES.keySet()) {
            int dx = Math.abs(snitchLocation.getBlockX() - playerLocation.getBlockX());
            int dy = Math.abs(snitchLocation.getBlockY() - playerLocation.getBlockY());
            int dz = Math.abs(snitchLocation.getBlockZ() - playerLocation.getBlockZ());
            if (dx <= RADIUS && dy <= RADIUS && dz <= RADIUS) {
                return CKGlobal.getSnitch(snitchLocation);
            }
        }
        return null;
    }

    private boolean isInCooldown(Player player) {
        Long lastActionTime = COOLDOWNS.get(player);
        if (lastActionTime != null) {
            return (System.currentTimeMillis() - lastActionTime) < COOLDOWN_IN_MILLIS;
        }
        return false;
    }
}
