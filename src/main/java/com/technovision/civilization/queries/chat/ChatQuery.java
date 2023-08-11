package com.technovision.civilization.queries.chat;

import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public abstract class ChatQuery {

    private static final Map<Player, ChatQuery> QUERIES = new HashMap<>();

    public static final long ONE_TICK = 20;
    public static final long ONE_MINUTE = ONE_TICK * 60;

    protected final Player player;
    protected final long timeout;
    protected BukkitTask timeoutTask;

    public ChatQuery(Player player, long timeout) {
        this.player = player;
        this.timeout = timeout;
        this.timeoutTask = Bukkit.getScheduler().runTaskLaterAsynchronously(CivilizationPlugin.plugin, this::timeout, timeout);
    }

    public abstract void start();

    public abstract boolean handleResponse(String response);

    public void cancel(String reason) {
        ChatQuery chatQuery = QUERIES.remove(player);
        if (chatQuery != null) {
            if (chatQuery.timeoutTask != null) {
                chatQuery.timeoutTask.cancel();
                chatQuery.timeoutTask = null;
            }
        }
        if (reason != null) {
            CivMessage.sendError(player, reason);
        }
    }

    public void cancel() { cancel(null); }

    public void timeout() {
        cancel("Setup Canceled: Player took too long to respond!");
    }

    public Player getPlayer() {
        return player;
    }

    public static ChatQuery getQuery(Player player) {
        return QUERIES.get(player);
    }

    public static void startQuery(Player player, ChatQuery chatQuery) {
        if (QUERIES.containsKey(player)) {
            chatQuery.cancel("Setup has been canceled!");
        } else {
            QUERIES.put(player, chatQuery);
            chatQuery.start();
        }
    }

}
