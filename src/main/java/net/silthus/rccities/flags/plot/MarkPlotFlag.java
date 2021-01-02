package net.silthus.rccities.flags.plot;

import de.raidcraft.economy.wrapper.Economy;
import net.silthus.rccities.RCCities;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@FlagInformation(
        name = "MARK",
        friendlyName = "Fackelmarkierung",
        type = FlagType.BOOLEAN,
        cooldown = 5,
        needsRefresh = false
)
public class MarkPlotFlag extends MarkPlotBaseFlag {

    public MarkPlotFlag(Plot plot) {

        super(plot);
    }

    @Override
    public void refresh() throws RaidCraftException {

        if (getPlot() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());
        double markCost = RCCities.instance().getPluginConfig().getFlagPlotMarkCost();

        if (currentValue) {

            if (!getPlot().getCity().hasMoney(markCost)) {
                throw new RaidCraftException("Es ist nicht genug Geld in der Stadtkasse!");
            }

            // withdraw
            getPlot().getCity().withdrawMoney(markCost);
            RCCities.instance().getResidentManager()
                    .broadcastCityMessage(getPlot().getCity(), "Plot Markierung: "
                            + Economy.get().format(markCost) + ChatColor.GOLD + " abgezogen!");
        }

        super.refresh();
    }
}
