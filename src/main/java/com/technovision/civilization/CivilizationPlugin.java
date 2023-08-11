package com.technovision.civilization;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.technovision.civilization.commands.AcceptCommand;
import com.technovision.civilization.commands.DenyCommand;
import com.technovision.civilization.commands.civ.CivCommand;
import com.technovision.civilization.commands.town.TownCommand;
import com.technovision.civilization.data.Database;
import com.technovision.civilization.events.ItemEvents;
import com.technovision.civilization.events.NexusEvents;
import com.technovision.civilization.events.ResidentEvents;
import com.technovision.civilization.events.TechEvents;
import com.technovision.civilization.managers.*;
import com.technovision.civilization.util.CivColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CivilizationPlugin extends JavaPlugin {

    public static final Logger logger = Logger.getLogger("Minecraft");

    public static JavaPlugin plugin;
    public static final String namespace = "civilization";

    public static Database database;
    public static WorldGuardPlugin worldGuard;
    public static SchematicManager schematicManager;
    public static RegionManager regionManager;
    public static CivGlobal civGlobal;

    @Override
    public void onEnable() {
        // Load config
        plugin = this;
        loadConfig();
        initDatabase();
        initDependencies();

        // Initialize managers
        ItemManager.init();
        schematicManager = new SchematicManager();
        regionManager = new RegionManager(this);
        civGlobal = new CivGlobal(this);

        registerEventHandlers();
        registerCommands();

        logger.info(String.format("[%s] - Successfully loaded!", getDescription().getName()));
    }

    @Override
    public void onDisable() {
        logger.info(String.format("[%s] - Successfully disabled!", getDescription().getName()));
    }

    private void registerEventHandlers() {
        getServer().getPluginManager().registerEvents(new NexusEvents(), this);
        getServer().getPluginManager().registerEvents(new ResidentEvents(), this);
        getServer().getPluginManager().registerEvents(new ItemEvents(this), this);
        getServer().getPluginManager().registerEvents(new TechEvents(this), this);
    }

    private void registerCommands() {
        this.getCommand("civ").setExecutor(new CivCommand(this));
        this.getCommand("town").setExecutor(new TownCommand(this));
        this.getCommand("accept").setExecutor(new AcceptCommand());
        this.getCommand("deny").setExecutor(new DenyCommand());
    }

    private void initDependencies() {
        if (getServer().getPluginManager().getPlugin("WorldEdit") == null
                || getServer().getPluginManager().getPlugin("WorldGuard") == null
                || getServer().getPluginManager().getPlugin("Vault") == null) {
            logger.severe(String.format("[%s] - Disabled (Missing dependencies)", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        worldGuard = WorldGuardPlugin.inst();
        setGlobalFlags();
    }

    private void initDatabase() {
        try {
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.SEVERE);
            String databaseName = getConfig().getString("mongodb.database");
            String uri = getConfig().getString("mongodb.uri");
            database = new Database(databaseName, uri);
        } catch (Exception e) {
            logger.severe(String.format("[%s] - Disabled (Unable to connect to MongoDB database)", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    private void setGlobalFlags() {
        World world = getServer().getWorlds().get(0);
        com.sk89q.worldguard.protection.managers.RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        GlobalProtectedRegion globalRegion = new GlobalProtectedRegion("__global__");
        regions.addRegion(globalRegion);
        globalRegion.setFlag(Flags.GREET_MESSAGE, CivColor.LightGray+"Entering Wilderness "+CivColor.Red+"[PvP]");
        globalRegion.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        try {
            regions.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();
    }

    public static int scheduleSyncDelayedTask(Runnable task, long delay) {
        return Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, delay);
    }

    public static BukkitTask scheduleAsyncDelayedTask(Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    public static int scheduleSyncRepeatingTask(Runnable task, long delay, long repeat) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, delay, repeat);
    }

    public static BukkitTask scheduleAsyncRepeatingTask(Runnable task, long delay, long repeat) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delay, repeat);
    }
}
