package net.silthus.rccities.upgrades;

import io.ebean.Database;
import lombok.Getter;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.upgrades.tables.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
@Getter
public class RCUpgrades {

    private final UpgradeManager upgradeManager;
    private final RCCitiesPlugin plugin;
    private Database database;

    public RCUpgrades(RCCitiesPlugin plugin) {
        this.plugin = plugin;
        upgradeManager = new UpgradeManager();

        setupDatabase();
    }

    private void setupDatabase() {
        this.database = new EbeanWrapper(Config.builder(plugin)
                .entities(
                        TUpgrade.class,
                        TUpgradeLevel.class,
                        TUpgradeHolder.class,
                        TUpgradeInfo.class,
                        TLevelInfo.class
                )
                .build()).connect();
    }
}
