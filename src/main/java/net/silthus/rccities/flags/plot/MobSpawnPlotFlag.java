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
        name = "MOB_SPAWNING",
        friendlyName = "Mob-Spawning (An/Aus)",
        type = FlagType.BOOLEAN,
        cooldown = 60
)
public class MobSpawnPlotFlag extends AbstractPlotFlag {

    public MobSpawnPlotFlag(Plot plot) {

        super(plot);
    }

    @Override
    public void refresh() throws RaidCraftException {

        if (getPlot() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());

        if (currentValue) {
            getPlot().getRegion().setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
        } else {
            getPlot().getRegion().setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
        }
    }
}
