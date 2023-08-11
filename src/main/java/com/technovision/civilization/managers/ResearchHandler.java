package com.technovision.civilization.managers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.technovision.civilization.CivilizationPlugin;
import com.technovision.civilization.data.Database;
import com.technovision.civilization.data.objects.*;
import com.technovision.civilization.data.enums.Technology;
import com.technovision.civilization.queries.chat.ChatQuery;
import com.technovision.civilization.util.CivColor;
import com.technovision.civilization.util.CivMessage;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class ResearchHandler implements Listener {

    public static final long TICK_RESEARCH = 60 * ChatQuery.ONE_MINUTE; // One hour

    private final HashMap<String, Integer> researchTicks;
    private final CivilizationPlugin plugin;

    public ResearchHandler(CivilizationPlugin plugin) {
        this.plugin = plugin;
        this.researchTicks = new HashMap<>();
    }

    public void startResearch(Civilization civ, Technology technology) {
        // Update data
        civ.setCurrentResearch(new Research(technology));
        Bson update = Updates.set("currentResearch", civ.getCurrentResearch());
        Database.civilizations.updateOne(Filters.eq("name", civ.getName()), update);

        // Schedule research tick
        cancelResearchTick(civ);
        scheduleResearchTick(civ);
    }

    private void scheduleResearchTick(Civilization civ) {
        researchTicks.put(civ.getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                civ.tickResearch();
                if (civ.isResearchComplete()) {
                    CivMessage.sendCiv(civ, CivColor.LightGreen+"Your civilization have researched a new tech!");
                    cancelResearchTick(civ);
                }
            }, TICK_RESEARCH, TICK_RESEARCH));
    }

    private void cancelResearchTick(Civilization civ) {
        if (researchTicks.containsKey(civ.getName())) {
            Bukkit.getScheduler().cancelTask(researchTicks.remove(civ.getName()));
        }
    }
}
