package com.technovision.craftedkingdoms.commands.group;

import com.technovision.craftedkingdoms.CKGlobal;
import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.commands.CommandBase;
import com.technovision.craftedkingdoms.exceptions.CKException;
import com.technovision.craftedkingdoms.util.EffectUtils;
import com.technovision.craftedkingdoms.util.MessageUtils;
import com.technovision.craftedkingdoms.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Handles group commands.
 *
 * @author TechnoVision
 */
public class GroupCommand extends CommandBase {

    public GroupCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/group";
        displayName = "Group";

        // Implemented
        commands.put("create", "[name] - Create a new group.");

        // Not Yet Implemented
    }

    public void create_cmd() throws CKException {
        // Get group name from args
        if (args.length < (1)) {
            throw new CKException("You must enter a name for your group.");
        }
        String name = args[1];
        name = name.replace(" ", "_");
        name = name.replace("\"", "");
        name = name.replace("\'", "");

        // Error checking
        Player player = getPlayer();
        if (CKGlobal.isInGroup(player)) {
            throw new CKException("You must leave your current group first.");
        }
        if (name.length() > 30) {
            throw new CKException("Your group name must be under 30 characters.");
        }
        if (!StringUtils.isAlpha(name)) {
            throw new CKException("Your group name can only contain letters [A-Z].");
        }
        if (CKGlobal.isGroup(name)) {
            throw new CKException("A group named "+ChatColor.YELLOW+args[1]+ChatColor.RED+" already exists!");
        }
        CKGlobal.createGroup(name, player);

        // Send success message & firework
        MessageUtils.send(player, " ");
        MessageUtils.sendHeading(player, "You Created a Group!");
        String[] msg = {
                ChatColor.GREEN + "You can manage your group by using",
                ChatColor.GREEN + "the /group command in chat! ",
                " ",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group" + ChatColor.GRAY + " to manage your group.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group invite" + ChatColor.GRAY + " to invite players.",
                ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/group set" + ChatColor.GRAY + " to set a display name.",
                " "
        };
        MessageUtils.send(player, msg);
        EffectUtils.firework(EffectUtils.greenFirework, player.getLocation().add(0, -1, 0));
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
