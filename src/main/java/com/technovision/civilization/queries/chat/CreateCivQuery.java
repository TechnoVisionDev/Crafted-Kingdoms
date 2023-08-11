package com.technovision.civilization.queries.chat;

import com.technovision.civilization.CivGlobal;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.data.Database;
import com.technovision.civilization.data.objects.Civilization;
import com.technovision.civilization.data.objects.Town;
import com.technovision.civilization.exceptions.AlreadyRegisteredException;
import com.technovision.civilization.exceptions.CivException;
import com.technovision.civilization.managers.ItemManager;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
import com.technovision.civilization.util.Effects;
import com.technovision.civilization.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateCivQuery extends ChatQuery {

    public String townName;
    public String civName;
    private boolean awaitingCivName = true;

    public CreateCivQuery(Player player) {
        super(player, ChatQuery.ONE_MINUTE * 3);
    }

    public void start() {
        queryCivName(getPlayer());
    }

    public boolean handleResponse(String message) {
        if (message.equalsIgnoreCase("cancel")) {
            cancel("Setup has been canceled!");
            return false;
        }
        if (awaitingCivName) {
            if (!StringUtils.isAlpha(message)) {
                cancel("Civilization names can only contain letters (A-Z)!");
                return false;
            }
            if (CivGlobal.isCiv(message)) {
                cancel("A civilization named '" + message + "' already exists!");
                return false;
            }
            civName = message;
            awaitingCivName = false;
            queryCapitalName(getPlayer());
            return false;
        } else {
            if (!StringUtils.isAlpha(message)) {
                cancel("Civilization names can only contain letters (A-Z)!");
                return false;
            }
            if (CivGlobal.isTown(message)) {
                cancel("A town named '" + message + "' already exists!");
                return false;
            }
            townName = message;
            Bukkit.getScheduler().scheduleSyncDelayedTask(CivilizationPlugin.plugin, this::createCivAndTown);
            return true;
        }
    }

    private void createCivAndTown() {
        try {
            // Check that player has founders flag item in hand
            ItemStack stack = getPlayer().getInventory().getItemInMainHand();
            if (!stack.isSimilar(ItemManager.founders_flag)) {
                cancel("You must be holding a founder's flag.");
            }

            // Create capitol town and claim chunk
            Town capitol = new Town(townName, civName, getPlayer(), true);
            CivGlobal.addTown(townName, capitol);
            Database.towns.insertOne(capitol);

            // Create civilization
            Civilization civ = new Civilization(civName, getPlayer(), capitol.getName(), capitol.getNexus().getChunkCoord());
            CivGlobal.addCiv(civName, civ);
            Database.civilizations.insertOne(civ);

            // Update player's resident data
            try {
                CivGlobal.getResident(getPlayer()).joinTown(capitol.getName());
            } catch (AlreadyRegisteredException e) {
                CivMessage.sendError(getPlayer(), "You are already a member of this town.");
            }

            // Remove item from player's hand
            getPlayer().getInventory().getItemInMainHand().setAmount(stack.getAmount()-1);
            cancel(null);
            CivMessage.sendCivFoundingMessage(getPlayer());

            // Spawn firework effect above nexus
            Effects.firework(Effects.greenFirework, getPlayer().getLocation().add(0, 1, 0));
        } catch (CivException e) {
            cancel(e.getMessage());
        }
    }

    private static void queryCivName(Player player) {
        CivMessage.send(player, " ");
        CivMessage.send(player, " ");
        CivMessage.sendHeading(player, "Founding a Civilization!");
        String[] msg = {
                CivColor.LightPurple + "The time has come to establish sovereign rule",
                CivColor.LightPurple + "over your people.",
                " ",
                CivColor.LightPurple + CivColor.BOLD + "What shall your civilization be called?",
                CivColor.LightGray + "(To cancel, type `cancel`)",
                " ", " "
        };
        CivMessage.send(player, msg);
    }

    private static void queryCapitalName(Player player) {
        CivMessage.send(player, " ");
        CivMessage.send(player, " ");
        CivMessage.sendHeading(player, "Establish a Capital!");
        String[] msg = {
                CivColor.LightGreen + "Now it's time to settle the capital of your",
                CivColor.LightGreen + "soon-to-be sprawling empire.",
                " ",
                CivColor.LightGreen + CivColor.BOLD + "What shall your capital be called?",
                CivColor.LightGray + "(To cancel, type `cancel`)",
                " ", " "
        };
        CivMessage.send(player, msg);
    }
}

