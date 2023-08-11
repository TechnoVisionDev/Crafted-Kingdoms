package com.technovision.civilization.commands.town;

import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.commands.CommandBase;
import com.technovision.civilization.data.objects.ChunkCoord;
import com.technovision.civilization.data.objects.Resident;
import com.technovision.civilization.data.objects.Town;
import com.technovision.civilization.data.objects.Yields;
import com.technovision.civilization.exceptions.CivException;
import com.technovision.civilization.managers.RegionManager;
import com.technovision.civilization.queries.command.JoinTownResponse;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

/**
 * Handles all town commands
 *
 * @author TechnoVision
 */
public class TownCommand extends CommandBase {

    public static final long INVITE_TIMEOUT = 30000; //30 seconds

    public TownCommand(CivilizationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/town";
        displayName = "Town";

        // Implemented
        commands.put("claim", "Claim the plot you are standing in for this town.");
        commands.put("add", "[name] - invite a resident to the town.");
        commands.put("evict", "[name] - evict a resident from the town");
        commands.put("survey", "Display stats for building a town here.");

        // Not Yet Implemented
        /**
        commands.put("unclaim", "Unclaim the plot you are standing on, no refunds.");
        commands.put("nexus", "Move the town's nexus block to your location.");
        commands.put("group", "Manage town permission groups.");
        commands.put("upgrade", "Manage town upgrades.");
        commands.put("info", "Show information about this town.");
        commands.put("members", "Show a list of members in this town.");
        commands.put("deposit", "[amount] - deposits this amount into the town's treasury.");
        commands.put("withdraw","[amount] - withdraws this amount from the town's treasury.");
        commands.put("set", "Change various town properties.");
        commands.put("leave", "leaves the town you are currently in.");
        commands.put("show", "[name] show info for town of this name.");
        commands.put("evict", "[name] - evicts the resident named from town");
        commands.put("list", "shows a list of all towns in the world.");
        commands.put("reset", "Resets certain structures, action depends on structure.");
        commands.put("top", "Shows the top 5 towns in the world.");
        commands.put("disbandtown", "Disbands this town, requres leader to type disbandtown as well.");
        commands.put("outlaw", "Manage town outlaws.");
        commands.put("leavegroup", "[town] [group] - Leaves the group in [town] named [group]");
        commands.put("select", "[town] - Switches your control to this town, if you have the proper permissions.");
        commands.put("capitulate", " gives this town over to the currently owner civ. It will no longer remember its native civilization and will not revolt.");
        commands.put("templates", "Displays all templates bound to this town.");
        commands.put("event", "Displays information about the current random event going down.");
        commands.put("claimmayor", "claim yourself as mayor of this town. All current mayors must be inactive.");
        commands.put("movestructure", "[coord] [town] moves the structure specified by the coord to the specfied town.");
        commands.put("enablestructure", "[coord] attempts to enable the specified structure if its currently disabled.");
        */
    }

    public void info_cmd() throws CivException {

    }

    public void claim_cmd() throws CivException {
        requireMayorAssistantLeader();

        // Get resident data
        if (!(sender instanceof Player player)) return;
        Town town = getSenderTown();
        if (!town.getMayorID().equals(player.getUniqueId())) {
            CivMessage.sendError(player, "You must be the town's mayor to claim land!");
        }
        Chunk chunk = player.getLocation().getChunk();

        // Check if claim is valid
        if (!town.hasAvailableClaims()) {
            CivMessage.sendError(player, "Your town is out of claims! Increase your culture to gain more.");
            return;
        }
        if (town.isClaimed(chunk)) {
            CivMessage.sendError(player, "Your town has already claimed this chunk!");
            return;
        }
        if (RegionManager.isInForeignTerritory(player)) {
            CivMessage.sendError(player, "Another town has already claimed this chunk!");
            return;
        }
        if (!town.isValidClaim(chunk)) {
            CivMessage.sendError(player, "You can only claim chunks adjacent to existing claims!");
            return;
        }

        // Claim land for town
        town.claim(player);
        CivMessage.sendSuccess(player, "You successfully claimed this chunk! " + CivColor.Yellow +"(X=" +chunk.getX() + ", Z="+chunk.getZ()+")");
    }

