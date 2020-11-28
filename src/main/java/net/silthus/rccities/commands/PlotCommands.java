package net.silthus.rccities.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldguard.bukkit.util.Entities;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.commands.QueuedCaptchaCommand;
import de.raidcraft.rccities.DatabasePlot;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.RolePermission;
import de.raidcraft.rccities.manager.CityManager;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public class PlotCommands {

    private RCCitiesPlugin plugin;

    public PlotCommands(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"plot"},
            desc = "Plot commands"
    )
    @NestedCommand(value = NestedCommands.class, executeBody = true)
    public void plot(CommandContext args, CommandSender sender) throws CommandException {

        Plot plot;
        if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
        Player player = (Player) sender;
        plot = plugin.getPlotManager().getPlot(player.getLocation().getChunk());
        if (plot == null) {
            throw new CommandException("Hier befindet sich kein Plot! Nutze '/plot info <Plot ID>'");
        }

        plugin.getPlotManager().printPlotInfo(plot, sender);
    }

    public static class NestedCommands {

        private final RCCitiesPlugin plugin;
        private static int unclaimTask = 0;

        public NestedCommands(RCCitiesPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"take"},
                desc = "Take a plot from player",
                min = 1,
                usage = "<Einwohner> [Plot ID]"
        )
        @CommandPermissions("rccities.plot.take")
        public void take(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            Plot plot;
            if (args.argsLength() > 1) {
                int plotId = args.getInteger(1);
                plot = plugin.getPlotManager().getPlot(plotId);
                if (plot == null) {
                    throw new CommandException("Es gibt kein Plot mit dieser ID!");
                }
            } else {
                plot = plugin.getPlotManager().getPlot(player.getLocation().getChunk());
                if (plot == null) {
                    throw new CommandException("Hier befindet sich kein Plot zum vergeben!");
                }
            }

            City city = plot.getCity();

            // check if resident has permission
            Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
            if (resident == null || !resident.getRole().hasPermission(RolePermission.PLOT_DISTRIBUTION)) {
                throw new CommandException("Du hast in der Gilde '" + city.getFriendlyName() + "' nicht die Berechtigung Plots zu entziehen!");
            }

            Resident targetResident = plugin.getResidentManager().getResident(UUIDUtil.convertPlayer(args.getString(0)), city);
            if (targetResident == null) {
                throw new CommandException("Der angegebene Spieler ist kein Mitglied deiner Gilde '" + city.getFriendlyName() + "'!");
            }

            plot.removeResident(resident);
            plot.getCity().refreshFlags();
            plot.refreshFlags();
            player.sendMessage(ChatColor.GREEN + "Du hast den Plot '" + plot.getRegionName() + "' erfolgreich " + targetResident.getName() + " entzogen!");
            if (targetResident.getPlayer() != null) {
                targetResident.getPlayer()
                        .sendMessage(ChatColor.GREEN + "Dir wurde in der Gilde '" + city.getFriendlyName() + "' der Plot '" + plot.getRegionName() + "' entzogen!");
            }
        }

        @Command(
                aliases = {"give"},
                desc = "Distributes a plot",
                min = 1,
                usage = "<Einwohner> [Plot ID]"
        )
        @CommandPermissions("rccities.plot.give")
        public void give(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            Plot plot;
            if (args.argsLength() > 1) {
                int plotId = args.getInteger(1);
                plot = plugin.getPlotManager().getPlot(plotId);
                if (plot == null) {
                    throw new CommandException("Es gibt kein Plot mit dieser ID!");
                }
            } else {
                plot = plugin.getPlotManager().getPlot(player.getLocation().getChunk());
                if (plot == null) {
                    throw new CommandException("Hier befindet sich kein Plot zum vergeben!");
                }
            }

            City city = plot.getCity();

            // check if resident has permission
            Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
            if (resident == null || !resident.getRole().hasPermission(RolePermission.PLOT_DISTRIBUTION)) {
                throw new CommandException("Du hast in der Gilde '" + city.getFriendlyName() + "' nicht die Berechtigung Plots zu vergeben!");
            }

            Resident targetResident = plugin.getResidentManager().getResident(UUIDUtil.convertPlayer(args.getString(0)), city);
            if (targetResident == null) {
                throw new CommandException("Der angegebene Spieler ist kein Mitglied deiner Gilde '" + city.getFriendlyName() + "'!");
            }

            plot.assignResident(targetResident);
            plot.getCity().refreshFlags();
            plot.refreshFlags();
            player.sendMessage(ChatColor.GREEN + "Du hast den Plot '" + plot.getRegionName() + "' erfolgreich an " + targetResident.getName() + " vergeben!");
            if (targetResident.getPlayer() != null) {
                targetResident.getPlayer()
                        .sendMessage(ChatColor.GREEN + "Dir wurde in der Gilde '" + city.getFriendlyName() + "' der Plot '" + plot.getRegionName() + "' zugewiesen!");
            }
        }

        @Command(
                aliases = {"claim"},
                desc = "Claims a plot",
                flags = "f"
        )
        @CommandPermissions("rccities.plot.claim")
        public void claim(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            // check if here is a wrong region
            if (!plugin.getWorldGuardManager().claimable(player.getLocation())) {
                throw new CommandException("An dieser Stelle befindet sich bereits eine andere Region!");
            }

            // get neighbor plot and city
            Chunk chunk = player.getLocation().getChunk();
            Plot[] neighborPlots = new Plot[8];
            neighborPlots[0] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX(), chunk.getZ() + 1));
            neighborPlots[1] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ() + 1));
            neighborPlots[2] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ() + 1));
            neighborPlots[3] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX(), chunk.getZ() - 1));
            neighborPlots[4] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ() - 1));
            neighborPlots[5] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ() - 1));
            neighborPlots[6] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX() - 1, chunk.getZ()));
            neighborPlots[7] = plugin.getPlotManager().getPlot(player.getWorld().getChunkAt(chunk.getX() + 1, chunk.getZ()));

            City city = null;
            for (Plot plot : neighborPlots) {
                if (plot == null) continue;
                if (city != null && !city.equals(plot.getCity())) {
                    throw new CommandException("Dieser Plot liegt zu dicht an einer anderen Gilde!");
                }
                city = plot.getCity();
            }
            // admins can claim wild chunks
            if(args.hasFlag('f') && sender.hasPermission("rccities.admin")) {
                if(city == null) {
                    if(args.argsLength() == 0) {
                        throw new CommandException("Gebe eine Stadt als ersten Parameter an!");
                    }
                    city = plugin.getCityManager().getCity(args.getString(0));
                }
            } else if (city == null) {
                throw new CommandException("Neue Plots müssen an bestehende anknüpfen!");
            }

            // check if resident has permission
            Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
            if (resident == null || !resident.getRole().hasPermission(RolePermission.PLOT_CLAIM)) {
                throw new CommandException("Du hast in der Gilde '" + city.getFriendlyName() + "' nicht die Berechtigung Plots zu claimen!");
            }

            // check plot credit
            if (city.getPlotCredit() == 0) {
                throw new CommandException("Deine Gilde hat keine freien Plots zum claimen!");
            }

            // check max radius
            Location plotCenter = new Location(chunk.getWorld(), chunk.getX() * 16 + 8, 0, chunk.getZ() * 16 + 8);
            Location fixedSpawn = city.getSpawn().clone();
            fixedSpawn.setY(0);
            if (fixedSpawn.distance(plotCenter) > city.getMaxRadius()) {
                throw new CommandException("Deine Gilde darf nur im Umkreis von " + city.getMaxRadius() + " Blöcken um den Stadtmittelpunkt claimen!");
            }

            Plot plot = new DatabasePlot(plotCenter, city);

            // create schematic
            try {
                plugin.getSchematicManager().createSchematic(plot);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }

            // withdraw plot credit
            city.setPlotCredit(city.getPlotCredit() - 1);

            // reload city flags
            city.refreshFlags();

            player.sendMessage(ChatColor.GREEN + "Der Plot wurde erfolgreich geclaimt! (Restliches Guthaben: " + city.getPlotCredit() + " Plots)");
        }

        @Command(
                aliases = {"unclaim"},
                desc = "Unclaims a plot",
                flags = "rfa"
        )
        @CommandPermissions("rccities.plot.unclaim")
        public void unclaim(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            boolean restoreSchematics = false;
            boolean force = false;
            if (args.hasFlag('r')) {
                restoreSchematics = true;
            }
            if (args.hasFlag('f')) {
                force = true;
            }

            Plot plot = plugin.getPlotManager().getPlot(player.getLocation().getChunk());
            if (plot == null) {
                throw new CommandException("Hier befindet sich kein Chunk zum unclaimen!");
            }

            if (plugin.getPlotManager().getPlots(plot.getCity()).size() == 1) {
                throw new CommandException("Der letze Plot kann nicht gelöscht werden!");
            }

            try {
                if (restoreSchematics) {
                    sender.sendMessage(ChatColor.DARK_RED + "Bei der Löschung des Plots wird die Landschaft zurückgesetzt!");
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "Bei der Löschung des Plots wird die Landschaft NICHT zurückgesetzt!");
                }
                if(args.hasFlag('a')) {
                    sender.sendMessage(ChatColor.DARK_RED + "Bist du sicher dass ALLE plots wiederhergestellt werden sollen?");
                    new QueuedCaptchaCommand(sender, this, "unclaimAll", sender, plot.getCity(), restoreSchematics);

                } else {
                    if (force) {
                        unclaimPlot(sender, plot, restoreSchematics);
                    } else {
                        new QueuedCaptchaCommand(sender, this, "unclaimPlot", sender, plot, restoreSchematics);
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new CommandException(e.getMessage());
            }
        }

        @Command(
                aliases = {"flag"},
                desc = "Change plot flag",
                usage = "[Plot ID] <Flag> <Parameter>"
        )
        @CommandPermissions("rccities.plot.flag")
        public void flag(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");

            if(args.argsLength() < 2) {
                String flagList = "";
                for(FlagInformation info : plugin.getFlagManager().getRegisteredPlotFlagInformationList()) {
                    flagList += info.name() + ", ";
                }
                throw new CommandException("Verfügbare Flags: " + flagList);
            }

            Player player = (Player) sender;

            Plot plot;
            String flagName;
            String flagValue;
            if (args.argsLength() > 2) {
                int plotId = args.getInteger(0);
                flagName = args.getString(1);
                flagValue = args.getString(2);
                plot = plugin.getPlotManager().getPlot(plotId);
                if (plot == null) {
                    throw new CommandException("Es gibt kein Plot mit dieser ID!");
                }
            } else {
                flagName = args.getString(0);
                flagValue = args.getString(1);
                plot = plugin.getPlotManager().getPlot(player.getLocation().getChunk());
                if (plot == null) {
                    throw new CommandException("Hier befindet sich kein Plot!");
                }
            }

            City city = plot.getCity();

            // check if resident has permission
            Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
            if (resident == null || !resident.getRole().hasPermission(RolePermission.PLOT_FLAG_MODIFICATION)) {
                throw new CommandException("Du hast in der Gilde '" + city.getFriendlyName() + "' nicht die Berechtigung Plots zu konfigurieren!");
            }

            try {
                plot.setFlag(player, flagName, flagValue);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
            player.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Flag '" + ChatColor.YELLOW + flagName.toUpperCase()
                    + ChatColor.GREEN + "' auf den Wert '" + ChatColor.YELLOW + flagValue.toUpperCase() + "' gesetzt!");
        }

        @Command(
                aliases = {"info"},
                desc = "Shows info about a plot",
                usage = "[Plot ID]"
        )
        @CommandPermissions("rccities.plot.info")
        public void info(CommandContext args, CommandSender sender) throws CommandException {

            Plot plot;
            if (args.argsLength() > 0) {
                int plotId = args.getInteger(0);
                plot = plugin.getPlotManager().getPlot(plotId);
                if (plot == null) {
                    throw new CommandException("Es gibt kein Plot mit dieser ID!");
                }
            } else {
                if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
                Player player = (Player) sender;
                plot = plugin.getPlotManager().getPlot(player.getLocation().getChunk());
                if (plot == null) {
                    throw new CommandException("Hier befindet sich kein Plot!");
                }
            }

            plugin.getPlotManager().printPlotInfo(plot, sender);
        }

        /*
         ***********************************************************************************************************************************
         */

        public void unclaimPlot(CommandSender sender, Plot plot, boolean restoreSchematics) {

            City city = plot.getCity();
            if (restoreSchematics) {
                try {
                    plugin.getSchematicManager().restorePlot(plot);
                } catch (RaidCraftException e) {
                    sender.sendMessage(ChatColor.RED + "Es ist ein Fehler beim wiederherstellen des Plots aufgetreten! (" + e.getMessage() + ")");
                }
            }

            plot.delete();
            plugin.getResidentManager().broadcastCityMessage(city, "Der Plot '" + plot.getRegionName() + "' wurde gelöscht!");
        }

        public void unclaimAll(CommandSender sender, City city, boolean restoreSchematics) {

            UnclaimAllTask unclaimAllTask = new UnclaimAllTask(sender, city, restoreSchematics);
            unclaimTask = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(RCCitiesPlugin.class), unclaimAllTask, 0, 5 * 20).getTaskId();
        }

        private class UnclaimAllTask implements Runnable {

            private CommandSender sender;
            private City city;
            private boolean restoreSchematics;

            public UnclaimAllTask(CommandSender sender, City city, boolean restoreSchematics) {
                this.sender = sender;
                this.city = city;
                this.restoreSchematics = restoreSchematics;
            }

            @Override
            public void run() {

                List<Plot> plots = RaidCraft.getComponent(RCCitiesPlugin.class).getPlotManager().getPlots(city);
                if (plots.isEmpty()) {
                    RaidCraft.LOGGER.info("[RCCities - Unclaim all] Done: Unclaimed all plots of city + '" + city.getName() + "'!");
                    sender.sendMessage(ChatColor.GREEN + "Done: Unclaimed all plots of city + '" + city.getName() + "'!");
                    Bukkit.getScheduler().cancelTask(unclaimTask);
                    return;
                }

                // get one plot
                Plot plot = plots.get(0);

                RaidCraft.LOGGER.info("[RCCities - Unclaim all] Start unclaiming all plots (" + plots.size() + ") of city + '" + city.getName() + "'!");
                sender.sendMessage(ChatColor.GREEN + "Start unclaiming all plots (" + plots.size() + ") of city + '" + city.getName() + "'!");

                int totalCount = plots.size();
                RaidCraft.LOGGER.info("[RCCities - Unclaim all] Der Plot '" + plot.getRegionName() + "' wurde gelöscht! (übrig: " + (totalCount-1) + ")");
                sender.sendMessage("Der Plot '" + plot.getRegionName() + "' wurde gelöscht! (übrig: " + (totalCount-1) + ")");

                if (restoreSchematics) {
                    try {
                        plugin.getSchematicManager().restorePlot(plot);
                    } catch (RaidCraftException e) {
                        RaidCraft.LOGGER.info("[RCCities - Unclaim all] Fehler beim wiederherstellen des Plots '" + plot.getRegionName() + "'! (" + e.getMessage() + ")");
                        sender.sendMessage(ChatColor.RED + "Fehler beim wiederherstellen des Plots '" + plot.getRegionName() + "'! (" + e.getMessage() + ")");
                    }
                }

                plot.delete();

                if (restoreSchematics) {
                    int i = 0;
                    for (Entity entity : plot.getLocation().getChunk().getEntities()) {
                        entity.remove();
                        i++;
                    }
                    RaidCraft.LOGGER.info("[RCCities - Unclaim all] Removed " + i + " entities in unclaimed chunk!");
                }
            }
        }
    }

}
