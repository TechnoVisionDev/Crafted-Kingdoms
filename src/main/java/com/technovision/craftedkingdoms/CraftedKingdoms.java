package com.technovision.craftedkingdoms;

import com.technovision.craftedkingdoms.commands.FortifyCommand;
import com.technovision.craftedkingdoms.commands.group.GroupCommand;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.events.DisableEvents;
import com.technovision.craftedkingdoms.events.FortifyEvents;
import com.technovision.craftedkingdoms.events.PearlEvents;
import com.technovision.craftedkingdoms.events.PlayerEvents;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CraftedKingdoms extends JavaPlugin {

    public static JavaPlugin plugin;
    public static final String namespace = "craftedkingdoms";
    public static final Logger logger = Logger.getLogger("Minecraft");

    public static Database database;
    public static CKGlobal CKGlobal;

    @Override
    public void onEnable() {
        // Load config
        plugin = this;
        loadConfig();

        // Initialize database
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

        // Initialize managers
        CKGlobal = new CKGlobal();

        // Register events and commands
        registerEventHandlers();
        registerCommands();

        logger.info(String.format("[%s] - Successfully loaded!", getDescription().getName()));
    }

    @Override
    public void onDisable() {
        // Remove any remaining armor stand nametags
        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(ArmorStand.class)) {
                if (entity.hasMetadata("fortifyTag")) {
                    entity.remove();
                }
            }
        }
        logger.info(String.format("[%s] - Successfully disabled!", getDescription().getName()));
    }

    private void registerEventHandlers() {
        getServer().getPluginManager().registerEvents(new DisableEvents(), this);
        getServer().getPluginManager().registerEvents(new PearlEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new FortifyEvents(), this);
    }

    private void registerCommands() {
        this.getCommand("group").setExecutor(new GroupCommand(this));
        this.getCommand("fortify").setExecutor(new FortifyCommand(this));
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();
    }
}
