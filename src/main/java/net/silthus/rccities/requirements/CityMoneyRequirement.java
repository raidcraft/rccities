package net.silthus.rccities.requirements;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.requirement.AbstractRequirement;
import net.silthus.rccities.upgrades.api.requirement.Requirement;
import net.silthus.rccities.upgrades.api.requirement.RequirementInformation;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author Silthus
 */
@RequirementInformation(
        value = "city.money",
        desc = "Checks the balance of the city."
)
public class CityMoneyRequirement extends AbstractRequirement<City> {

    private Economy economy;

    protected CityMoneyRequirement(ConfigurationSection config) {
        super(config);
        Economy economy = RCCitiesPlugin.getPlugin().getEconomy();
    }

    public boolean test(City city, ConfigurationSection config) {

        return economy.bankHas(city.getBankAccountName(), config.getDouble("money")).type == EconomyResponse.ResponseType.SUCCESS;
    }

    @Override
    public String getDescription(City entity, ConfigurationSection config) {

        return "Die Stadt '" + entity.getName() + "' muss mindestens " + economy.format(config.getDouble("money")) + " Geld besitzen";
    }

    @Override
    public String getReason(City entity, ConfigurationSection config) {

        return "Es ist zu wenig Geld in der Stadtkasse. Benötigt werden " + economy.format(config.getDouble("money")) + "!";
    }

    @Override
    public void load(ConfigurationSection data) {

    }
}
