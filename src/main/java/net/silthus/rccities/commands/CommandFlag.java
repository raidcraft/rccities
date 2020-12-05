package net.silthus.rccities.commands;

import lombok.Getter;
import net.silthus.rccities.CityPermissions;
import org.bukkit.entity.Player;

@Getter
public class CommandFlag {

    private String flagString;

    public static CommandFlag EMPTY_FLAG = new CommandFlag("");

    public CommandFlag(String flagString) {
        this.flagString = flagString;
    }

    public boolean hasFlag(char flag) {
        return flagString.contains(String.valueOf(flag));
    }

    public boolean hasAdminFlag(Player player, char flag) {

        if(!player.hasPermission(CityPermissions.GROUP_ADMIN)) {
            return false;
        }

        return hasFlag(flag);
    }
}
