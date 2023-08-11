package com.technovision.tribes.commands.tribe;

import com.technovision.tribes.TribesGlobal;
import com.technovision.tribes.TribesPlugin;
import com.technovision.tribes.commands.CommandBase;
import com.technovision.tribes.data.Database;
import com.technovision.tribes.data.objects.Tribe;
import com.technovision.tribes.exceptions.TribesException;
import com.technovision.tribes.util.MessageUtils;
import com.technovision.tribes.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Handles tribe commands.
 *
 * @author TechnoVision
 */
public class TribeCommand extends CommandBase {

    public TribeCommand(TribesPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/tribe";
        displayName = "Tribe";

        // Implemented
        commands.put("create", "[name] - Create a new tribe.");

        // Not Yet Implemented
    }

    public void create_cmd() throws TribesException {
        // Get tribe name from args
        if (args.length < (1)) {
            throw new TribesException("You must enter a name for your tribe.");
        }
        String name = args[1];
        name = name.replace(" ", "_");
        name = name.replace("\"", "");
        name = name.replace("\'", "");

        // Error checking
        if (name.length() > 30) {
            throw new TribesException("Your tribe name must be under 30 characters.");
        }
        if (!StringUtils.isAlpha(name)) {
            throw new TribesException("Your tribe name can only contain letters [A-Z].");
        }
        if (TribesGlobal.isTribe(name)) {
            throw new TribesException("A tribe named "+ChatColor.YELLOW+args.length+ChatColor.RED+" already exists!");
        }

        // Create tribe and add to database
        Player player = getPlayer();
        Tribe tribe = new Tribe(name, player);
        TribesGlobal.addTribe(tribe);
        Database.tribes.insertOne(tribe);

        // Send success message
        MessageUtils.send(player, " ");
        MessageUtils.sendHeading(player, "You Founded a Tribe!");
        String[] msg = {
                ChatColor.GREEN + "You can manage your tribe by using",
                ChatColor.GREEN + "the /tribe command in chat! ",
                " ",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/tribe" + ChatColor.GRAY + " to manage your tribe.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/tribe invite" + ChatColor.GRAY + " to invite players.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/tribe set" + ChatColor.GRAY + " to set a display name.",
                " "
        };
        MessageUtils.send(player, msg);
    }

    @Override
    public void doDefaultAction() throws TribesException {
        showHelp();
    }

    @Override
    public void showHelp() {
        showBasicHelp();
    }

    @Override
    public void permissionCheck() throws TribesException {
    }
}
