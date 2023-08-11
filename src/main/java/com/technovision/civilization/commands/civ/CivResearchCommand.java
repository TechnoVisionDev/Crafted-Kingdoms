package com.technovision.civilization.commands.civ;

import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.commands.CommandBase;
import com.technovision.civilization.data.objects.Civilization;
import com.technovision.civilization.data.objects.Research;
import com.technovision.civilization.data.objects.Resident;
import com.technovision.civilization.data.enums.Technology;
import com.technovision.civilization.exceptions.CivException;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;

import java.util.List;

public class CivResearchCommand extends CommandBase {

    public CivResearchCommand(CivilizationPlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        command = "/civ research";
        displayName = "Civ Research";

        commands.put("list", "List the available technologies we can research.");
        commands.put("progress", "Shows progress on your current research.");
        commands.put("on", "[tech] - Starts researching on this technology.");
        commands.put("change", "[tech] - Stops researching our current tech, changes to this. You will lose all progress on your current tech.");
        commands.put("finished", "Shows which technologies we already have.");
    }

    public void list_cmd() {
        Civilization civ = getSenderCiv();
        List<Technology> techs = civ.getAvailableTechs();
        CivMessage.sendHeading(sender, "Available Research");
        for (Technology tech : techs) {
            CivMessage.send(sender, String.format("%s %sCoins:%s %,d %sScience: %s%,d",
                    tech.getName(),
                    CivColor.LightGray, CivColor.Yellow,
                    tech.getMoneyCost(),
                    CivColor.LightGray, CivColor.Yellow,
                    tech.getScienceCost()
            ));
        }
    }

    public void progress_cmd() {
        Civilization civ = getSenderCiv();
        Research research = civ.getCurrentResearch();
        CivMessage.sendHeading(sender, "Currently Researching");
        if (research != null) {
            double scienceCost = research.getCost();
            Technology tech = Technology.valueOf(civ.getCurrentResearch().getTechnology());
            int percentageComplete = (int)((civ.getCurrentResearch().getProgress() / scienceCost)*100);
            if (percentageComplete > 100) percentageComplete = 100;
            CivMessage.send(sender, String.format("%s%s %sis %s%d%%%s complete. %s(%,.2f / %,.2f)",
                    CivColor.Yellow,
                    tech.getName(),
                    CivColor.LightGray, CivColor.Yellow,
                    percentageComplete,
                    CivColor.LightGray, CivColor.Yellow,
                    civ.getCurrentResearch().getProgress(),
                    scienceCost
            ));
        } else {
            CivMessage.send(sender, CivColor.LightGray + "Nothing currently researching.");
        }

    }

    public void on_cmd() throws CivException {
        Civilization civ = getSenderCiv();
        if (civ.getCurrentResearch() != null && !civ.getCurrentResearch().isComplete()) {
            throw new CivException("You are already researching a tech. Use "
                    +CivColor.Yellow+"/civ research change"+ CivColor.Rose+" if needed.");
        }
        if (args.length < 2) {
            throw new CivException("Enter the name of the technology you want to research.");
        }

        String techName = combineArgs(stripArgs(args, 1));
        Technology tech = Technology.getByName(techName);
        if (tech == null) {
            throw new CivException("Couldn't find technology named "+CivColor.Yellow+techName+CivColor.Rose+".");
        }
        if (civ.hasResearched(tech)) {
            throw new CivException("You have already researched the "+CivColor.Yellow+tech.getName()+CivColor.Rose+" technology.");
        }
        if (!civ.canResearch(tech)) {
            throw new CivException("You cannot research "+CivColor.Yellow+tech.getName()+CivColor.Rose+" at this time.");
        }
        if (!civ.canAfford(tech.getMoneyCost())) {
            throw new CivException("You do not have enough coins to research "+CivColor.Yellow+tech.getName()+CivColor.Rose+".");
        }
        civ.deductFromTreasury(tech.getMoneyCost());
        // TODO: Fix this!
        //plugin.civHandler.startResearch(civ, tech);

        CivMessage.sendCiv(civ, "Our Civilization started researching "+tech.getName());
        CivMessage.sendSuccess(sender, "You have started researching "+CivColor.Yellow+tech.getName());
    }

    public void change_cmd() throws CivException {
        Civilization civ = getSenderCiv();
        if (args.length < 2) {
            list_cmd();
            throw new CivException("Enter the name of the technology you want to change to.");
        }

        String techName = combineArgs(stripArgs(args, 1));
        Technology tech = Technology.getByName(techName);
        if (tech == null) {
            throw new CivException("Couldn't find technology named "+CivColor.Yellow+techName+CivColor.Rose+".");
        }
        if (civ.hasResearched(tech)) {
            throw new CivException("You have already researched the "+CivColor.Yellow+tech.getName()+CivColor.Rose+" technology.");
        }
        if (!civ.canResearch(tech)) {
            throw new CivException("You cannot research "+CivColor.Yellow+tech.getName()+CivColor.Rose+" at this time.");
        }
        if (!civ.canAfford(tech.getMoneyCost())) {
            throw new CivException("You do not have enough coins to research "+CivColor.Yellow+tech.getName()+CivColor.Rose+".");
        }
        Research currResearch = civ.getCurrentResearch();
        if (currResearch != null) {
            currResearch.setProgress(0);
            String currTechName = Technology.valueOf(currResearch.getTechnology()).getName();
            CivMessage.send(sender, CivColor.Rose+"Progress on "+CivColor.Yellow+currTechName+CivColor.Rose+" has been lost.");
            civ.setCurrentResearch(null);
        }
        civ.deductFromTreasury(tech.getMoneyCost());
        // TODO: Fix this!
        //plugin.civHandler.startResearch(civ, tech);

        CivMessage.sendCiv(civ, "Our Civilization started researching "+tech.getName());
        CivMessage.sendSuccess(sender, "You have started researching "+CivColor.Yellow+tech.getName());
    }

    public void finished_cmd() throws CivException {
        Civilization civ = getSenderCiv();
        CivMessage.sendHeading(sender, "Researched Technologies");
        if (civ.getTechnology().isEmpty()) {
            throw new CivException(CivColor.LightGray + "We have not researched anything yet.");
        }

        // Print out technologies to console
        StringBuilder out = new StringBuilder();
        for (String key : civ.getTechnology()) {
            Technology tech = Technology.valueOf(key);
            out.append(CivColor.Yellow).append(tech.getName()).append(CivColor.LightGray).append(", ");
        }
        CivMessage.send(sender, out.toString());
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
        Resident resident = getResident();
        Civilization civ = getSenderCiv();
        if (civ == null) {
            throw new CivException("You must be in a civilization to run that command!");
        }
        if (!civ.getLeaderID().equals(resident.getPlayerID())) {
            throw new CivException("Only civ leaders and advisers can access research.");
        }
    }
}
