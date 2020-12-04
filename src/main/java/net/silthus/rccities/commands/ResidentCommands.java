package net.silthus.rccities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.google.common.base.Strings;
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
@CommandAlias("resident")
public class ResidentCommands extends BaseCommand {

    private final RCCitiesPlugin plugin;

    public ResidentCommands(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    @Default
    @Subcommand("info")
    @CommandPermission("rccities.resident.info")
    public void resident(Player player, @Optional OfflinePlayer resident) {

        if(resident == null) {
            resident = player;
        }
        plugin.getResidentManager().printResidentInfo(resident.getUniqueId(), player);
    }

    @Subcommand("setrole|promote")
    @CommandCompletion("@cities")
    @CommandPermission("rccities.resident.promote")
    public void setRole(Player player, OfflinePlayer residentPlayer, City city, String roleName, @Optional String flags) {

        Role newRole;
        Role oldRole;

        Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
        if (!player.hasPermission("rccities.resident.promote.all")) {
            if (resident == null || !resident.getRole().hasPermission(RolePermission.KICK)) {
                throw new InvalidCommandArgument("Du darfst keine Berufe in der Stadt '" + city.getFriendlyName()
                        + "' zuweisen!");
            }
        }

        try {
            newRole = Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCommandArgument("Es gibt keinen Beruf mit diesem Namen. Verfügbare Berufe: "
                    + Arrays.toString(Role.values()));
        }
        if (newRole.isAdminOnly() && !player.hasPermission("rccities.resident.promote.all")) {
            throw new InvalidCommandArgument("Dieser Beruf kann nur von Administratoren vergeben werden!");
        }

        Resident targetResident = plugin.getResidentManager().getResident(residentPlayer.getUniqueId(), city);
        if (targetResident == null) {
            if (!Strings.isNullOrEmpty(flags) && flags.contains("f") && player.hasPermission("rccities.resident.promote.all")) {
                try {
                    targetResident = plugin.getResidentManager().addResident(city, residentPlayer);
                    targetResident.setRole(Role.RESIDENT);
                } catch (RaidCraftException e) {
                    throw new InvalidCommandArgument(e.getMessage());
                }
            } else {
                throw new InvalidCommandArgument("In dieser Stadt gibt es kein Mitglied mit dem Namen '" + residentPlayer.getName() + "'");
            }
        }
        oldRole = targetResident.getRole();

        if (oldRole.isAdminOnly() && !player.hasPermission("rccities.resident.promote.all")) {
            throw new InvalidCommandArgument("Der jetzige Beruf des Spielers kann nur von Administratoren geändert werden!");
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
