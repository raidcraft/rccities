package net.silthus.rccities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.google.common.base.Strings;
import net.silthus.rccities.CityPermissions;
import net.silthus.rccities.DatabasePlot;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.api.resident.RolePermission;
import net.silthus.rccities.util.QueuedCaptchaCommand;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.*;
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
@CommandAlias("plot")
public class PlotCommands extends BaseCommand {

    private final RCCitiesPlugin plugin;
    private static int unclaimTask = 0;

    public PlotCommands(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    @Default
    @Subcommand("info")
    @CommandPermission(CityPermissions.GROUP_USER + ".plot.info")
    public void info(Player player, Plot plot) {

        plugin.getPlotManager().printPlotInfo(plot, player);
    }

    @Subcommand("take")
    @CommandPermission(CityPermissions.GROUP_USER + ".plot.take")
    public void take(Player player, Plot plot, String targetResidentName) {

        City city = plot.getCity();
        Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);

        // check if resident has permission
        if(!CommandHelper.hasRolePermissions(player, city, RolePermission.PLOT_DISTRIBUTION)) {
            throw new InvalidCommandArgument("Du hast in der Stadt nicht die Berechtigung Plots zu entziehen!");
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetResidentName);
        Resident targetResident = plugin.getResidentManager().getResident(offlinePlayer.getUniqueId(), city);
        if (targetResident == null) {
            throw new InvalidCommandArgument("Der angegebene Spieler ist kein Mitglied deiner Stadt '"
                    + city.getFriendlyName() + "'!");
        }

        plot.removeResident(resident);
        plot.getCity().refreshFlags();
        plot.refreshFlags();
        player.sendMessage(ChatColor.GREEN + "Du hast den Plot '" + plot.getRegionName() + "' erfolgreich "
                + targetResident.getName() + " entzogen!");
        if (targetResident.getPlayer() != null) {
            targetResident.getPlayer()
                    .sendMessage(ChatColor.GREEN + "Dir wurde in der Stadt '" + city.getFriendlyName()
                            + "' der Plot '" + plot.getRegionName() + "' entzogen!");
        }
    }

    @Subcommand("give")
    @CommandPermission(CityPermissions.GROUP_USER + ".plot.give")
    public void give(Player player, Plot plot, String resident) {

        City city = plot.getCity();
        Resident residentObj = plugin.getResidentManager().getResident(player.getUniqueId(), city);

        // check if resident has permission
        if(!CommandHelper.hasRolePermissions(player, city, RolePermission.PLOT_DISTRIBUTION)) {
            throw new InvalidCommandArgument("Du hast in der Stadt nicht die Berechtigung Plots zu vergeben!");
        }

        Resident targetResident = plugin.getResidentManager().getResident(resident, city);
        if (targetResident == null) {
            throw new InvalidCommandArgument("Der angegebene Spieler ist kein Mitglied deiner Stadt '"
                    + city.getFriendlyName() + "'!");
        }

        plot.assignResident(targetResident);
        plot.getCity().refreshFlags();
        plot.refreshFlags();
        player.sendMessage(ChatColor.GREEN + "Du hast den Plot '" + plot.getRegionName() + "' erfolgreich an "
                + targetResident.getName() + " vergeben!");
        if (targetResident.getPlayer() != null) {
            targetResident.getPlayer()
                    .sendMessage(ChatColor.GREEN + "Dir wurde in der Stadt '"
                            + city.getFriendlyName() + "' der Plot '" + plot.getRegionName() + "' zugewiesen!");
        }
    }

