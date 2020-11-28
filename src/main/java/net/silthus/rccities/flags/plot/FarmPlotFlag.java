package net.silthus.rccities.flags.plot;

import com.sk89q.worldguard.domains.DefaultDomain;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.flags.AbstractPlotFlag;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.flags.FlagType;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.resident.Resident;

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
            for (Resident resident : RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().getResidents(getPlot().getCity())) {
                defaultDomain.addPlayer(resident.getName());
            }
            getPlot().getRegion().setMembers(defaultDomain);
        } else {
            getPlot().getRegion().setMembers(new DefaultDomain());
        }
    }
}
