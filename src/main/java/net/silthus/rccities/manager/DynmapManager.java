package net.silthus.rccities.manager;

import lombok.Getter;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * Author: Philip
 * Date: 12.12.12 - 22:16
 * Description:
 */
public class DynmapManager {

    private static String MARKER_SET_NAME = "rccities";
    private RCCitiesPlugin plugin;
    private DynmapAPI dynmap;

    @Getter
    private class API {

        MarkerAPI markerAPI;
        MarkerSet markerSet;

        public API(MarkerAPI markerAPI, MarkerSet markerSet) {
            this.markerAPI = markerAPI;
            this.markerSet = markerSet;
        }
    }

    public DynmapManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;

        Plugin dynmapPlugin = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if (dynmapPlugin == null) {
            return;
        }
        dynmap = (DynmapAPI)dynmapPlugin;
    }

    private API getApi() {

        if (dynmap == null) {
            plugin.getLogger().warning("Dynmap not installed!");
            return null;
        }

        MarkerSet cityMarkers = dynmap.getMarkerAPI().getMarkerSet(MARKER_SET_NAME);

        if(cityMarkers == null) {
            plugin.getLogger().warning("Create Dynmap marker set");
            cityMarkers = dynmap.getMarkerAPI().createMarkerSet(MARKER_SET_NAME, plugin.getPluginConfig().getDynmapMarkerSetLabel(),
                    dynmap.getMarkerAPI().getMarkerIcons(), true /* persistent */);
        }

        return new API(dynmap.getMarkerAPI(), cityMarkers);
    }

    public void addCityMarker(City city) {

        API api = getApi();
        if (api == null) {
            return;
        }

        if (city == null) {
            return;
        }

        removeCityMarker(city);

        String cityMarkerId = city.getName().toLowerCase().replace(" ", "_");

        api.getMarkerSet().createMarker(cityMarkerId
                , city.getFriendlyName()
                , city.getSpawn().getWorld().getName()
                , city.getSpawn().getBlockX()
                , city.getSpawn().getBlockY()
                , city.getSpawn().getBlockZ()
                , api.getMarkerAPI().getMarkerIcon("bighouse")
                , true /* persistent */);
    }

    public void removeCityMarker(City city) {

        API api = getApi();
        if (api == null) {
            return;
        }

        String markerId = city.getName().toLowerCase().replace(" ", "_");
        Marker marker = api.getMarkerSet().findMarker(markerId);
        if(marker != null) {
            marker.deleteMarker();
        }
    }

    public void addPlotAreaMarker(Plot plot) {

        API api = getApi();
        if (api == null) {
            return;
        }

        if (plot == null) {
            return;
        }

        double[] corner_x = { plot.getLocation().getX() - 8, plot.getLocation().getX() + 8 };
        double[] corner_y = { plot.getLocation().getZ() - 8, plot.getLocation().getZ() + 8 };

        // minus (-) is not supported as maker ID due to usage as YAML key
        String markerId = plot.getRegionName().replace("-", "m");

        AreaMarker areaMarker = api.getMarkerSet().createAreaMarker(
                markerId /* id */,
                plot.getRegionName() /* label */,
                false /* markup */,
                plot.getLocation().getWorld().getName() /* world */,
                corner_x,
                corner_y,
                true /* persistent */);
    }

    public void removePlotAreaMarker(Plot plot) {

        API api = getApi();
        if (api == null) {
            return;
        }

        String markerId = plot.getRegionName().replace("-", "m");
        AreaMarker marker = api.getMarkerSet().findAreaMarker(markerId);
        if(marker != null) {
            marker.deleteMarker();
        }
    }
}
