package net.silthus.rccities.manager;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.DatabasePlot;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.tables.TPlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class PlotManager {

    private RCCitiesPlugin plugin;
    private Map<Location, Plot> cachedPlots = new HashMap<>();

    public PlotManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    public void printPlotInfo(Plot plot, CommandSender sender) {

        String assignmentsList = "";
        int assignmentCount = 0;
        for (Resident resident : plot.getAssignedResidents()) {
            if (!assignmentsList.isEmpty()) assignmentsList += ChatColor.GRAY + ", ";
            assignmentsList += ChatColor.YELLOW + resident.getName();
            assignmentCount++;
        }

        sender.sendMessage("*********************************");
        sender.sendMessage(ChatColor.GOLD + "Informationen zum Plot '" + ChatColor.YELLOW + plot.getRegionName() + ChatColor.GOLD + "'");
        sender.sendMessage(ChatColor.GOLD + "Stadt: " + ChatColor.YELLOW + plot.getCity().getFriendlyName());
        sender.sendMessage(ChatColor.GOLD + "Besitzer (" + assignmentCount + "): " + ChatColor.YELLOW + assignmentsList);
        sender.sendMessage("*********************************");
    }

    public List<Plot> getPlots(City city) {

        List<Plot> plots = new ArrayList<>();
        List<TPlot> tPlots = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TPlot.class).where().eq("city_id", city.getId()).findList();
        for (TPlot tPlot : tPlots) {
            Location plotLocation = new Location(city.getSpawn().getWorld(), tPlot.getX(), 0, tPlot.getZ());
            if (!cachedPlots.containsKey(plotLocation) && Bukkit.getWorld(tPlot.getCity().getWorld()) != null) {
                Plot plot = new DatabasePlot(tPlot);
                cachedPlots.put(plotLocation, plot);
                plots.add(plot);
            } else {
                plots.add(cachedPlots.get(plotLocation));
            }
        }
        return plots;
    }

    public void removeFromCache(Plot plot) {

        cachedPlots.remove(plot.getLocation());
    }

    public Plot getPlot(int id) {

        for (Plot plot : cachedPlots.values()) {
            if (plot.getId() == id) {
                return plot;
            }
        }
        TPlot tPlot = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TPlot.class, id);
        if (tPlot != null && Bukkit.getWorld(tPlot.getCity().getWorld()) != null) {
            Plot plot = new DatabasePlot(tPlot);
            cachedPlots.put(plot.getLocation(), plot);
            return plot;
        }
        return null;
    }

    public Plot getPlot(Chunk chunk) {

        Location simpleLocation = new Location(chunk.getWorld(), chunk.getX() * 16 + 8, 0, chunk.getZ() * 16 + 8);
        Plot plot = cachedPlots.get(simpleLocation);

        if (plot == null) {
            TPlot tPlot = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TPlot.class).where().eq("x", simpleLocation.getX()).eq("z", simpleLocation.getZ()).findUnique();
            if (tPlot != null && Bukkit.getWorld(tPlot.getCity().getWorld()) != null) {
                plot = new DatabasePlot(tPlot);
                cachedPlots.put(plot.getLocation(), plot);
            }
        }
        return plot;
    }

    public void clearCache() {

        cachedPlots.clear();
    }
}
