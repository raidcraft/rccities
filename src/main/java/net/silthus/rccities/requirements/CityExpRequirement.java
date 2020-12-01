package net.silthus.rccities.requirements;

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
        value = "city.exp",
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

        return "Die Stadt '" + entity.getName() + "' muss mindestens " + config.getInt("exp") + " EXP besitzen";
    }

    public String getReason(City entity) {

        return "Es müssen sich mindestens " + config.getInt("exp") + " EXP in der Stadtkasse befinden!";
    }
}
