package net.silthus.rccities.requirements;

import net.silthus.rccities.api.city.City;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author Silthus
 */
public class CityExpRequirement implements ReasonableRequirement<City> {

    @Override
    @Information(
            value = "city.exp",
            desc = "Checks the exp of the city.",
            conf = {"exp: <min exp>"}
    )
    public boolean test(City city, ConfigurationSection config) {

        return city.getExp() >= config.getInt("exp");
    }

    @Override
    public Optional<String> getDescription(City entity, ConfigurationSection config) {

        return Optional.of(config.getInt("exp") + " EXP");
    }

    @Override
    public String getReason(City entity, ConfigurationSection config) {

        return "Es müssen sich mindestens " + config.getInt("exp") + " EXP in der Stadtkasse befinden!";
    }
}
