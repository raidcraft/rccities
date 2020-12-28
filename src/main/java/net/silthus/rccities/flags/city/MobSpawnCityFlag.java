package net.silthus.rccities.flags.city;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "MOB_SPAWNING",
        friendlyName = "Mob-Spawning (An/Aus)",
        type = FlagType.BOOLEAN
)
public class MobSpawnCityFlag extends AbstractBooleanPlotwiseCityFlag {

    public MobSpawnCityFlag(City city) {

        super(city);
    }

    @Override
    public void announce(boolean state) {

        if (state) {
            RCCitiesPlugin.instance().getResidentManager().broadcastCityMessage(getCity(), "Es spawnen nun Mobs im Stadtgebiet!");
        } else {
            RCCitiesPlugin.instance().getResidentManager().broadcastCityMessage(getCity(), "Es spawnen nicht länger Mobs im Stadtgebiet!");
        }
    }

    @Override
    public void allow(Plot plot) {

        plot.getRegion().setFlag(Flags.MOB_SPAWNING, StateFlag.State.ALLOW);
    }

    @Override
    public void deny(Plot plot) {

        plot.getRegion().setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
    }
}
