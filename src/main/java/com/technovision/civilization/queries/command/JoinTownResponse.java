package com.technovision.civilization.queries.command;

import com.technovision.civilization.data.objects.Resident;
import com.technovision.civilization.data.objects.Town;
import com.technovision.civilization.exceptions.AlreadyRegisteredException;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
import org.bukkit.entity.Player;

public class JoinTownResponse implements CommandQuery {

    public Town town;
    public Resident resident;
    public Player sender;

    @Override
    public void processResponse(String param) {
        String resName = CivColor.Yellow+resident.getPlayerName()+CivColor.LightGray;
        if (param.equalsIgnoreCase("accept")) {
            CivMessage.send(sender, resName+" accepted our town invitation.");
            try {
                town.addResident(resident);
            } catch (AlreadyRegisteredException e) {
                CivMessage.sendError(sender, resName+" is already a town member.");
                return;
            }
            CivMessage.sendTown(town, resName+" has joined the town of "+CivColor.Yellow+town.getName());
        } else {
            CivMessage.send(sender, resName+" denied our town invitation.");
        }
    }

    @Override
    public void processResponse(String response, Resident responder) {
        processResponse(response);
    }
}
