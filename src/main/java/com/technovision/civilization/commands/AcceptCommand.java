package com.technovision.civilization.commands;

import com.technovision.civilization.threading.tasks.PlayerQuestionTask;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (!(sender instanceof Player player)) {
            CivMessage.sendError(sender, "Only a player can execute this command.");
            return false;
        }

        PlayerQuestionTask task = (PlayerQuestionTask) CivMessage.getQuestionTask(player.getName());
        if (task != null) {
            // We have a question, and the answer was "Accepted" so notify the task.
            synchronized(task) {
                task.setResponse("accept");
                task.notifyAll();
            }
            return true;
        }

        /**
        Resident resident = CivGlobal.getResident(player);
        if (resident.hasTown()) {
            if (resident.getCiv().getLeaderGroup().hasMember(resident)) {
                CivLeaderQuestionTask civTask = (CivLeaderQuestionTask) CivMessage.getQuestionTask("civ:"+resident.getCiv().getName());

                synchronized(civTask) {
                    civTask.setResponse("accept");
                    civTask.setResponder(resident);
                    civTask.notifyAll();
                }
                return true;
            }
        }
         */

        CivMessage.sendError(sender, "No question to respond to.");
        return true;
    }
}
