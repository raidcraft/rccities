package net.silthus.rccities.rewards;

import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.reward.AbstractReward;
import net.silthus.rccities.upgrades.api.reward.RewardInformation;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
@RewardInformation("CITY_PLOTS")
public class CityPlotsReward extends AbstractReward<City> {

    int plotAmount;
    boolean broadcast;

    public CityPlotsReward(ConfigurationSection config) {

        super(config);
    }

    @Override
    public void load(ConfigurationSection config) {

        plotAmount = config.getInt("amount");
        broadcast = config.getBoolean("broadcast", true);
    }

    @Override
    public void reward(City city) {

        city.setPlotCredit(city.getPlotCredit() + plotAmount);

        if (broadcast) {
            if (plotAmount > 1) {
                RCCitiesPlugin.instance().getResidentManager().broadcastCityMessage(city, "Die Stadt hat " + plotAmount + " neue Plots erhalten!");
            } else {
                RCCitiesPlugin.instance().getResidentManager().broadcastCityMessage(city, "Die Stadt hat einen neuen Plot erhalten!");
            }
        }
    }

    @Override
    public String getDescription() {

        if (plotAmount > 1) {
            return plotAmount + " Plots";
        } else {
            return plotAmount + " Plot";
        }
    }
}