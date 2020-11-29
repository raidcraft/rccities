package net.silthus.rccities.flags.plot;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
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
    public void refresh() throws RaidCraftException {

        if (getPlot() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());

        if (currentValue) {
            DefaultDomain defaultDomain = new DefaultDomain();
            for (Resident resident : RCCitiesPlugin.getPlugin().getResidentManager().getResidents(getPlot().getCity())) {
                defaultDomain.addPlayer(resident.getName());
            }
            getPlot().getRegion().setMembers(defaultDomain);
        } else {
            getPlot().getRegion().setMembers(new DefaultDomain());
        }
    }
}
