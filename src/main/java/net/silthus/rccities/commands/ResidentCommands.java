package net.silthus.rccities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.google.common.base.Strings;
import net.silthus.rccities.CityPermissions;
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
    @CommandPermission(CityPermissions.GROUP_USER + ".resident.info")
    public void resident(Player player, @Optional OfflinePlayer resident) {

        if(resident == null) {
            resident = player;
        }
        plugin.getResidentManager().printResidentInfo(resident.getUniqueId(), player);
    }

    @Subcommand("setrole|promote")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".resident.promote")
    public void setRole(Player player, OfflinePlayer residentPlayer, City city, String roleName, CommandFlag flags) {

        Role newRole;

        CommandHelper.checkRolePermissions(player, city, RolePermission.PROMOTE);

        try {
            newRole = Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConditionFailedException("Es gibt keinen Beruf mit diesem Namen. Verfügbare Berufe: "
                    + Arrays.toString(Role.values()));
        }

        setResidentRole(player, residentPlayer, city, flags, newRole);
    }

    @Subcommand("setmayor")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".resident.promote")
    public void setMayor(Player player, OfflinePlayer residentPlayer, City city, CommandFlag flags) {

        setResidentRole(player, residentPlayer, city, flags, Role.MAYOR);
    }

    @Subcommand("setresident")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".resident.promote")
    public void setResident(Player player, OfflinePlayer residentPlayer, City city, CommandFlag flags) {

        setResidentRole(player, residentPlayer, city, flags, Role.RESIDENT);
    }

    @Subcommand("setvicemayor")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".resident.promote")
    public void setViceMayor(Player player, OfflinePlayer residentPlayer, City city, CommandFlag flags) {

        setResidentRole(player, residentPlayer, city, flags, Role.VICE_MAYOR);
    }

    @Subcommand("setassistant")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".resident.promote")
    public void setAssistant(Player player, OfflinePlayer residentPlayer, City city, CommandFlag flags) {

        setResidentRole(player, residentPlayer, city, flags, Role.ASSISTANT);
    }

    @Subcommand("setslave")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".resident.promote")
    public void setSlave(Player player, OfflinePlayer residentPlayer, City city, CommandFlag flags) {

        setResidentRole(player, residentPlayer, city, flags, Role.SLAVE);
    }

    private void setResidentRole(Player player, OfflinePlayer residentPlayer, City city, CommandFlag flags, Role role)
        throws ConditionFailedException {

        CommandHelper.checkRolePermissions(player, city, RolePermission.PROMOTE);

        if (role.isAdminOnly() && !player.hasPermission(CityPermissions.GROUP_ADMIN)) {
            throw new ConditionFailedException("Dieser Beruf kann nur von Administratoren vergeben werden!");
        }

        Resident targetResident = CommandHelper.getResident(residentPlayer, city, flags.hasAdminFlag(player, 'f'));

        if (targetResident.getRole().isAdminOnly() && !player.hasPermission(CityPermissions.GROUP_ADMIN)) {
            throw new ConditionFailedException("Der jetzige Beruf des Spielers kann nur von Administratoren geändert werden!");
        }

        if(targetResident.getRole() == role) {
            throw new ConditionFailedException("Der Einwohner hat bereits diesen Bruf");
        }

        targetResident.setRole(role);

        Bukkit.broadcastMessage(ChatColor.GOLD + targetResident.getName() + " ist nun "
                + role.getFriendlyName() + " der Stadt '" + city.getFriendlyName() + "'!");
    }
}
