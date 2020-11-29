package net.silthus.rccities.requirements;

import net.silthus.rccities.api.city.City;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author Silthus
 */
public class CityUpgradeLevelRequirement implements ReasonableRequirement<City> {

    @Override
    @Information(
            value = "city.level",
            desc = "Checks if city has level unlocked.",
            conf = {"upgrade-id: <ID of parent update>",
                    "upgrade-level-id: <ID of affecting update level>"}
    )
    public boolean test(City city, ConfigurationSection config) {

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
    public Optional<String> getDescription(City city, ConfigurationSection config) {

        return Optional.of("Level '" + config.getString("upgrade-level-id") + "' freigeschaltet");
    }

    @Override
    public String getReason(City city, ConfigurationSection config) {

        return "Es muss das Upgrade-Level '" + config.getString("upgrade-level-id") + "' freigeschaltet sein!";
    }
}
