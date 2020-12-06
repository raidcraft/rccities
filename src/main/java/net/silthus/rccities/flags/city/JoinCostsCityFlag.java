package net.silthus.rccities.flags.city;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.AbstractCityFlag;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.util.RaidCraftException;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "JOIN_COSTS",
        friendlyName = "Beitrittskosten",
        type = FlagType.DOUBLE,
        cooldown = 0
)
public class JoinCostsCityFlag extends AbstractCityFlag {

    public JoinCostsCityFlag(City city) {

        super(city);
    }

    public double getAmount() {
        return Double.parseDouble(getValue());
    }

    @Override
    public void refresh() throws RaidCraftException {

        // dummy flag -> do nothing
    }
}
