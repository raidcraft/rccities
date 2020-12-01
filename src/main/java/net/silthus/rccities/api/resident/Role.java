package net.silthus.rccities.api.resident;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Philip Urban
 */
public enum Role {

    SLAVE("Sklave", true, ChatColor.GRAY,
            RolePermission.COLLECT_EXP
    ),

    RESIDENT("Einwohner", false, ChatColor.GREEN,
            RolePermission.LEAVE,
            RolePermission.DEPOSIT,
            RolePermission.COLLECT_EXP,
            RolePermission.PREFIX_SKILL,
            RolePermission.GET_KICKED,
            RolePermission.SPAWN_TELEPORT
    ),

    ADMIN("Administrator", true, ChatColor.DARK_RED,
            RolePermission.LEAVE,
            RolePermission.KICK,
            RolePermission.INVITE,
            RolePermission.BUILD_EVERYWHERE,
            RolePermission.PLOT_DISTRIBUTION,
            RolePermission.PLOT_CLAIM,
            RolePermission.SET_DESCRIPTION,
            RolePermission.SET_SPAWN,
            RolePermission.CITY_FLAG_MODIFICATION,
            RolePermission.PLOT_FLAG_MODIFICATION,
            RolePermission.PROMOTE,
            RolePermission.SPAWN_TELEPORT,
            RolePermission.PLOT_BUY,
            RolePermission.DEPOSIT,
            RolePermission.WITHDRAW,
            RolePermission.STAFF,
            RolePermission.PREFIX_SKILL
    ),

    MAYOR("Bürgermeister", true, ChatColor.RED,
            RolePermission.LEAVE,
            RolePermission.KICK,
            RolePermission.INVITE,
            RolePermission.BUILD_EVERYWHERE,
            RolePermission.PLOT_DISTRIBUTION,
            RolePermission.PLOT_CLAIM,
            RolePermission.SET_DESCRIPTION,
            RolePermission.SET_SPAWN,
            RolePermission.PROMOTE,
            RolePermission.CITY_FLAG_MODIFICATION,
            RolePermission.PLOT_FLAG_MODIFICATION,
            RolePermission.PLOT_BUY,
            RolePermission.DEPOSIT,
            RolePermission.WITHDRAW,
            RolePermission.STAFF,
            RolePermission.COLLECT_EXP,
            RolePermission.PREFIX_SKILL,
            RolePermission.SPAWN_TELEPORT
    ),

    VICE_MAYOR("Vize Bürgermeister", false, ChatColor.DARK_BLUE,
            RolePermission.LEAVE,
            RolePermission.KICK,
            RolePermission.INVITE,
            RolePermission.BUILD_EVERYWHERE,
            RolePermission.PLOT_DISTRIBUTION,
            RolePermission.PLOT_CLAIM,
            RolePermission.CITY_FLAG_MODIFICATION,
            RolePermission.PLOT_FLAG_MODIFICATION,
            RolePermission.PROMOTE,
            RolePermission.PLOT_BUY,
            RolePermission.DEPOSIT,
            RolePermission.STAFF,
            RolePermission.COLLECT_EXP,
            RolePermission.PREFIX_SKILL,
            RolePermission.GET_KICKED,
            RolePermission.SPAWN_TELEPORT
    ),

    ASSISTANT("Stadtassistent", false, ChatColor.BLUE,
            RolePermission.LEAVE,
            RolePermission.INVITE,
            RolePermission.BUILD_EVERYWHERE,
            RolePermission.PLOT_DISTRIBUTION,
            RolePermission.PLOT_FLAG_MODIFICATION,
            RolePermission.DEPOSIT,
            RolePermission.STAFF,
            RolePermission.COLLECT_EXP,
            RolePermission.PREFIX_SKILL,
            RolePermission.GET_KICKED,
            RolePermission.SPAWN_TELEPORT
    );

    private final Set<RolePermission> permissions = new HashSet<>();
    private final String friendlyName;
    private final boolean adminOnly;
    private final ChatColor chatColor;

    Role(String friendlyName, boolean adminOnly, ChatColor chatColor, RolePermission... permissions) {

        this.friendlyName = friendlyName;
        this.adminOnly = adminOnly;
        this.chatColor = chatColor;
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public boolean hasPermission(RolePermission permission) {

        return permissions.contains(permission);
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public boolean isAdminOnly() {

        return adminOnly;
    }

    public ChatColor getChatColor() {

        return chatColor;
    }
}
