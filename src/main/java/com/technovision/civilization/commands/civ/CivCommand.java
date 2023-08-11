package com.technovision.civilization.commands.civ;

import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.commands.CommandBase;
import com.technovision.civilization.exceptions.CivException;

public class CivCommand extends CommandBase {

    public static final long INVITE_TIMEOUT = 30000; //30 seconds

    public CivCommand(CivilizationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/civ";
        displayName = "Civ";

        // Implemented
        commands.put("research", "Manage civilization's research.");

        // Not Yet Implemented
        /**
        commands.put("townlist", "Shows a list of all towns in the civilization.");
        commands.put("deposit", "[amount] - deposits this amount into the civ's treasury.");
        commands.put("withdraw", "[amount] - withdraws this amount from the civ's treasury.");
        commands.put("info", "Shows information about this Civilization");
        commands.put("show", "[name] gives you information about the civ named [name].");
        commands.put("list", "(name) - shows all civs in the world, or the towns for the civ named (name).");
        commands.put("gov", "Manage your civilizations government.");
        commands.put("time", "View information about upcoming events.");
        commands.put("set", "Set various civilization properties such as taxes and border color");
        commands.put("group", "Manage the leaders and advisers group.");
        commands.put("dip", "Manage civilization's diplomacy.");
        commands.put("top", "Show the top 5 civilizations in the world.");
        commands.put("disbandtown", "[town] Disbands this town. Mayor must also issue /town disbandtown");
        commands.put("claimleader", "claim yourself as leader of this civ. All current leaders must be inactive.");
        */
    }

    public void info_cmd() throws CivException {

    }

    public void research_cmd() {
        CivResearchCommand cmd = new CivResearchCommand(plugin);
        cmd.onCommand(sender, null, "research", this.stripArgs(args, 1));
    }

    @Override
    public void doDefaultAction() throws CivException {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() throws CivException {
    }
}