    public void add_cmd() throws CivException {
        requireMayorAssistantLeader();

        Resident newResident = getNamedResident(1);
        Player player = getPlayer();
        Town town = getSenderTown();

        // TODO: Add war check
        /**
        if (War.isWarTime()) {
            throw new CivException("Cannot invite players to town during WarTime.");
        }

        if (War.isWithinWarDeclareDays() && town.getCiv().getDiplomacyManager().isAtWar()) {
            throw new CivException("Cannot invite players to a civ that is at war within "+War.getTimeDeclareDays()+" days before WarTime.");
        }
         */

        JoinTownResponse join = new JoinTownResponse();
        join.town = town;
        join.resident = newResident;
        join.sender = player;

        if (town.hasResident(newResident)) {
            throw new CivException(CivColor.Yellow+newResident.getPlayerName()+CivColor.Rose+" is already a member of our town.");
        }
        if (newResident.getTown() != null) {
            throw new CivException(CivColor.Yellow+newResident.getPlayerName()+CivColor.Rose+" is currently a member of "+CivColor.Yellow+newResident.getTown());
        }

        CivMessage.questionPlayer(player, newResident.getAsPlayer(),
                "Would you like to join the town of "+town.getName()+"?",
                INVITE_TIMEOUT, join);

        CivMessage.sendSuccess(sender, CivColor.LightGray+"Invited "+CivColor.Yellow+newResident.getPlayerName()+CivColor.LightGray+" to the town of "+CivColor.Yellow+town.getName());
    }

    public void evict_cmd() throws CivException {
        requireMayorAssistantLeader();

        Town town = getSenderTown();
        Resident resident = getResident();
        if (args.length < 2) {
            throw new CivException("Enter the name of the resident to evict.");
        }

        Resident residentToKick = getNamedResident(1);
        String resName = CivColor.Yellow+residentToKick.getPlayerName();
        if (residentToKick.getTown() == null || !residentToKick.getTown().equals(town.getName())) {
            throw new CivException(resName+CivColor.Rose+" is not a member of our town.");
        }

        town.removeResident(residentToKick);
        CivMessage.send(residentToKick.getAsPlayer(), CivColor.LightGray+"You have been evicted from the town of "+CivColor.Yellow+town.getName());
        CivMessage.sendTown(town, resName+CivColor.LightGray+" has been evicted from town by "+CivColor.Yellow+resident.getPlayerName());

        // TODO: If resident is land owner, give them a few grace days to leave
        /**
        if (!residentToKick.isLandOwner()) {
            town.removeResident(residentToKick);

            try {
                CivMessage.send(CivGlobal.getPlayer(residentToKick), CivColor.Yellow+"You have been evicted from town!");
            } catch (CivException e) {
                //Player not online.
            }
            CivMessage.sendTown(town, residentToKick.getName()+" has been evicted from town by "+resident.getName());
            return;
        }
        residentToKick.setDaysTilEvict(CivSettings.GRACE_DAYS);
        residentToKick.warnEvict();
        residentToKick.save();
        CivMessage.sendSuccess(sender, args[1]+" will be evicted from town in "+CivSettings.GRACE_DAYS+" days.");
        */
    }

    public void survey_cmd() {
        // Get data
        if (!(sender instanceof Player player)) return;
        Chunk chunk = player.getLocation().getChunk();
        ChunkCoord coord = new ChunkCoord(player.getWorld().getName(), chunk.getX(), chunk.getZ());
        Yields stats = CivGlobal.calculateTownYields(player.getWorld(), coord);

        // List out biomes
        StringBuilder biomeList = new StringBuilder();
        for (String biomeName : stats.biomes.keySet()) {
            int count = stats.biomes.get(biomeName);
            String[] parts = biomeName.split("_");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1).toLowerCase();
            }
            biomeName = String.join(" ", parts);
            biomeList.append(CivColor.Gold).append(biomeName).append(": ").append(CivColor.White).append(count).append(CivColor.Gold).append(" | ");
        }

        // Print to chat
        CivMessage.sendHeading(player, "Survey Results");
        CivMessage.send(player, CivColor.LightGreen + CivColor.BOLD + "Biome Counts:");
        CivMessage.send(player, biomeList.toString());
        CivMessage.send(player, CivColor.LightGreen + CivColor.BOLD + "Totals:");
        CivMessage.send(player, String.format(
                "%sProduction:%s %,.2f %s| Science:%s %,.2f %s| Growth:%s %,.2f %s| Happiness:%s %,.2f",
                CivColor.Gold, CivColor.White,
                stats.getProduction(),
                CivColor.Gold, CivColor.White,
                stats.getScience(),
                CivColor.Gold, CivColor.White,
                stats.getGrowth(),
                CivColor.Gold, CivColor.White,
                stats.getHappiness()
        ));
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
