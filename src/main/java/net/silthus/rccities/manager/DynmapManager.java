package net.silthus.rccities.manager;

import lombok.Getter;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;

/**
 * Author: Philip
 * Date: 12.12.12 - 22:16
 * Description:
 */
public class DynmapManager {

    private final static String CITY_MARKER_SET = "rccities_cities";
    private final static String PLOT_MARKER_SET = "rccities_plots";
    private final RCCitiesPlugin plugin;
    private DynmapAPI dynmap;

    @Getter
    private class API {

        MarkerAPI markerAPI;
        MarkerSet cityMarkerSet;
        MarkerSet plotMarkerSet;

        public API(MarkerAPI markerAPI, MarkerSet cityMarkerSet, MarkerSet plotMarkerSet) {
            this.markerAPI = markerAPI;
            this.cityMarkerSet = cityMarkerSet;
            this.plotMarkerSet = plotMarkerSet;
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

        MarkerSet cityMarkers = dynmap.getMarkerAPI().getMarkerSet(CITY_MARKER_SET);
        MarkerSet plotMarkers = dynmap.getMarkerAPI().getMarkerSet(PLOT_MARKER_SET);

        if(cityMarkers == null) {
            plugin.getLogger().warning("Create Dynmap city marker set");
            cityMarkers = dynmap.getMarkerAPI().createMarkerSet(CITY_MARKER_SET, plugin.getPluginConfig().getDynmap().getCityMarkerSetLabel(),
                    null /* icon limit */, true /* persistent */);
        }

        if(plotMarkers == null) {
            plugin.getLogger().warning("Create Dynmap plot marker set");
            plotMarkers = dynmap.getMarkerAPI().createMarkerSet(PLOT_MARKER_SET, plugin.getPluginConfig().getDynmap().getPlotMarkerSetLabel(),
                    null /* icon limit */, true /* persistent */);
        }

        return new API(dynmap.getMarkerAPI(), cityMarkers, plotMarkers);
    }

    private String getCityMarkerId(City city) {
        return city.getName().toLowerCase().replace(" ", "_");
    }

    private String getCityCircleId(City city) {
        return getCityMarkerId(city) + "_radius";
    }

    private String getPlotAreaMarkerId(Plot plot) {
        return plot.getRegionName().replace("-", "m");
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

        api.getCityMarkerSet().createMarker(getCityMarkerId(city),
                city.getFriendlyName(),
                city.getSpawn().getWorld().getName(),
                city.getSpawn().getBlockX(),
                city.getSpawn().getBlockY(),
                city.getSpawn().getBlockZ(),
                api.getMarkerAPI().getMarkerIcon("bighouse"),
                true /* persistent */);

        CircleMarker circleMarker = api.getCityMarkerSet().createCircleMarker(getCityCircleId(city),
                city.getFriendlyName(),
                false,
                city.getSpawn().getWorld().getName(),
                city.getSpawn().getX(),
                city.getSpawn().getY(),
                city.getSpawn().getZ(),
                city.getMaxRadius(),
                city.getMaxRadius(),
                true /* persistent */);

        if(circleMarker != null) {
            circleMarker.setFillStyle(0, 0); // Set filling it transparent
            circleMarker.setLineStyle(
                    plugin.getPluginConfig().getDynmap().getPlotMarkerLineWeight() /* Line weight */,
                    1.0 /* Opacity */,
                    plugin.getPluginConfig().getDynmap().getPlotMarkerRGBLineColor() /* Color in RGB */);
        }
    }

    public void removeCityMarker(City city) {

        API api = getApi();
        if (api == null) {
            return;
        }

        Marker marker = api.getCityMarkerSet().findMarker(getCityMarkerId(city));
        if(marker != null) {
            marker.deleteMarker();
        }

        CircleMarker circleMarker = api.getCityMarkerSet().findCircleMarker(getCityCircleId(city));
        if(circleMarker != null) {
            circleMarker.deleteMarker();
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

        AreaMarker areaMarker = api.getPlotMarkerSet().createAreaMarker(
                getPlotAreaMarkerId(plot) /* id */,
                plot.getRegionName() /* label */,
                false /* markup */,
                plot.getLocation().getWorld().getName() /* world */,
                corner_x,
                corner_y,
                true /* persistent */);

        if(areaMarker != null) {
            areaMarker.setFillStyle(
                    0.2 /* Opacity */,
                    plugin.getPluginConfig().getDynmap().getPlotMarkerRGBFillColor() /* Color in RGB */);
            areaMarker.setLineStyle(
                    plugin.getPluginConfig().getDynmap().getPlotMarkerLineWeight() /* Line weight */,
                    1.0 /* Opacity */,
                    plugin.getPluginConfig().getDynmap().getPlotMarkerRGBLineColor() /* Color in RGB */);
        }
    }

    public void removePlotAreaMarker(Plot plot) {

        API api = getApi();
        if (api == null) {
            return;
        }

        AreaMarker marker = api.getPlotMarkerSet().findAreaMarker(getPlotAreaMarkerId(plot));
        if(marker != null) {
            marker.deleteMarker();
        }
    }
}
