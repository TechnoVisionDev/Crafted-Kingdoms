package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.enums.Ranks;
import com.technovision.craftedkingdoms.data.objects.Group;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommandBase implements CommandExecutor, TabCompleter {

    protected HashMap<String, String> commands = new HashMap<>();

    protected String[] args;
    protected CommandSender sender;

    protected String command;
    protected String displayName;
    protected boolean sendUnknownToDefault = false;
    protected CraftedKingdoms plugin;

    public CommandBase(CraftedKingdoms plugin) {
        this.plugin = plugin;
        init();
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(commands.keySet());
        }
        // Filter the list to include only entries that start with the current argument
        return completions.stream()
                .filter(str -> str.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
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

    public Group getGroupFromArgs(int index) throws CKException {
        if (args.length < index+1) {
            throw new CKException("You must specify a group name!");
        }
        Group group = CKGlobal.getGroup(args[index]);
        if (group == null) {
            throw new CKException("The group " + ChatColor.YELLOW + args[index] + ChatColor.RED + " doesn't exist!");
        }
        return group;
    }

    public Ranks getRankFromArgs(int index) throws CKException {
        if (args.length < index+1) {
            throw new CKException("You must specify a player rank!");
        }
        try {
            return Ranks.valueOf(args[index].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CKException("The rank " + ChatColor.YELLOW + args[index] + ChatColor.RED + " doesn't exist!");
        }
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

    protected String[] stripArgs(String[] someArgs, int amount) {
        if (amount >= someArgs.length) {
            return new String[0];
        }
        String[] argsLeft = new String[someArgs.length - amount];
        for (int i = 0; i < argsLeft.length; i++) {
            argsLeft[i] = someArgs[i+amount];
        }
        return argsLeft;
    }

    protected String combineArgs(String[] someArgs) {
        String combined = "";
        for (String str : someArgs) {
            combined += str + " ";
        }
        combined = combined.trim();
        return combined;
    }

    public Resident getResident() {
        if (!(sender instanceof Player player)) {
            return null;
        }
        return CKGlobal.getResident(player);
    }

    protected Resident getResidentFromArgs(int index) throws CKException {
        if (args.length < (index+1)) {
            throw new CKException("You must enter a player's name!");
        }
        String name = args[index].toLowerCase();
        name = name.replace("%", "(\\w*)");

        Resident res = CKGlobal.getResident(name);
        if (res == null) {
            throw new CKException("The player you specified doesn't exist!");
        }
        return res;
    }
}
