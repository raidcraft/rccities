package net.silthus.rccities.commands;

import co.aikar.commands.InvalidCommandArgument;
import net.silthus.rccities.CityPermissions;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.api.resident.RolePermission;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandHelper {

    public static boolean hasRolePermissions(Player player, City city, RolePermission rolePermission) {

        Resident resident = RCCitiesPlugin.getPlugin().getResidentManager().getResident(player.getUniqueId(), city);
        if (!player.hasPermission(CityPermissions.GROUP_ADMIN) &&
                (resident == null || !resident.getRole().hasPermission(rolePermission))) {
            return false;
        }

        return true;
    }
}
