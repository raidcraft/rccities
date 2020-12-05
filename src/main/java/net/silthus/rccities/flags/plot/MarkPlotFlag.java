package net.silthus.rccities.flags.plot;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.flags.AbstractPlotFlag;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.flags.FlagType;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
        double markCost = RCCitiesPlugin.getPlugin().getPluginConfig().getFlagPlotMarkCost();

        if (currentValue) {

            Economy economy = RCCitiesPlugin.getPlugin().getEconomy();
            if (!getPlot().getCity().hasMoney(markCost)) {
                throw new RaidCraftException("Es ist nicht genug Geld in der Stadtkasse!");
            }

            // withdraw
            getPlot().getCity().withdrawMoney(markCost);
            RCCitiesPlugin.getPlugin().getResidentManager()
                    .broadcastCityMessage(getPlot().getCity(), "Plot Markierung: "
                            + economy.format(markCost) + ChatColor.GOLD + " abgezogen!");
        }

        super.refresh();
    }
}
