package com.technovision.civilization.threading;

import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TaskMaster {

    private static final HashMap<String, BukkitTask> TASKS = new HashMap<String, BukkitTask>();
    private static final HashMap<String, BukkitTask> TIMERS = new HashMap<String, BukkitTask>();


    public static long getTicksTilDate(Date date) {
        Calendar c = Calendar.getInstance();

        if (c.getTime().after(date)) {
            return 0;
        }

        long timeInSeconds = (date.getTime() - c.getTime().getTime() ) / 1000;
        return timeInSeconds*20;
    }

    public static long getTicksToNextHour() {
        Calendar c = Calendar.getInstance();
        Date now = c.getTime();

        c.add(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date nextHour = c.getTime();

        long timeInSeconds = (nextHour.getTime() - now.getTime())/1000;
        return timeInSeconds*20;
    }



    public static void syncTask(Runnable runnable) {
        CivilizationPlugin.scheduleSyncDelayedTask(runnable, 0);
    }

    public static void syncTask(Runnable runnable, long l) {
        CivilizationPlugin.scheduleSyncDelayedTask(runnable, l);
    }

    public static void asyncTimer(String name, Runnable runnable,
                                  long delay, long repeat) {
        addTimer(name, CivilizationPlugin.scheduleAsyncRepeatingTask(runnable, delay, repeat));
    }

    public static void asyncTimer(String name, Runnable runnable, long time) {
        addTimer(name, CivilizationPlugin.scheduleAsyncRepeatingTask(runnable, time, time));
    }

    public static void asyncTask(String name, Runnable runnable, long delay) {
        addTask(name, CivilizationPlugin.scheduleAsyncDelayedTask(runnable, delay));
    }

    public static void asyncTask(Runnable runnable, long delay) {
        CivilizationPlugin.scheduleAsyncDelayedTask(runnable, delay);
    }

    private static void addTimer(String name, BukkitTask timer) {
        TIMERS.put(name, timer);
    }

    private static void addTask(String name, BukkitTask task) {
        TASKS.put(name, task);
    }

    public static void stopAll() {
        stopAllTasks();
        stopAllTimers();
    }

    public static void stopAllTasks() {
        for (BukkitTask task : TASKS.values()) {
            task.cancel();
        }
        TASKS.clear();
    }

    public static void stopAllTimers() {
        for (BukkitTask timer : TIMERS.values()) {
            timer.cancel();
        }
        TIMERS.clear();
    }

    public static void cancelTask(String name) {
        BukkitTask task = TASKS.get(name);
        if (task != null) {
            task.cancel();
        }
        TASKS.remove(name);
    }

    public static void cancelTimer(String name) {
        BukkitTask timer = TASKS.get(name);
        if (timer != null) {
            timer.cancel();
        }
        TIMERS.remove(name);
    }

    public static BukkitTask getTimer(String name) {
        return TIMERS.get(name);
    }

    public static BukkitTask getTask(String name) {
        return TASKS.get(name);
    }

    public static List<String> getTimersList() {
        List<String> out = new ArrayList<String>();
        out.add(CivMessage.buildTitle("Timers Running"));
        for (String name : TIMERS.keySet()) {
            out.add("Timer: "+name+" running.");
        }
        return out;
    }

    public static void syncTimer(String name, Runnable runnable, long time) {
        CivilizationPlugin.scheduleSyncRepeatingTask(runnable, time, time);
    }

    public static void syncTimer(String name, Runnable runnable, long delay, long repeat) {
        CivilizationPlugin.scheduleSyncRepeatingTask(runnable, delay, repeat);
    }

    public static boolean hasTask(String key) {
        BukkitTask task = TASKS.get(key);
        if (task == null) {
            return false;
        }
        if (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()) || Bukkit.getScheduler().isQueued(task.getTaskId())) {
            return true;
        }
        TASKS.remove(key);
        return false;
    }
}
