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

    private final RCCitiesPlugin plugin;

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

        commandManager.getCommandContexts().registerIssuerAwareContext(CommandFlag.class, c -> {

            String flagName = c.getFirstArg();

            if(Strings.isNullOrEmpty(flagName) || !flagName.startsWith("-")) {
                return CommandFlag.EMPTY_FLAG;
            }

            c.popFirstArg();
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
            String cityName = c.getFirstArg(); // Do not pop argument here

            City city = null;

            // At first try to get city by name
            //---------------------------------
            if(!Strings.isNullOrEmpty(cityName)) {
                city = plugin.getCityManager().getCity(cityName);
                if(city != null) {
                    c.popFirstArg(); // Pop argument from stack if city was found
                }
            }

            // If city still not set, try to get city
            // of player if there is only one citizenship
            //-------------------------------------------
            if(city == null) {
                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(c.getPlayer().getUniqueId());
                if (citizenships != null && 1 == citizenships.size()) {
                    city = citizenships.get(0).getCity();
                }
            }

            // If still no city found, try to get city
            // at current location
            //----------------------------------------
            if(city == null) {
                Plot plot = plugin.getPlotManager().getPlot(c.getPlayer().getLocation().getChunk());
                if(plot != null) {
                    city = plot.getCity();
                }
            }

            // Throw exception if no city was found
            //-------------------------------------
            if(city == null) {
                throw new InvalidCommandArgument("Bitte gebe eine Stadt an!");
            }

            return city;
        });
    }

    private void registerPlotContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerIssuerAwareContext(Plot.class, c -> {
            String plotRegionName = c.getFirstArg(); // Do not pop argument here

            Plot plot = null;

            // At first try to get plot by name
            //---------------------------------
            if(!Strings.isNullOrEmpty(plotRegionName)) {
                plot = plugin.getPlotManager().getPlot(plotRegionName);
                if(plot != null) {
                    c.popFirstArg(); // Pop argument from stack if plot was found
                }
            }

            // If no plot was found, try to get
            // plot at current location
            //---------------------------------
            if(plot == null) {
                plot = plugin.getPlotManager().getPlot(c.getPlayer().getLocation().getChunk());
            }

            // Throw exception if no plot was found
            //-------------------------------------
            if(plot == null) {
                throw new InvalidCommandArgument("Bitte gebe einen Plot an!");
            }

            return plot;
        });
    }
}
