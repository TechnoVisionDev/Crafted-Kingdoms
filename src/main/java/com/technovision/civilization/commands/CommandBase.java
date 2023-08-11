package com.technovision.civilization.commands;

import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.data.objects.Civilization;
import com.technovision.civilization.data.objects.Resident;
import com.technovision.civilization.data.objects.Town;
import com.technovision.civilization.exceptions.CivException;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
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
    protected CivilizationPlugin plugin;

    public CommandBase(CivilizationPlugin plugin) {
        this.plugin = plugin;
    }

    public void requireMayorAssistantLeader() throws CivException {
        Resident resident = getResident();
        Civilization civ = getSenderCiv();
        if (civ == null) {
            throw new CivException("You must be in a civilization to run that command!");
        }
        if (civ.isLeader(resident) || civ.isAdvisor(resident)) {
            return;
        }

        Town town = getSenderTown();
        if (town == null) {
            throw new CivException("You must be in a town to run that command!");
        }
        if (town.isMayor(resident) || town.isAssistant(resident)) {
            return;
        }
        throw new CivException("Only leaders, mayors, and assistants can access that command.");
    }

    public Player getPlayer() throws CivException {
        if (sender instanceof Player) {
            return (Player)sender;
        }
        throw new CivException("Only players can run that command!");
    }

    protected Resident getNamedResident(int index) throws CivException {
        if (args.length < (index+1)) {
            throw new CivException("You must enter a player's name.");
        }
        String name = args[index].toLowerCase();
        name = name.replace("%", "(\\w*)");

        Resident res = CivGlobal.getResidentByName(name);
        if (res == null) {
            throw new CivException("That player isn't online or doesn't exist.");
        }
        return res;
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

    public Civilization getSenderCiv() {
        if (!(sender instanceof Player player)) return null;
        return CivGlobal.getCivByPlayer(player);
    }

    public Town getSenderTown() throws CivException {
        if (!(sender instanceof Player player)) return null;;
        return CivGlobal.getTownByPlayer(player);
    }

    public Resident getResident() {
        if (!(sender instanceof Player player)) return null;
        return CivGlobal.getResident(player);
    }

    public abstract void init();

    /* Called when no arguments are passed. */
    public abstract void doDefaultAction() throws CivException;

    /* Called on syntax error. */
    public abstract void showHelp();

    /* Called before command is executed to check permissions. */
    public abstract void permissionCheck() throws CivException;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        init();

        this.args = args;
        this.sender = sender;

        try {
            permissionCheck();
        } catch (Exception e) {
            CivMessage.sendError(sender, e.getMessage());
            return false;
        }

        if (args.length == 0) {
            try {
                doDefaultAction();
                return true;
            } catch (CivException e) {
                CivMessage.sendError(sender, e.getMessage());
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
                        CivMessage.sendError(sender, "Internal Command Error.");
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof CivException) {
                            CivMessage.sendError(sender, e.getCause().getMessage());
                        } else {
                            CivMessage.sendError(sender, "Internal Command Error.");
                            e.getCause().printStackTrace();
                        }
                    }
                } catch (NoSuchMethodException e) {
                    if (sendUnknownToDefault) {
                        try {
                            doDefaultAction();
                        } catch (CivException e1) {
                            CivMessage.sendError(sender, e.getMessage());
                        }
                        return false;
                    }
                    CivMessage.sendError(sender, "Unknown method "+args[0]);
                }
                return true;
            }
        }

        if (sendUnknownToDefault) {
            try {
                doDefaultAction();
                return true;
            } catch (CivException e) {
                CivMessage.sendError(sender, e.getMessage());
            }
            return false;
        }

        CivMessage.sendError(sender, "Unknown command "+args[0]);
        return true;
    }

    public void showBasicHelp() {
        CivMessage.sendHeading(sender, displayName+" Command Help");
        for (String c : commands.keySet()) {
            String info = commands.get(c);

            info = info.replace("[", CivColor.Yellow+"[");
            info = info.replace("]", "]"+CivColor.LightGray);
            info = info.replace("(", CivColor.Yellow+"(");
            info = info.replace(")", ")"+CivColor.LightGray);

            CivMessage.send(sender, CivColor.Gold+command+" "+c+CivColor.LightGray+" "+info);
        }
    }
}
