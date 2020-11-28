package net.silthus.rccities.upgrades;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.rcupgrades.tables.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class RCUpgradesPlugin extends BasePlugin {

    private UpgradeManager upgradeManager;

    @Override
    public void enable() {

        upgradeManager = new UpgradeManager();
    }

    @Override
    public void disable() {
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> databases = new ArrayList<>();
        databases.add(TUpgrade.class);
        databases.add(TUpgradeLevel.class);
        databases.add(TUpgradeHolder.class);
        databases.add(TUpgradeInfo.class);
        databases.add(TLevelInfo.class);
        return databases;
    }

    public UpgradeManager getUpgradeManager() {

        return upgradeManager;
    }
}
