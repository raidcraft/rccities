package net.silthus.rccities.flags.city;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.flags.FlagType;
import de.raidcraft.rccities.api.flags.PlotFlag;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.flags.plot.PvpPlotFlag;

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
            RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().broadcastCityMessage(getCity(), "PvP ist im Stadtgebiet erlaubt!");
        } else {
            RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().broadcastCityMessage(getCity(), "PvP ist im Stadtgebiet verboten!");
        }
    }

    @Override
    public void allow(Plot plot) {

        // check if plot has its own pvp setting -> skip
        PlotFlag existingFlag = RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().getPlotFlag(plot, PvpPlotFlag.class);
        if (existingFlag != null && !existingFlag.getType().convertToBoolean(existingFlag.getValue())) return;

        plot.getRegion().setFlag(DefaultFlag.PVP, StateFlag.State.ALLOW);
    }

    @Override
    public void deny(Plot plot) {

        // check if plot has its own pvp setting -> skip
        PlotFlag existingFlag = RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().getPlotFlag(plot, PvpPlotFlag.class);
        if (existingFlag != null && existingFlag.getType().convertToBoolean(existingFlag.getValue())) return;

        plot.getRegion().setFlag(DefaultFlag.PVP, StateFlag.State.DENY);
    }
}