    @Subcommand("claim")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".plot.claim")
    public void claim(Player player, @Optional String cityName, CommandFlag flags) {

        // Check if there is already an plot
        if(plugin.getPlotManager().getPlot(player.getLocation().getChunk()) != null) {
            throw new ConditionFailedException("An dieser Stelle befindet sich bereits eine Stadt Plot!");
        }

        // check if here is a wrong region
        if (!plugin.getWorldGuardManager().claimable(player.getLocation())) {
            throw new ConditionFailedException("An dieser Stelle befindet sich bereits eine andere Region!");
        }

        // get neighbor plot and city
        Chunk chunk = player.getLocation().getChunk();
        Plot[] neighborPlots = new Plot[8];
        neighborPlots[0] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX(), chunk.getZ() + 1));
        neighborPlots[1] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX() - 1, chunk.getZ() + 1));
        neighborPlots[2] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX() + 1, chunk.getZ() + 1));
        neighborPlots[3] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX(), chunk.getZ() - 1));
        neighborPlots[4] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX() - 1, chunk.getZ() - 1));
        neighborPlots[5] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX() + 1, chunk.getZ() - 1));
        neighborPlots[6] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX() - 1, chunk.getZ()));
        neighborPlots[7] = plugin.getPlotManager().getPlot(player.getWorld()
                .getChunkAt(chunk.getX() + 1, chunk.getZ()));

        City city = null;
        for (Plot plot : neighborPlots) {
            if (plot == null) continue;
            if (city != null && !city.equals(plot.getCity())) {
                throw new ConditionFailedException("Dieser Plot liegt zu dicht an einer anderen Stadt!");
            }
            city = plot.getCity();
        }
        // admins can claim wild chunks
        if(flags.hasAdminFlag(player, 'f')) {
            if(city == null) {
                if(Strings.isNullOrEmpty(cityName)) {
                    throw new InvalidCommandArgument("Gebe eine Stadt als ersten Parameter an!");
                }
                city = plugin.getCityManager().getCity(cityName);
            }
        } else if (city == null) {
            throw new ConditionFailedException("Neue Plots müssen an bestehende anknüpfen!");
        }

        // check if resident has permission
        if(!CommandHelper.hasRolePermissions(player, city, RolePermission.PLOT_CLAIM)) {
            throw new InvalidCommandArgument("Du hast in der Stadt nicht die Berechtigung Plots zu Claimen!");
        }

        // check plot credit
        if (city.getPlotCredit() == 0) {
            throw new ConditionFailedException("Deine Stadt hat keine freien Plots zum claimen!");
        }

        // check max radius
        Location plotCenter = new Location(chunk.getWorld(), chunk.getX() * 16 + 8, 0, chunk.getZ() * 16 + 8);
        Location fixedSpawn = city.getSpawn().clone();
        fixedSpawn.setY(0);
        if (fixedSpawn.distance(plotCenter) > city.getMaxRadius()) {
            throw new ConditionFailedException("Deine Stadt darf nur im Umkreis von "
                    + city.getMaxRadius() + " Blöcken um den Stadtmittelpunkt claimen!");
        }

        Plot plot = new DatabasePlot(plotCenter, city);

        // create schematic
        try {
            plugin.getSchematicManager().createSchematic(plot);
        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }

        // Mark plot
        try {
            plot.setFlag(player, "MARK_FREE", "ON");
        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }

        // withdraw plot credit
        city.setPlotCredit(city.getPlotCredit() - 1);

        // reload city flags
        city.refreshFlags();

        player.sendMessage(ChatColor.GREEN + "Der Plot wurde erfolgreich geclaimt! (Restliches Guthaben: "
                + city.getPlotCredit() + " Plots)");
    }

    @Subcommand("unclaim")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".plot.unclaim")
    public void unclaim(Player player, Plot plot, CommandFlag flags) {

        boolean restoreSchematics = false;
        boolean force = false;
        if (flags.hasAdminFlag(player, 'r')) {
            restoreSchematics = true;
        }
        if (flags.hasAdminFlag(player, 'f')) {
            force = true;
        }

        if (plugin.getPlotManager().getPlots(plot.getCity()).size() == 1) {
            throw new InvalidCommandArgument("Der letze Plot kann nicht gelöscht werden!");
        }

        try {
            if (restoreSchematics) {
                player.sendMessage(ChatColor.DARK_RED
                        + "Bei der Löschung des Plots wird die Landschaft zurückgesetzt!");
            } else {
                player.sendMessage(ChatColor.DARK_RED
                        + "Bei der Löschung des Plots wird die Landschaft NICHT zurückgesetzt!");
            }
            if(flags.hasAdminFlag(player, 'a')) {
                player.sendMessage(ChatColor.DARK_RED
                        + "Bist du sicher dass ALLE plots wiederhergestellt werden sollen?");
                new QueuedCaptchaCommand(player, this, "unclaimAll", player, plot.getCity());

            } else {
                if (force) {
                    unclaimPlot(player, plot, restoreSchematics);
                } else {
                    new QueuedCaptchaCommand(player, this, "unclaimPlot",
                            player, plot, restoreSchematics);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
    }

    @Subcommand("flag")
    @CommandPermission(CityPermissions.GROUP_USER + ".plot.flag")
    public void flag(Player player, Plot plot, @Optional String flagName, @Optional String flagValue) {

        if(Strings.isNullOrEmpty(flagName) || Strings.isNullOrEmpty(flagValue)) {
            String flagList = "";
            for(FlagInformation info : plugin.getFlagManager().getRegisteredPlotFlagInformationList()) {
                flagList += info.name() + ", ";
            }
            throw new InvalidCommandArgument("Verfügbare Flags: " + flagList);
        }

        City city = plot.getCity();

        // check if resident has permission
        if(!CommandHelper.hasRolePermissions(player, city, RolePermission.PLOT_FLAG_MODIFICATION)) {
            throw new InvalidCommandArgument("Du hast in der Stadt nicht die Berechtigung Plots zu Konfigurieren!");
        }

        try {
            plot.setFlag(player, flagName, flagValue);
        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
        player.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Flag '"
                + ChatColor.YELLOW + flagName.toUpperCase()
                + ChatColor.GREEN + "' auf den Wert '" + ChatColor.YELLOW
                + flagValue.toUpperCase() + "' gesetzt!");
    }

    @Subcommand("mark")
    @CommandPermission(CityPermissions.GROUP_USER + ".plot.flag")
    public void mark(Player player, Plot plot) {

        City city = plot.getCity();

        // check if resident has permission
        if(!CommandHelper.hasRolePermissions(player, city, RolePermission.PLOT_FLAG_MODIFICATION)) {
            throw new InvalidCommandArgument("Du hast in der Stadt nicht die Berechtigung Plots zu Konfigurieren!");
        }

        try {
            plot.setFlag(player, "MARK", "ON");
        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
        player.sendMessage(ChatColor.GREEN + "Du hast erfolgreich den Plot markiert!");
    }

    @Subcommand("unmark")
    @CommandPermission(CityPermissions.GROUP_USER + ".plot.flag")
    public void unmark(Player player, Plot plot) {

        City city = plot.getCity();

        // check if resident has permission
        if(!CommandHelper.hasRolePermissions(player, city, RolePermission.PLOT_FLAG_MODIFICATION)) {
            throw new InvalidCommandArgument("Du hast in der Stadt nicht die Berechtigung Plots zu Konfigurieren!");
        }

        try {
            plot.setFlag(player, "MARK", "OFF");
        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
        player.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Plot Markierung entfernt!");
    }

    /*
     *******************************************************************************************************************
     */

    public void unclaimPlot(CommandSender sender, Plot plot, boolean restoreSchematics) {

        City city = plot.getCity();

        if (restoreSchematics) {
            try {
                plugin.getSchematicManager().restorePlot(plot);
            } catch (RaidCraftException e) {
                sender.sendMessage(ChatColor.RED
                        + "Es ist ein Fehler beim wiederherstellen des Plots aufgetreten! (" + e.getMessage() + ")");
            }
        }

        plot.delete();
        plugin.getResidentManager().broadcastCityMessage(city, "Der Plot '"
                + plot.getRegionName() + "' wurde gelöscht!");
    }

    public void unclaimAll(CommandSender sender, City city, boolean restoreSchematics) {

        UnclaimAllTask unclaimAllTask = new UnclaimAllTask(sender, city, restoreSchematics);
        unclaimTask = Bukkit.getScheduler()
                .runTaskTimer(RCCitiesPlugin.getPlugin(), unclaimAllTask, 0, 5 * 20).getTaskId();
    }

    private class UnclaimAllTask implements Runnable {

        private final CommandSender sender;
        private final City city;
        private final boolean restoreSchematics;

        public UnclaimAllTask(CommandSender sender, City city, boolean restoreSchematics) {
            this.sender = sender;
            this.city = city;
            this.restoreSchematics = restoreSchematics;
        }

        @Override
        public void run() {

            List<Plot> plots = RCCitiesPlugin.getPlugin().getPlotManager().getPlots(city);
            if (plots.isEmpty()) {
                plugin.getLogger().info("[RCCities - Unclaim all] Done: Unclaimed all plots of city + '"
                        + city.getName() + "'!");
                sender.sendMessage(ChatColor.GREEN + "Done: Unclaimed all plots of city + '" + city.getName() + "'!");
                Bukkit.getScheduler().cancelTask(unclaimTask);
                return;
            }

            // get one plot
            Plot plot = plots.get(0);

            plugin.getLogger().info("[RCCities - Unclaim all] Start unclaiming all plots ("
                    + plots.size() + ") of city + '" + city.getName() + "'!");
            sender.sendMessage(ChatColor.GREEN + "Start unclaiming all plots (" + plots.size() + ") of city + '"
                    + city.getName() + "'!");

            int totalCount = plots.size();
            plugin.getLogger().info("[RCCities - Unclaim all] Der Plot '" + plot.getRegionName()
                    + "' wurde gelöscht! (übrig: " + (totalCount-1) + ")");
            sender.sendMessage("Der Plot '" + plot.getRegionName() + "' wurde gelöscht! (übrig: "
                    + (totalCount-1) + ")");

            if (restoreSchematics) {
                try {
                    plugin.getSchematicManager().restorePlot(plot);
                } catch (RaidCraftException e) {
                    plugin.getLogger().info("[RCCities - Unclaim all] Fehler beim wiederherstellen des Plots '"
                            + plot.getRegionName() + "'! (" + e.getMessage() + ")");
                    sender.sendMessage(ChatColor.RED + "Fehler beim wiederherstellen des Plots '"
                            + plot.getRegionName() + "'! (" + e.getMessage() + ")");
                }
            }

            plot.delete();

            if (restoreSchematics) {
                int i = 0;
                for (Entity entity : plot.getLocation().getChunk().getEntities()) {
                    entity.remove();
                    i++;
                }
                plugin.getLogger().info("[RCCities - Unclaim all] Removed " + i + " entities in unclaimed chunk!");
            }
        }
    }
}
