package com.technovision.craftedkingdoms.commands.group;

import com.technovision.craftedkingdoms.CraftedKingdoms;
import com.technovision.craftedkingdoms.commands.CommandBase;
import com.technovision.craftedkingdoms.exceptions.CKException;

/**
 * Handles group commands.
 *
 * @author TechnoVision
 */
public class GroupPermsCommand extends CommandBase {

    public GroupPermsCommand(CraftedKingdoms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/group perms";
        displayName = "Group Permissions";

        commands.put("add", "[group] [rank] [permission] - Add a permission to a group's player rank.");
        commands.put("remove", "[group] [rank] [permission] - Remove a permission from a group's player rank.");
    }

    public void add_cmd() throws CKException {

    }

    public void remove_cmd() throws CKException {

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
