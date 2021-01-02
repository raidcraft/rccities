package net.silthus.rccities;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.annotation.ElementType;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RCCitiesPluginConfig extends BukkitYamlConfiguration {

    private DatabaseConfig database = new DatabaseConfig();
    private DynmapConfig dynmap = new DynmapConfig();
    private List<String> ignoredRegions = new ArrayList<>();
    @Comment("Enable it to automatically migrate old plot regions into new city plots")
    private boolean migrateOldPlots = true;
    @Comment("Enable support of external economy plugin instead of own balance tracking")
    private boolean useExternalEconomyPlugin = false;
    private int initialPlotCredit = 3;
    private double flagPlotMarkCost = 20.0;
    private double joinCosts = 100.0;
    @Comment("Number of additional plot lengths for max radius calculation")
    private int additionalRadiusPlots = 10;
    @Comment("This is the base cost of a new plot")
    private double newPlotCost = 250.0;
    @Comment("For each existing plot of a city the new plot is more expensive")
    private double newPlotCostPerOldPlot = 10.0;
    @Comment("Time to wait in seconds before player will be teleported to city spawn")
    private double spawnTeleportWarmup = 3;
    @Comment("Timeout in seconds between two teleport requests")
    private double spawnTeleportCooldown = 60;
    @Comment("Timeout for foreign town teleport (86400s = 24h)")
    private double foreignSpawnTeleportCooldown = 86400;
    @Comment("Custom message if foreign spawn cooldown is active")
    private String foreignSpawnTeleportCooldownMessage =
            "Du kannst dich nur einmal am Tag in eine fremde Stadt teleportieren!";

    @Comment("Cooldown between a city can make requests to process their upgrade to a higher level")
    private int upgradeRequestCooldown = 5 * 24 * 60;// 5 days in minutes

    public RCCitiesPluginConfig(Path path) {

        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }

    @ConfigurationElement
    @Getter
    @Setter
    public static class DatabaseConfig {

        private String username = "sa";
        private String password = "sa";
        private String driver = "h2";
        private String url = "jdbc:h2:~/rccities.db";
    }

    @ConfigurationElement
    @Getter
    @Setter
    public static class DynmapConfig {
        private String cityMarkerSetLabel = "Cities";
        private String plotMarkerSetLabel = "Cities - Plots";
        private int plotMarkerRGBFillColor = 0x73a9ff;
        private int plotMarkerRGBLineColor = 0x454eff;
        private int plotMarkerLineWeight = 1;
        private String defaultPlacesIcon = "pin";

        @ElementType(PlacesConfig.class)
        @Comment("Places marker configuration")
        private Map<String, PlacesConfig> places = initPlacesConfig();

        private Map<String, PlacesConfig> initPlacesConfig() {
            Map<String, PlacesConfig> places = new HashMap<>();
            List<String> aliases = new ArrayList<>();
            aliases.add("town_hall");
            aliases.add("rathaus");
            places.put("town_hall", new PlacesConfig(aliases, "temple"));

            return places;
        }

        public String getPlaceIcon(String placeName) {

            for(Map.Entry<String, PlacesConfig> entry : places.entrySet()) {

                for(String alias : entry.getValue().getAliases()) {
                    if(alias.equalsIgnoreCase(placeName)) {
                        return entry.getValue().getIcon();
                    }

                    // Check wildcard
                    if(alias.endsWith("*") && placeName.startsWith(alias.substring(0, alias.length() - 2))) {
                        return entry.getValue().getIcon();
                    }
                }
            }

            return defaultPlacesIcon;
        }

        @ConfigurationElement
        @Getter
        @Setter
        public static class PlacesConfig {

            private List<String> aliases;
            private String icon;

            public PlacesConfig(List<String> aliases, String icon) {
                this.aliases = aliases;
                this.icon = icon;
            }
        }
    }
}
