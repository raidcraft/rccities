package net.silthus.rccities.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.api.resident.Role;
import net.silthus.rccities.api.resident.RolePermission;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * @author Philip Urban
 */
public class ResidentCommands {

    private RCCitiesPlugin plugin;

    public ResidentCommands(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"resident", "einwohner"},
            desc = "Resident commands"
    )
    @NestedCommand(value = NestedCommands.class, executeBody = true)
    public void resident(CommandContext args, CommandSender sender) throws CommandException {

        if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
        Player player = (Player) sender;

        plugin.getResidentManager().printResidentInfo(player.getUniqueId(), sender);
    }

    public static class NestedCommands {

        private final RCCitiesPlugin plugin;

        public NestedCommands(RCCitiesPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"info"},
                desc = "Shows info about a resident",
                min = 1,
                usage = "<Resident Name>"
        )
        @CommandPermissions("rccities.resident.info")
        public void info(CommandContext args, CommandSender sender) throws CommandException {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.getString(0));
            plugin.getResidentManager().printResidentInfo(offlinePlayer.getUniqueId(), sender);
        }

        @Command(
                aliases = {"setrole", "promote"},
                desc = "Shows info about a resident",
                min = 2,
                flags = "f",
                usage = "[Stadt] <Spieler> <Beruf>"
        )
        @CommandPermissions("rccities.resident.promote")
        public void setRole(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            Role newRole;
            Role oldRole;
            String target;
            String roleName;

            if (args.argsLength() > 2) {
                target = args.getString(1);
                roleName = args.getString(2);
                city = plugin.getCityManager().getCity(args.getString(0));
                if (city == null) {
                    throw new CommandException("Es gibt keine Stadt mit dem Namen '" + args.getString(0) + "'!");
                }
                if (!player.hasPermission("rccities.resident.promote.all")) {
                    Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                    if (resident == null || !resident.getRole().hasPermission(RolePermission.KICK)) {
                        throw new CommandException("Du darfst keine Berufe in der Stadt '" + city.getFriendlyName() + "' zuweisen!");
                    }
                }
            } else {
                target = args.getString(0);
                roleName = args.getString(1);
                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(player.getUniqueId(), RolePermission.PROMOTE);
                if (citizenships == null) {
                    throw new CommandException("Du besitzt in keiner Stadt das Recht Spielern Berufe zuzuteilen!");
                }
                if (citizenships.size() > 1) {
                    throw new CommandException("Du besitzt in mehreren Städten das Recht Spielern Berufe zuzuteilen! Gebe die gewünschte Stadt als Parameter an.");
                }
                city = citizenships.get(0).getCity();
            }

            try {
                newRole = Role.valueOf(roleName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CommandException("Es gibt keinen Beruf mit diesem Namen. Verfügbare Berufe: " + Arrays.toString(Role.values()));
            }
            if (newRole.isAdminOnly() && !player.hasPermission("rccities.resident.promote.all")) {
                throw new CommandException("Dieser Beruf kann nur von Administratoren vergeben werden!");
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args.getString(0));
            Resident targetResident = plugin.getResidentManager().getResident(offlinePlayer.getUniqueId(), city);
            if (targetResident == null) {
                if (player.hasPermission("rccities.resident.promote.all") && args.hasFlag('f')) {
                    OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
                    try {
                        targetResident = plugin.getResidentManager().addResident(city, targetPlayer);
                        targetResident.setRole(Role.RESIDENT);
                    } catch (RaidCraftException e) {
                        throw new CommandException(e.getMessage());
                    }
                } else {
                    throw new CommandException("In dieser Stadt gibt es kein Mitglied mit dem Namen '" + target + "'");
                }
            }
            oldRole = targetResident.getRole();

            if (oldRole.isAdminOnly() && !player.hasPermission("rccities.resident.promote.all")) {
                throw new CommandException("Der jetzige Beruf des Spielers kann nur von Administratoren geändert werden!");
            }

            targetResident.setRole(newRole);
            // set owner on all city plots
            if (!oldRole.hasPermission(RolePermission.BUILD_EVERYWHERE) && newRole.hasPermission(RolePermission.BUILD_EVERYWHERE)) {
                for (Plot plot : plugin.getPlotManager().getPlots(city)) {
                    plot.updateRegion(false);
                }
            }
            // remove owner from all city plots
            if (oldRole.hasPermission(RolePermission.BUILD_EVERYWHERE) && !newRole.hasPermission(RolePermission.BUILD_EVERYWHERE)) {
                for (Plot plot : plugin.getPlotManager().getPlots(city)) {
                    plot.updateRegion(false);
                }
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + targetResident.getName() + " ist nun " + newRole.getFriendlyName() + " der Stadt '" + city.getFriendlyName() + "'!");
        }
    }
}
