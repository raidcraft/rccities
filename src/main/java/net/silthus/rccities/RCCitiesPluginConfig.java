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
    private List<String> ignoredRegions = new ArrayList<>();
    private int defaultTownRadius = 64;
    private int initialPlotCredit = 3;
    private double flagPlotMarkCost = 20.0;
    private double joinCosts = 100.0;
    @Comment("This is the base cost of a new plot")
    private double newPlotCost = 500.0;
    @Comment("For each existing plot of a city the new plot is more expensive")
    private double newPlotCostPerOldPlot = 25.0;
    @Comment("Time to wait in seconds before player will be teleported to city spawn")
    private double spawnTeleportWarmup = 5;
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

        private String username = "sa";
        private String password = "sa";
        private String driver = "h2";
        private String url = "jdbc:h2:~/skills.db";
    }
}
