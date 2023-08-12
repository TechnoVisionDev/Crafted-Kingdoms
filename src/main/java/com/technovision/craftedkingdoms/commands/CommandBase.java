package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class CommandBase implements CommandExecutor {

    protected HashMap<String, String> commands = new HashMap<>();

    protected String[] args;
    protected CommandSender sender;

    protected String command;
    protected String displayName;
    protected boolean sendUnknownToDefault = false;
    protected CraftedKingdoms plugin;

    public CommandBase(CraftedKingdoms plugin) {
        this.plugin = plugin;
    }

    public Player getPlayer() throws CKException {
        if (sender instanceof Player) {
            return (Player)sender;
        }
        throw new CKException("Only players can run that command!");
    }

    public abstract void init();

    /* Called when no arguments are passed. */
    public abstract void doDefaultAction() throws CKException;

    /* Called on syntax error. */
    public abstract void showHelp();

    /* Called before command is executed to check permissions. */
    public abstract void permissionCheck() throws CKException;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        init();

        this.args = args;
        this.sender = sender;

        try {
            permissionCheck();
        } catch (Exception e) {
            MessageUtils.sendError(sender, e.getMessage());
            return false;
        }

        if (args.length == 0) {
            try {
                doDefaultAction();
                return true;
            } catch (CKException e) {
                MessageUtils.sendError(sender, e.getMessage());
            }
            return false;
        }

        if (args[0].equalsIgnoreCase("help")) {
            showHelp();
            return true;
        }

        for (String c : commands.keySet()) {
            if (c.equalsIgnoreCase(args[0])) {
                try {
                    Method method = this.getClass().getMethod(args[0].toLowerCase()+"_cmd");
                    try {
                        method.invoke(this);
                        return true;
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                        MessageUtils.sendError(sender, "Internal Command Error.");
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof CKException) {
                            MessageUtils.sendError(sender, e.getCause().getMessage());
                        } else {
                            MessageUtils.sendError(sender, "Internal Command Error.");
                            e.getCause().printStackTrace();
                        }
                    }
                } catch (NoSuchMethodException e) {
                    if (sendUnknownToDefault) {
                        try {
                            doDefaultAction();
                        } catch (CKException e1) {
                            MessageUtils.sendError(sender, e.getMessage());
                        }
                        return false;
                    }
                    MessageUtils.sendError(sender, "Unknown method "+args[0]);
                }
                return true;
            }
        }

        if (sendUnknownToDefault) {
            try {
                doDefaultAction();
                return true;
            } catch (CKException e) {
                MessageUtils.sendError(sender, e.getMessage());
            }
            return false;
        }

        MessageUtils.sendError(sender, "Unknown command "+args[0]);
        return true;
    }

    public void showBasicHelp() {
        MessageUtils.sendHeading(sender, displayName+" Command Help");
        for (String c : commands.keySet()) {
            String info = commands.get(c);

            info = info.replace("[", ChatColor.YELLOW+"[");
            info = info.replace("]", "]"+ChatColor.GRAY);
            info = info.replace("<", ChatColor.YELLOW+"<");
            info = info.replace(">", ">"+ChatColor.GRAY);

            MessageUtils.send(sender, ChatColor.GOLD+command+" "+c+ChatColor.GRAY+" "+info);
        }
    }
}
