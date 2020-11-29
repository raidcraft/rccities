package net.silthus.rccities.requirements;

import net.silthus.rccities.api.city.City;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

/**
 * @author Silthus
 */
public class CityMoneyRequirement implements ReasonableRequirement<City> {

    @Override
    @Information(
            value = "city.money",
            desc = "Checks the balance of the city.",
            conf = {"money: <min balance>"}
    )
    public boolean test(City city, ConfigurationSection config) {

        return RaidCraft.getEconomy().hasEnough(AccountType.CITY, city.getBankAccountName(), config.getDouble("money"));
    }

    @Override
    public Optional<String> getDescription(City entity, ConfigurationSection config) {

        return Optional.of(RaidCraft.getEconomy().getFormattedAmount(config.getDouble("money")));
    }

    @Override
    public String getReason(City entity, ConfigurationSection config) {

        return "Es ist zu wenig Geld in der Stadtkasse. Benötigt werden " + RaidCraft.getEconomy().getFormattedAmount(config.getDouble("money")) + "!";
    }
}
