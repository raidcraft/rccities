package net.silthus.rccities.flags.plot;

import com.sk89q.worldguard.domains.DefaultDomain;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.flags.AbstractPlotFlag;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.util.RaidCraftException;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "FARM",
        friendlyName = "Farm-Plot (An/Aus)",
        type = FlagType.BOOLEAN,
        cooldown = 60
)
public class FarmPlotFlag extends AbstractPlotFlag {

    public FarmPlotFlag(Plot plot) {

        super(plot);
    }

    @Override
    public void refresh() {

        if (getPlot() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());

        if (currentValue) {
            DefaultDomain defaultDomain = new DefaultDomain();
            for (Resident resident : RCCitiesPlugin.instance().getResidentManager().getResidents(getPlot().getCity())) {
                defaultDomain.addPlayer(resident.getName());
            }
            getPlot().getRegion().setMembers(defaultDomain);
        } else {
            getPlot().getRegion().setMembers(new DefaultDomain());
        }
    }
}
