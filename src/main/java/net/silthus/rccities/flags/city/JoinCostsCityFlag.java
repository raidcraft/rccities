package net.silthus.rccities.flags.city;

import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.AbstractCityFlag;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.flags.FlagType;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "JOIN_COSTS",
        friendlyName = "Beitrittskosten",
        type = FlagType.MONEY,
        cooldown = 0
)
public class JoinCostsCityFlag extends AbstractCityFlag {

    public JoinCostsCityFlag(City city) {

        super(city);
    }

    @Override
    public void refresh() throws RaidCraftException {

        // dummy flag -> do nothing
    }
}
