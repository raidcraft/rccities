package net.silthus.rccities.flags.plot;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.silthus.rccities.api.flags.AbstractPlotFlag;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.util.RaidCraftException;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "TNT",
        friendlyName = "TNT",
        type = FlagType.BOOLEAN,
        cooldown = 60
)
public class TntPlotFlag extends AbstractPlotFlag {

    public TntPlotFlag(Plot plot) {

        super(plot);
    }

    @Override
    public void refresh() throws RaidCraftException {

        if (getPlot() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());

        if (currentValue) {
            getPlot().getRegion().setFlag(Flags.TNT, StateFlag.State.ALLOW);
        } else {
            getPlot().getRegion().setFlag(Flags.TNT, StateFlag.State.DENY);
        }
    }
}
