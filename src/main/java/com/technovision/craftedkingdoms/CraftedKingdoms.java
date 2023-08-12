package com.technovision.craftedkingdoms;

import com.technovision.craftedkingdoms.commands.group.GroupCommand;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.events.PearlEvents;
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
        initDatabase();

        // Initialize managers
        CKGlobal = new CKGlobal();

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
        this.getCommand("group").setExecutor(new GroupCommand(this));
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
