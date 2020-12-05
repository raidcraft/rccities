package net.silthus.rccities.commands;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Strings;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.stream.Collectors;

public class CommandSetup {

    private RCCitiesPlugin plugin;

    public CommandSetup(RCCitiesPlugin plugin) {
        this.plugin = plugin;
    }

    public void setupCommands() {

        plugin.setCommandManager(new PaperCommandManager(plugin));

        registerPlotContext(plugin.getCommandManager());
        registerOfflinePlayerContext(plugin.getCommandManager());
        registerCityContext(plugin.getCommandManager());
        registerFlagContext(plugin.getCommandManager());

        registerCityCompletion(plugin.getCommandManager());

        plugin.getCommandManager().registerCommand(new PlotCommands(plugin));
        plugin.getCommandManager().registerCommand(new ResidentCommands(plugin));
        plugin.getCommandManager().registerCommand(new TownCommands(plugin));
    }

    private void registerCityCompletion(PaperCommandManager commandManager) {

        commandManager.getCommandCompletions().registerAsyncCompletion("cities", context ->
                plugin.getCityManager().getCities().stream().map(City::getName).collect(Collectors.toSet()));
    }

    private void registerFlagContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerContext(CommandFlag.class, c -> {

            String flagName = c.popFirstArg();

            if(Strings.isNullOrEmpty(flagName) || !flagName.startsWith("-")) {
                return CommandFlag.EMPTY_FLAG;
            }

            return new CommandFlag(flagName);
        });
    }

    private void registerOfflinePlayerContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerIssuerAwareContext(OfflinePlayer.class, c -> {
            String playerName = c.popFirstArg();

            if(Strings.isNullOrEmpty(playerName)) {
                return c.getPlayer();
            } else {
                return Bukkit.getOfflinePlayer(playerName);
            }
        });
    }

    private void registerCityContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerIssuerAwareContext(City.class, c -> {
            String cityName = c.popFirstArg();

            City city;
            if(Strings.isNullOrEmpty(cityName)) {
                Plot plot = plugin.getPlotManager().getPlot(c.getPlayer().getLocation().getChunk());
                if (plot == null) {

                    List<Resident> citizenships = plugin.getResidentManager().getCitizenships(c.getPlayer().getUniqueId());
                    if (1 != citizenships.size()) {
                        throw new InvalidCommandArgument(
                                "Hier befindet sich keine Stadt oder du bist Einwohner in mehr als einer Stadt!");
                    }
                    city = citizenships.get(0).getCity();
                } else {
                    city = plot.getCity();
                }
                if (city == null) {
                    throw new InvalidCommandArgument(
                            "Es ist ein Fehler aufgetreten. Gebe den Stadtnamen direkt an!");
                }
            } else {
                city = plugin.getCityManager().getCity(cityName);
                if (city == null) {
                    throw new InvalidCommandArgument("Es gibt keine Stadt mit diesem Namen!");
                }
            }

            return city;
        });
    }

    private void registerPlotContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerIssuerAwareContext(Plot.class, c -> {
            String plotRegionName = c.popFirstArg();

            Plot plot;
            if(Strings.isNullOrEmpty(plotRegionName)) {
                plot = plugin.getPlotManager().getPlot(c.getPlayer().getLocation().getChunk());
                if (plot == null) {
                    throw new ConditionFailedException("Hier befindet sich kein Plot!");
                }
            } else {
                try {
                    plot = plugin.getPlotManager().getPlot(plotRegionName);
                } catch (IllegalArgumentException e) {
                    plot = null;
                }
                if (plot == null) {
                    throw new InvalidCommandArgument("Es gibt kein Plot mit diesem Namen!");
                }
            }

            return plot;
        });
    }
}
