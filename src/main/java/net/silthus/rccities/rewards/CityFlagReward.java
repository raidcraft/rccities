package net.silthus.rccities.rewards;

import net.silthus.rccities.api.city.City;
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
            RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().setCityFlag(city, null, flagName, flagValue);
        } catch (RaidCraftException e) {
        }
    }
}