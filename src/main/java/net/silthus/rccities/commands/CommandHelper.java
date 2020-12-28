package net.silthus.rccities.commands;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.silthus.rccities.CityPermissions;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.api.resident.Role;
import net.silthus.rccities.api.resident.RolePermission;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandHelper {

    public static void checkRolePermissions(Player player, City city, RolePermission rolePermission)
            throws ConditionFailedException {

        Resident resident = RCCitiesPlugin.getPlugin().getResidentManager().getResident(player.getUniqueId(), city);
        if (!player.hasPermission(CityPermissions.GROUP_ADMIN) &&
                (resident == null || !resident.getRole().hasPermission(rolePermission))) {
            throw new ConditionFailedException("Du hast in der Stadt nicht die nötigen Rechte!");
        }
    }

    public static Resident getResident(OfflinePlayer residentPlayer, City city, boolean force)
            throws InvalidCommandArgument {

        Resident resident = RCCitiesPlugin.getPlugin().getResidentManager()
                .getResident(residentPlayer.getUniqueId(), city);
        if (resident == null) {
            if (force) {
                try {
                    resident = RCCitiesPlugin.getPlugin().getResidentManager().addResident(city, residentPlayer);
                    resident.setRole(Role.RESIDENT);
                } catch (RaidCraftException e) {
                    throw new InvalidCommandArgument(e.getMessage());
                }
            } else {
                throw new ConditionFailedException("In dieser Stadt gibt es kein Mitglied mit dem Namen '"
                        + residentPlayer.getName() + "'");
            }
        }

        return resident;
    }
}
