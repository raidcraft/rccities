package net.silthus.rccities.upgrades;

import io.ebean.Database;
import lombok.Getter;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.upgrades.tables.*;

/**
 * @author Philip Urban
 */
@Getter
public class RCUpgrades {

    private final UpgradeManager upgradeManager;
    private final RCCitiesPlugin plugin;

    public RCUpgrades(RCCitiesPlugin plugin) {
        this.plugin = plugin;
        upgradeManager = new UpgradeManager();
    }
}
