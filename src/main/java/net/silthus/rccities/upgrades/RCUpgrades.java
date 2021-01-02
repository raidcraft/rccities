package net.silthus.rccities.upgrades;

import lombok.Getter;
import net.silthus.rccities.RCCities;

/**
 * @author Philip Urban
 */
@Getter
public class RCUpgrades {

    private final UpgradeManager upgradeManager;
    private final RCCities plugin;

    public RCUpgrades(RCCities plugin) {
        this.plugin = plugin;
        upgradeManager = new UpgradeManager();
    }
}
