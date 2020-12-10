package net.silthus.rccities;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RCCitiesPluginConfig extends BukkitYamlConfiguration {

    private DatabaseConfig database = new DatabaseConfig();
    private DynmapConfig dynmap = new DynmapConfig();
    private List<String> ignoredRegions = new ArrayList<>();
    @Comment("Enable it to automatically migrate old plot regions into new city plots")
    private boolean migrateOldPlots = true;
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

    @Comment("Cooldown between a city can make requests to process their upgrade to a higher level")
    private int upgradeRequestCooldown = 5 * 24 * 60;// 5 days in minutes

    public RCCitiesPluginConfig(Path path) {

        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }

    @ConfigurationElement
    @Getter
    @Setter
    public static class DatabaseConfig {

        private String username = "minecraft";
        private String password = "password";
        private String driver = "mariadb";
        private String url = "jdbc:mariadb://db:3306/minecraft";
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
    }
}
