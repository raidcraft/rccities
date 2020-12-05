package net.silthus.rccities.manager;

import co.aikar.commands.CommandIssuer;
import net.silthus.rccities.DatabasePlot;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.RCCitiesPluginConfig;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.tables.TPlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Philip Urban
 */
public class PlotManager {

    private final RCCitiesPlugin plugin;
    private final Map<Location, Plot> cachedPlots = new HashMap<>();

    public PlotManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    public void printPlotInfo(Plot plot, Player player) {

        String assignmentsList = "";
        int assignmentCount = 0;
        for (Resident resident : plot.getAssignedResidents()) {
            if (!assignmentsList.isEmpty()) assignmentsList += ChatColor.GRAY + ", ";
            assignmentsList += ChatColor.YELLOW + resident.getName();
            assignmentCount++;
        }

        player.sendMessage("*********************************");
        player.sendMessage(ChatColor.GOLD + "Informationen zum Plot '" + ChatColor.YELLOW + plot.getRegionName() + ChatColor.GOLD + "'");
        player.sendMessage(ChatColor.GOLD + "Stadt: " + ChatColor.YELLOW + plot.getCity().getFriendlyName());
        player.sendMessage(ChatColor.GOLD + "Besitzer (" + assignmentCount + "): " + ChatColor.YELLOW + assignmentsList);
        player.sendMessage("*********************************");
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

    public Plot getPlot(String regionName) {

        // Region name should be "city_chunkX_chunkZ"
        String[] parts = regionName.split("_");
        if(parts.length != 3) return null;

        int chunkX = 0;
        int chunkZ = 0;
        try {
            chunkX = Integer.parseInt(parts[1]);
            chunkZ = Integer.parseInt(parts[2]);

        } catch(NumberFormatException e) {
            return null;
        }

        for (Plot plot : cachedPlots.values()) {
            if (plot.getLocation().getChunk().getX() == chunkX
                    && plot.getLocation().getChunk().getZ() == chunkZ) {
                return plot;
            }
        }
        TPlot tPlot = TPlot.find.query()
                .where().eq("X", chunkX * 16 + 8).eq("Z", chunkZ * 16 + 8).findOne();
        if (tPlot != null && Bukkit.getWorld(tPlot.getCity().getWorld()) != null) {
            Plot plot = new DatabasePlot(tPlot);
            cachedPlots.put(plot.getLocation(), plot);
            return plot;
        }
        return null;
    }

    public Plot getPlot(Chunk chunk) {

        Location simpleLocation = new Location(chunk.getWorld(), chunk.getX() * 16 + 8, 0,
                chunk.getZ() * 16 + 8);
        Plot plot = cachedPlots.get(simpleLocation);

        if (plot == null) {
            TPlot tPlot = TPlot.find.query().where().eq("x", simpleLocation.getX())
                    .eq("z", simpleLocation.getZ()).findOne();
            if (tPlot != null && Bukkit.getWorld(tPlot.getCity().getWorld()) != null) {
                plot = new DatabasePlot(tPlot);
                cachedPlots.put(plot.getLocation(), plot);
            }
        }
        return plot;
    }

    public double getNewPlotCosts(City city, int count) {

        RCCitiesPluginConfig config = plugin.getPluginConfig();
        double cost = 0;
        for(int i = 0; i < count; i++) {
            cost += config.getNewPlotCost() + (config.getNewPlotCostPerOldPlot() * (city.getSize() + i));
        }
        return cost;
    }

    public void clearCache() {

        cachedPlots.clear();
    }
}
