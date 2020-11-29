package net.silthus.rccities.flags.city;

import com.sk89q.worldguard.protection.flags.Flags;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
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
        plot.getRegion().setFlag(Flags.GREET_MESSAGE, ChatColor.GREEN + "~ " + plot.getRegionName() + ": " + residentList + " ~");
    }

    @Override
    public void deny(Plot plot) {

        plot.getRegion().setFlag(Flags.GREET_MESSAGE, null);
    }
}
