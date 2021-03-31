package net.silthus.rccities.upgrades;

import lombok.Getter;
import net.silthus.rccities.RCCitiesPlugin;

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
