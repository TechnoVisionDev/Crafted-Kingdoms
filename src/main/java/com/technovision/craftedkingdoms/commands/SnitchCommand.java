package com.technovision.craftedkingdoms.commands;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.data.enums.Permissions;
import com.technovision.craftedkingdoms.data.objects.Resident;
import com.technovision.craftedkingdoms.data.objects.Snitch;
import com.technovision.craftedkingdoms.data.objects.SnitchLog;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Handles command for utilizing snitch blocks.
 *
 * @author TechnoVision
 */
public class SnitchCommand extends CommandBase {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd h:mma");

    public SnitchCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/snitch";
        displayName = "Snitch";

        // Implemented
        commands.put("name", "<name> - Name the snitch you are looking at.");
        commands.put("logs", "<name> [page] - Print logs stored by a snitch.");
        commands.put("inspect", "Get details on the snitch you are looking at.");
    }

    public void inspect_cmd() throws CKException {
        Player player = getPlayer();
        Block targetBlock = player.getTargetBlockExact(10);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            throw new CKException("You must be looking at a Snitch block!");
        }

        Snitch snitch = CKGlobal.getSnitch(targetBlock.getLocation());
        if (snitch == null) {
            throw new CKException("You must be looking at a Snitch block!");
        }

        // Add Owner
        List<String> msg = new ArrayList<>();
        msg.add("" + ChatColor.AQUA + ChatColor.BOLD + "Name: " + ChatColor.GRAY + (snitch.getName() == null ? "None" : snitch.getName()));
        msg.add("" + ChatColor.AQUA + ChatColor.BOLD + "Group: " + ChatColor.GRAY + snitch.getGroup());
        msg.add("" + ChatColor.AQUA + ChatColor.BOLD + "Logs: " + ChatColor.GRAY + (snitch.getLog().isEmpty() ? "None" : snitch.getLog().size()));

        // Send message
        MessageUtils.sendHeading(sender, "Snitch Info");
        MessageUtils.send(sender, msg.toArray(new String[0]));
    }

    public void logs_cmd() throws CKException {
        Player player = getPlayer();
        Resident resident = getResident();
        if (args.length < 2) {
            throw new CKException("You must specify the snitch name!");
        }

        // Use snitch name specified
        String name = args[1];
        Snitch snitch = null;
        for (Snitch potentialSnitch : CKGlobal.SNITCHES.values()) {
            if (potentialSnitch.getName() != null && potentialSnitch.getName().equalsIgnoreCase(name)) {
                if (!resident.isInGroup(potentialSnitch.getGroup())) {
                    throw new CKException("You are not in the group that owns that snitch!");
                }
                if (!resident.hasPermission(potentialSnitch.getGroup(), Permissions.SNITCH_VIEW)) {
                    throw new CKException("You must have the " + ChatColor.YELLOW + Permissions.SNITCH_VIEW.name() + ChatColor.RED + " permission to do that!");
                }
                snitch = potentialSnitch;
                break;
            }
        }
        if (snitch == null) {
            throw new CKException("No snitch exists with that name!");
        }
        if (!snitch.isJukebox()) {
            throw new CKException("Only jukebox snitches store logs!");
        }
        List<SnitchLog> logs = snitch.getLog();
        Collections.reverse(logs);

        int page = 1;
        if (args.length == 3) {
            try {
                page = Integer.parseInt(args[2]);
                if (page < 1) {
                    throw new CKException("Page number must be 1 or higher!");
                }
            } catch (NumberFormatException e) {
                throw new CKException("Invalid page number!");
            }
        }

        int totalLogs = logs.size();
        int totalPages = (int) Math.ceil((double) totalLogs / 5);

        int startIndex = (page - 1) * 5;
        int endIndex = Math.min(startIndex + 5, logs.size());
        if (startIndex >= logs.size()) {
            MessageUtils.sendError(player, "There are no logs yet for this Snitch!");
            return;
        }

        List<String> msg = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            SnitchLog log = logs.get(i);
            String message = log.getMessage();
            Date date = log.getDate();
            // Send the message and date to the player
            msg.add(ChatColor.YELLOW + dateFormat.format(date) + ChatColor.GRAY + " - " + ChatColor.AQUA + message);
        }

        MessageUtils.sendHeading(player, name + " Logs (" + page + "/" + totalPages + ")");
        MessageUtils.send(player, msg.toArray(new String[0]));
    }

    public void name_cmd() throws CKException {
        if (args.length < 2) {
            throw new CKException("You must specify a snitch name!");
        }
        String name = args[1];

        Player player = getPlayer();
        Resident resident = getResident();
        Block targetBlock = player.getTargetBlockExact(10);
        if (targetBlock == null || targetBlock.getType() == Material.AIR) {
            throw new CKException("You must be looking at a Snitch block!");
        }

        Snitch snitch = CKGlobal.getSnitch(targetBlock.getLocation());
        if (snitch == null) {
            throw new CKException("You must be looking at a Snitch block!");
        }
        if (!resident.isInGroup(snitch.getGroup())) {
            throw new CKException("You are not in the group that owns that snitch!");
        }
        if (!resident.hasPermission(snitch.getGroup(), Permissions.SNITCH_NAME)) {
            throw new CKException("You must have the " + ChatColor.YELLOW + Permissions.SNITCH_NAME.name() + ChatColor.RED + " permission to do that!");
        }

        for (Snitch s : CKGlobal.SNITCHES.values()) {
            if (s.getName() != null && s.getName().equalsIgnoreCase(name)) {
                throw new CKException("A snitch already exists with that name!");
            }
        }

        snitch.assignName(name);
        MessageUtils.send(sender, ChatColor.GRAY + "You have named this snitch block to " + ChatColor.YELLOW + name);
    }

    @Override
    public void doDefaultAction() throws CKException {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() throws CKException {
    }
}
