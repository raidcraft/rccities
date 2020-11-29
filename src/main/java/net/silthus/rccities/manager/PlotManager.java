package net.silthus.rccities.manager;

import net.silthus.rccities.DatabasePlot;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.tables.TPlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.*;

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
        List<TPlot> tPlots = TPlot.find.query().where().eq("city_id", city.getId()).findList();
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

    public Plot getPlot(UUID id) {

        for (Plot plot : cachedPlots.values()) {
            if (plot.getId() == id) {
                return plot;
            }
        }
        TPlot tPlot = TPlot.find.byId(id);
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
            TPlot tPlot = TPlot.find.query().where().eq("x", simpleLocation.getX()).eq("z", simpleLocation.getZ()).findOne();
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
