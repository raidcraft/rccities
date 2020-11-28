package net.silthus.rccities.flags.city;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.flags.FlagType;
import de.raidcraft.rccities.api.plot.Plot;

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
            RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().broadcastCityMessage(getCity(), "Es spawnen nun Mobs im Stadtgebiet!");
        } else {
            RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().broadcastCityMessage(getCity(), "Es spawnen nicht länger Mobs im Stadtgebiet!");
        }
    }

    @Override
    public void allow(Plot plot) {

        plot.getRegion().setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.ALLOW);
    }

    @Override
    public void deny(Plot plot) {

        plot.getRegion().setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
    }
}
