package net.silthus.rccities.rewards;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.reward.AbstractReward;
import net.silthus.rccities.upgrades.api.reward.RewardInformation;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
@RewardInformation("CITY_SUBTRACT_MONEY")
public class SubtractMoneyReward extends AbstractReward<City> {

    double amount;

    public SubtractMoneyReward(ConfigurationSection config) {

        super(config);
    }

    @Override
    public void load(ConfigurationSection config) {

        amount = config.getDouble("money");
    }

    @Override
    public void reward(City city) {

        city.withdrawMoney(amount);
    }
}