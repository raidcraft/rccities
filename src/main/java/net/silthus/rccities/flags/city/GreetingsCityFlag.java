package net.silthus.rccities.flags.city;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.flags.FlagType;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.resident.Resident;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "GREETINGS",
        friendlyName = "Plot-Nachrichten (An/Aus)",
        type = FlagType.BOOLEAN
)
public class GreetingsCityFlag extends AbstractBooleanPlotwiseCityFlag {

    public GreetingsCityFlag(City city) {

        super(city);
    }

    @Override
    public void announce(boolean state) {

    }

    @Override
    public void allow(Plot plot) {

        String residentList = "";
        for (Resident resident : plot.getAssignedResidents()) {
            if (!residentList.isEmpty()) residentList += ChatColor.GRAY + ", ";
            residentList += ChatColor.GREEN + resident.getName();
        }
        if (residentList.isEmpty()) {
            residentList = "No owners";
        }
        plot.getRegion().setFlag(DefaultFlag.GREET_MESSAGE, ChatColor.GREEN + "~ " + plot.getRegionName() + ": " + residentList + " ~");
    }

    @Override
    public void deny(Plot plot) {

        plot.getRegion().setFlag(DefaultFlag.GREET_MESSAGE, null);
    }
}
