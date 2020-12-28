package net.silthus.rccities.manager;

import lombok.Setter;
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

    public int getPlotCount(City city) {
        return TPlot.find.query().where().eq("city_id", city.getId()).findCount();
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
        int citySize = city.getSize();
        int cityPlotCredit = city.getPlotCredit();
        double baseCost = config.getNewPlotCost();
        for(int i = 0; i < count; i++) {
            cost += baseCost + (config.getNewPlotCostPerOldPlot() * (cityPlotCredit + citySize + i));
        }
        return cost;
    }

    public void clearCache() {

        cachedPlots.clear();
    }

    public boolean migrateOldPlot(City city, Location location) {

        // Delete old plot region
        if(!plugin.getWorldGuardManager().deleteOldPlotRegion(city.getFriendlyName(), location)) {
            return false;
        }

        plugin.getLogger().info("Ein alter Plot (" + location.getChunk().getX()
                + "/" + location.getChunk().getZ() + ") von " + city.getFriendlyName() + " wurde migriert!");

        // Create new plot
        Plot plot = new DatabasePlot(location, city);

        return true;
    }

    public void migrateAllPlots(City city) {

        Map<String, Location> oldPlotMap = plugin.getWorldGuardManager()
                .getAllOldPlots(city.getFriendlyName(), city.getSpawn().getWorld());

        if(oldPlotMap.size() == 0) return;

        OldPlotMigrationTask oldPlotMigrationTask = new OldPlotMigrationTask(city, oldPlotMap);
        plugin.getLogger().info("Es werden nun " + oldPlotMap.size() + " Plots von "
                + city.getFriendlyName() + " migriert...");
        int taskId = Bukkit.getScheduler()
                .runTaskTimer(RCCitiesPlugin.instance(), oldPlotMigrationTask, 0, 10).getTaskId();
        oldPlotMigrationTask.setTaskId(taskId);
    }

    private class OldPlotMigrationTask implements Runnable {

        City city;
        Map<String, Location> oldPlotMap;
        @Setter
        int taskId = -1;
        int totalCount = 0;
        int goodCount = 0;
        int failCount = 0;
        int doneCount = 0;

        public OldPlotMigrationTask(City city, Map<String, Location> oldPlotMap) {

            this.city = city;
            this.oldPlotMap = oldPlotMap;
            totalCount = oldPlotMap.size();
        }

        @Override
        public void run() {

            if(oldPlotMap.size() > 0) {
                Map.Entry<String, Location> nextEntry = oldPlotMap.entrySet().iterator().next();
                Location location = nextEntry.getValue();
                if(migrateOldPlot(city, location)) {
                    goodCount++;
                } else {
                    failCount++;
                }
                doneCount++;

                oldPlotMap.remove(nextEntry.getKey());

                if(doneCount % 20 == 0) {
                    plugin.getLogger().info("Migration von " + city.getFriendlyName() + ": Total(" + totalCount
                            + ") Fertig(" + goodCount + ") Fehler(" + failCount + ") Offen("
                            + (totalCount - doneCount) + ")");
                }
            }

            if(oldPlotMap.size() == 0 && taskId != -1) {
                plugin.getLogger().info("Es wurden " + goodCount + " Plots von "
                        + city.getFriendlyName() + " migriert!");
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
    }
}
