package net.silthus.rccities.flags.city.admin;

import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.AbstractCityFlag;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.flags.FlagType;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "INVITE",
        friendlyName = "Neue Einwohner",
        type = FlagType.BOOLEAN,
        cooldown = 0,
        adminOnly = true
)
public class InviteCityFlag extends AbstractCityFlag {

    public InviteCityFlag(City city) {

        super(city);
    }

    @Override
    public void refresh() throws RaidCraftException {

        // dummy flag -> do nothing
    }
}
