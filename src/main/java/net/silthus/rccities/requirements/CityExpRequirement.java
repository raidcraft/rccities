package net.silthus.rccities.requirements;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.requirement.AbstractRequirement;
import net.silthus.rccities.upgrades.api.requirement.RequirementInformation;

/**
 * @author Silthus
 */
@RequirementInformation(
        value = "CITY_EXP",
        desc = "Checks the exp of the city."
)
public class CityExpRequirement extends AbstractRequirement<City> {

    protected CityExpRequirement() {
        super();
    }

    public boolean test(City city) {

        return city.getExp() >= config.getInt("exp");
    }


    public String getDescription(City entity) {

        return "Die Stadt '" + entity.getFriendlyName() + "' muss mindestens " + config.getInt("exp") + " EXP besitzen";
    }

    public String getReason(City entity) {

        return "Es müssen sich mindestens " + config.getInt("exp") + " EXP in der Stadtkasse befinden!";
    }
}
