package com.technovision.craftedkingdoms;

import com.technovision.craftedkingdoms.commands.*;
import com.technovision.craftedkingdoms.commands.group.GroupCommand;
import com.technovision.craftedkingdoms.data.Database;
import com.technovision.craftedkingdoms.handlers.*;
import com.technovision.craftedkingdoms.handlers.farming.FarmingHandler;
import com.technovision.craftedkingdoms.handlers.sharding.EssenceHandler;
import com.technovision.craftedkingdoms.handlers.sharding.ShardHandler;
import org.bukkit.World;
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
        // Save planted crops
        FarmingHandler.saveCropsToDatabase();

        // Remove any remaining armor stand nametags
        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(ArmorStand.class)) {
                if (entity.hasMetadata("fortifyTag") || entity.hasMetadata("particles")) {
                    entity.remove();
                }
            }
        }

        logger.info(String.format("[%s] - Successfully disabled!", getDescription().getName()));
    }

    private void registerEventHandlers() {
        getServer().getPluginManager().registerEvents(new VanillaHandler(), this);
        getServer().getPluginManager().registerEvents(new EssenceHandler(), this);
        getServer().getPluginManager().registerEvents(new ShardHandler(), this);
        getServer().getPluginManager().registerEvents(new ResidentHandler(), this);
        getServer().getPluginManager().registerEvents(new FortifyHandler(), this);
        getServer().getPluginManager().registerEvents(new FarmingHandler(), this);
        getServer().getPluginManager().registerEvents(new SnitchHandler(), this);
        getServer().getPluginManager().registerEvents(new ChatHandler(), this);
        getServer().getPluginManager().registerEvents(new ItemHandler(), this);
        getServer().getPluginManager().registerEvents(new RecipesCommand(), this);
        getServer().getPluginManager().registerEvents(new CropsCommand(), this);
    }

    private void registerCommands() {
        GroupCommand groupCommand = new GroupCommand(this);
        FortifyCommand fortifyCommand = new FortifyCommand(this);
        ShardCommand shardCommand = new ShardCommand(this);
        ChatCommand chatCommand = new ChatCommand(this);
        SnitchCommand snitchCommand = new SnitchCommand(this);
        RecipesCommand recipesCommand = new RecipesCommand();
        CropsCommand cropsCommand = new CropsCommand();

        this.getCommand("group").setExecutor(groupCommand);
        this.getCommand("fortify").setExecutor(fortifyCommand);
        this.getCommand("soulshard").setExecutor(shardCommand);
        this.getCommand("chat").setExecutor(chatCommand);
        this.getCommand("snitch").setExecutor(snitchCommand);
        this.getCommand("recipes").setExecutor(recipesCommand);
        this.getCommand("crops").setExecutor(cropsCommand);

        this.getCommand("group").setTabCompleter(groupCommand);
        this.getCommand("fortify").setTabCompleter(fortifyCommand);
        this.getCommand("soulshard").setTabCompleter(shardCommand);
        this.getCommand("chat").setTabCompleter(chatCommand);
        this.getCommand("snitch").setTabCompleter(snitchCommand);
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();
    }
}
