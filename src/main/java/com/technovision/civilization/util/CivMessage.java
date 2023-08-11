package com.technovision.civilization.util;

import com.technovision.civilization.data.objects.Civilization;
import com.technovision.civilization.data.objects.Town;
import com.technovision.civilization.exceptions.CivException;
import com.technovision.civilization.queries.command.CommandQuery;
import com.technovision.civilization.threading.TaskMaster;
import com.technovision.civilization.threading.tasks.PlayerQuestionTask;
import com.technovision.civilization.threading.tasks.QuestionBaseTask;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CivMessage {

    private static final Map<String, QuestionBaseTask> QUESTIONS = new ConcurrentHashMap<String, QuestionBaseTask>();

    public static void sendError(Object sender, String line) {
        send(sender, CivColor.Rose+line);
    }

    public static void send(Object sender, String line) {
        if ((sender instanceof Player)) {
            ((Player) sender).sendMessage(line);
        } else if (sender instanceof CommandSender) {
            ((CommandSender) sender).sendMessage(line);
        }
    }
    public static void send(Object sender, String[] lines) {
        boolean isPlayer = false;
        if (sender instanceof Player)
            isPlayer = true;

        for (String line : lines) {
            if (isPlayer) {
                ((Player) sender).sendMessage(line);
            } else {
                ((CommandSender) sender).sendMessage(line);
            }
        }
    }

    public static void sendTown(Town town, String message) {
        for (UUID id : town.getMembers()) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                send(player, message);
            }
        }
    }

    public static void sendCiv(Civilization civ, String message) {
        for (UUID id : civ.getMembers()) {
            Player player = Bukkit.getPlayer(id);
            if (player != null) {
                send(player, message);
            }
        }
    }

    public static void sendSuccess(CommandSender sender, String message) {
        send(sender, CivColor.LightGreen+message);
    }

    public static void sendHeading(CommandSender sender, String title) {
        send(sender, buildTitle(title));
    }

    public static String buildTitle(String title) {
        String line =   "-------------------------------------------------";
        String titleBracket = "[ " + CivColor.Gold + title + CivColor.LightBlue + " ]";

        if (titleBracket.length() > line.length()) {
            return CivColor.LightBlue+"-"+titleBracket+"-";
        }

        int min = (line.length() / 2) - titleBracket.length() / 2;
        int max = (line.length() / 2) + titleBracket.length() / 2;

        String out = CivColor.LightBlue + line.substring(0, Math.max(0, min));
        out += titleBracket + line.substring(max);

        return out;
    }

    public static String buildSmallTitle(String title) {
        String line =   CivColor.LightBlue+"------------------------------";

        String titleBracket = "[ "+title+" ]";

        int min = (line.length() / 2) - titleBracket.length() / 2;
        int max = (line.length() / 2) + titleBracket.length() / 2;

        String out = CivColor.LightBlue + line.substring(0, Math.max(0, min));
        out += titleBracket + line.substring(max);

        return out;
    }

    public static void sendCivFoundingMessage(Player player) {
        CivMessage.send(player, " ");
        CivMessage.send(player, " ");
        CivMessage.sendHeading(player, "You Founded a Civilization!");
        String[] msg = {
                CivColor.LightGreen + "You can manage your civilization by right",
                CivColor.LightGreen + "clicking on the nexus block! ",
                " ",
                CivColor.LightGray + "Use " + CivColor.Yellow + "/civ" + CivColor.LightGray + " to manage your empire",
                CivColor.LightGray + "Use " + CivColor.Yellow + "/town" + CivColor.LightGray + " to manage this town",
                CivColor.LightGray + "Use " + CivColor.Yellow + "/town claim" + CivColor.LightGray + " to claim land",
                " "
        };
        CivMessage.send(player, msg);
    }

    public static void questionPlayer(Player fromPlayer, Player toPlayer, String question, long timeout, CommandQuery finishedFunction) throws CivException {
        PlayerQuestionTask task = (PlayerQuestionTask) QUESTIONS.get(toPlayer.getName());
        if (task != null) {
            throw new CivException("Player already has a question pending, wait 30 seconds and try again.");
        }

        task = new PlayerQuestionTask(toPlayer, fromPlayer, question, timeout, finishedFunction);
        QUESTIONS.put(toPlayer.getName(), task);
        TaskMaster.asyncTask("", task, 0);
    }

    public static void removeQuestion(String playerName) {
        QUESTIONS.remove(playerName);
    }

    public static QuestionBaseTask getQuestionTask(String playerName) {
        return QUESTIONS.get(playerName);
    }
}
