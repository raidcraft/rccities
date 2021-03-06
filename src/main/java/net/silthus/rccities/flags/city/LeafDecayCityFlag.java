package net.silthus.rccities.flags.city;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "LEAF_DECAY",
        friendlyName = "Laub-Zerfall (An/Aus)",
        type = FlagType.BOOLEAN
)
public class LeafDecayCityFlag extends AbstractBooleanPlotwiseCityFlag {

    public LeafDecayCityFlag(City city) {

        super(city);
    }

    @Override
    public void announce(boolean state) {

    }

    @Override
    public void allow(Plot plot) {

        plot.getRegion().setFlag(Flags.LEAF_DECAY, StateFlag.State.ALLOW);
    }

    @Override
    public void deny(Plot plot) {

        plot.getRegion().setFlag(Flags.LEAF_DECAY, StateFlag.State.DENY);
    }
}
