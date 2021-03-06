package net.silthus.rccities.requirements;

import net.milkbowl.vault.economy.Economy;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.requirement.AbstractRequirement;
import net.silthus.rccities.upgrades.api.requirement.RequirementInformation;

/**
 * @author Silthus
 */
@RequirementInformation(
        value = "CITY_MONEY",
        desc = "Checks the balance of the city."
)
public class CityMoneyRequirement extends AbstractRequirement<City> {

    private Economy economy;

    protected CityMoneyRequirement() {
        super();
    }

    public boolean test(City city) {

        return city.hasMoney(config.getDouble("money"));
    }

    @Override
    public String getDescription(City entity) {

        return "Die Stadt '" + entity.getFriendlyName() + "' muss mindestens " + economy.format(config.getDouble("money")) + " Geld besitzen";
    }

    @Override
    public String getReason(City entity) {

        return "Es ist zu wenig Geld in der Stadtkasse. Benötigt werden " + economy.format(config.getDouble("money")) + "!";
    }
}
