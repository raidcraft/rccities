package net.silthus.rccities.requirements;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.requirement.AbstractRequirement;
import net.silthus.rccities.upgrades.api.requirement.RequirementInformation;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

@RequirementInformation(
        value = "city.unlocked",
        desc = "Other upgrade must be unlocked."
)
public class CityUpgradeLevelRequirement extends AbstractRequirement<City> {

    protected CityUpgradeLevelRequirement() {
        super();
    }

    public boolean test(City city) {

        if(city == null) return false;
        Upgrade upgrade = city.getUpgrades().getUpgrade(config.getString("upgrade-id"));
        if (upgrade == null) return false;
        UpgradeLevel upgradeLevel = upgrade.getLevel(config.getString("upgrade-level-id"));
        if (upgradeLevel == null) return false;

        if (upgradeLevel.isUnlocked()) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription(City city) {

        return "Das Upgrade-Level '" + config.getString("upgrade-level-id") + "' muss freigeschaltet sein";
    }

    @Override
    public String getReason(City city) {

        return "Es muss das Upgrade-Level '" + config.getString("upgrade-level-id") + "' freigeschaltet sein!";
    }
}
