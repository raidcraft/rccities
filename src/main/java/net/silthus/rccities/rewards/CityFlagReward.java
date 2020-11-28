package net.silthus.rccities.rewards;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.reward.AbstractReward;
import de.raidcraft.api.reward.RewardInformation;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
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