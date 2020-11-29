package net.silthus.rccities.flags.city.admin;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.AbstractCityFlag;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.util.RaidCraftException;

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
