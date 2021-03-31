package net.silthus.rccities.manager;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.util.LocationUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Philip Urban
 */
public class WorldGuardManager implements Listener {

    private final RCCitiesPlugin plugin;

    public WorldGuardManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    public ApplicableRegionSet getChunkRegions(Location location) {
        int bx = LocationUtil.getChunkX(location) << 4;
        int bz = LocationUtil.getChunkZ(location) << 4;
        BlockVector3 pt1 = BlockVector3.at(bx, 0, bz);
        BlockVector3 pt2 = BlockVector3.at(bx + 15, 256, bz + 15);
        ProtectedCuboidRegion chunkRegion = new ProtectedCuboidRegion("chunkRegion", pt1, pt2);

        ApplicableRegionSet regions = plugin.getWorldGuard().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(chunkRegion);
        return regions;
    }

    public boolean claimable(String cityName, Location location) {

        ApplicableRegionSet regions = getChunkRegions(location);
        if (regions == null || regions.size() == 0) {
            return true;
        }
        for (ProtectedRegion region : regions) {
            boolean ignored = false;
            if(plugin.getPluginConfig().getIgnoredRegions().stream()
                    .anyMatch(a -> a.equalsIgnoreCase(region.getId()))) {
                ignored = true;
            }
            if(isOldPlotRegion(cityName, region)) {
                ignored = true;
            }
            if(!ignored) return false;
        }
        return true;
    }

    public void save() {

        for (World world : Bukkit.getServer().getWorlds()) {
            try {
                plugin.getWorldGuard().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).save();
            } catch (StorageException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
    }

    public boolean isOldPlotRegion(String cityName, ProtectedRegion region) {

        // Old region name pattern is: cit_id

        String regionName = region.getId();

        if(!plugin.getPluginConfig().isMigrateOldPlots()) {
            return false;
        }

        if(!regionName.startsWith(cityName.toLowerCase())) {
            return false;
        }

        // Strip city name
        regionName = regionName.replace(cityName.toLowerCase() + "_", "");

        // Check if only id is left
        return StringUtils.isNumeric(regionName);
    }

    public boolean isOldPlot(String cityName, Location location) {
        ApplicableRegionSet regions = getChunkRegions(location);
        if (regions.size() == 0) {
            return false;
        }

        for (ProtectedRegion region : regions) {
            if(isOldPlotRegion(cityName, region)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Location> getAllOldPlots(String cityName, World world) {

        if(!plugin.getPluginConfig().isMigrateOldPlots()) {
            return new HashMap<>();
        }

        String regionName = cityName.toLowerCase() + "_";

        // Get all regions with old plot name: city_id
        Map<String, Location> regions = plugin.getWorldGuard().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(world)).getRegions().entrySet().stream()
                .filter(map -> map.getKey().startsWith(regionName))
                .filter(map -> StringUtils.isNumeric(map.getKey().replace(regionName, "")))
                .collect(Collectors.toMap(map -> map.getKey(), map -> BukkitAdapter.adapt(world, map.getValue().getMinimumPoint())));
        return regions;
    }

    public boolean deleteOldPlotRegion(String cityName, Location location) {
        ApplicableRegionSet regions = getChunkRegions(location);
        if (regions.size() == 0) {
            return false;
        }

        for (ProtectedRegion region : regions) {
            if(isOldPlotRegion(cityName, region)) {
                plugin.getWorldGuard().getPlatform().getRegionContainer()
                        .get(BukkitAdapter.adapt(location.getWorld())).removeRegion(region.getId());
                return true;
            }
        }
        return false;
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {

        // Check if here is a city plot
        if(!plugin.getPlotManager().isInsidePlot(event.getBlock().getLocation())) {
            return;
        }

        // allow pistons
        event.setCancelled(false);
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {

        // Check if here is a city plot
        if(!plugin.getPlotManager().isInsidePlot(event.getBlock().getLocation())) {
            return;
        }

        // allow pistons
        event.setCancelled(false);
    }
}
