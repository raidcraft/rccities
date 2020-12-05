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
    private String cityUpgradeHolder = "cit-upgrade-holder";
    private double joinCosts = 100.0;

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
