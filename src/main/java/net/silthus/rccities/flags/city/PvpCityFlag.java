package net.silthus.rccities.flags.city;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.flags.PlotFlag;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.flags.plot.PvpPlotFlag;


/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "PVP",
        friendlyName = "PvP Kampf innerhalb der Stadt (An/Aus)",
        type = FlagType.BOOLEAN
)
public class PvpCityFlag extends AbstractBooleanPlotwiseCityFlag {

    public PvpCityFlag(City city) {

        super(city);
    }

    @Override
    public void announce(boolean state) {

        if (state) {
            RCCitiesPlugin.instance().getResidentManager().broadcastCityMessage(getCity(), "PvP ist im Stadtgebiet erlaubt!");
        } else {
            RCCitiesPlugin.instance().getResidentManager().broadcastCityMessage(getCity(), "PvP ist im Stadtgebiet verboten!");
        }
    }

    @Override
    public void allow(Plot plot) {

        // check if plot has its own pvp setting -> skip
        PlotFlag existingFlag = RCCitiesPlugin.instance().getFlagManager().getPlotFlag(plot, PvpPlotFlag.class);
        if (existingFlag != null && !existingFlag.getType().convertToBoolean(existingFlag.getValue())) return;

        plot.getRegion().setFlag(Flags.PVP, StateFlag.State.ALLOW);
    }

    @Override
    public void deny(Plot plot) {

        // check if plot has its own pvp setting -> skip
        PlotFlag existingFlag = RCCitiesPlugin.instance().getFlagManager().getPlotFlag(plot, PvpPlotFlag.class);
        if (existingFlag != null && existingFlag.getType().convertToBoolean(existingFlag.getValue())) return;

        plot.getRegion().setFlag(Flags.PVP, StateFlag.State.DENY);
    }
}
