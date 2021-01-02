package net.silthus.rccities.rewards;

import net.silthus.rccities.RCCities;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.reward.AbstractReward;
import net.silthus.rccities.upgrades.api.reward.RewardInformation;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
@RewardInformation("CITY_FLAG")
public class CityFlagReward extends AbstractReward<City> {

    String flagName;
    String flagValue;

    public CityFlagReward(ConfigurationSection config) {

        super(config);
    }

    @Override
    public void load(ConfigurationSection config) {

        flagName = config.getString("flag-name");
        flagValue = config.getString("flag-value");
    }

    @Override
    public void reward(City city) {

        try {
            RCCities.instance().getFlagManager().setCityFlag(city, null, flagName, flagValue);
        } catch (RaidCraftException e) {
        }
    }
}