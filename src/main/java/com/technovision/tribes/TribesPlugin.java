package com.technovision.tribes;

import com.technovision.tribes.commands.tribe.TribeCommand;
import com.technovision.tribes.data.Database;
import com.technovision.tribes.events.PearlEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TribesPlugin extends JavaPlugin {

    public static JavaPlugin plugin;
    public static final String namespace = "tribes";
    public static final Logger logger = Logger.getLogger("Minecraft");

    public static Database database;
    public static TribesGlobal tribesGlobal;

    @Override
    public void onEnable() {
        // Load config
        plugin = this;
        loadConfig();
        initDatabase();

        // Initialize managers
        tribesGlobal = new TribesGlobal();

        // Register events and commands
        registerEventHandlers();
        registerCommands();

        logger.info(String.format("[%s] - Successfully loaded!", getDescription().getName()));
    }

    @Override
    public void onDisable() {
        logger.info(String.format("[%s] - Successfully disabled!", getDescription().getName()));
    }

    private void registerEventHandlers() {
        getServer().getPluginManager().registerEvents(new PearlEvents(), this);
    }

    private void registerCommands() {
        this.getCommand("tribe").setExecutor(new TribeCommand(this));
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
        }
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();
    }
}
