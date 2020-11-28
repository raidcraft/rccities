package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.Role;
import de.raidcraft.rccities.api.resident.RolePermission;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "SET_CITY_RESIDENT_ROLE")
public class SetResidentRoleAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String residentName = args.getString("resident");
        residentName = ParseString.INST.parse(conversation, residentName);
        String roleName = args.getString("role");
        roleName = ParseString.INST.parse(conversation, roleName);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        Resident resident = RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().getResident(UUIDUtil.convertPlayer(residentName), city);
        if (resident == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Resident '" + resident + "' does not exist!");
        }

        Role newRole = Role.valueOf(roleName.toUpperCase());
        Role oldRole = resident.getRole();
        if (oldRole.isAdminOnly() && !conversation.getPlayer().hasPermission("rccities.resident.promote.all")) {
            conversation.getPlayer().sendMessage("Der jetzige Beruf des Einwohners kann nur von Administratoren ge�ndert werden!");
            conversation.endConversation(EndReason.INFORM);
            return;
        }

        if (newRole.isAdminOnly() && !conversation.getPlayer().hasPermission("rccities.resident.promote.all")) {
            conversation.getPlayer().sendMessage("Dieser Beruf kann nur von Administratoren vergeben werden!");
            conversation.endConversation(EndReason.INFORM);
            return;
        }

        resident.setRole(newRole);
        // set owner on all city plots
        if (!oldRole.hasPermission(RolePermission.BUILD_EVERYWHERE) && newRole.hasPermission(RolePermission.BUILD_EVERYWHERE)) {
            for (Plot plot : RaidCraft.getComponent(RCCitiesPlugin.class).getPlotManager().getPlots(city)) {
                plot.updateRegion(false);
            }
        }
        // remove owner from all city plots
        if (oldRole.hasPermission(RolePermission.BUILD_EVERYWHERE) && !newRole.hasPermission(RolePermission.BUILD_EVERYWHERE)) {
            for (Plot plot : RaidCraft.getComponent(RCCitiesPlugin.class).getPlotManager().getPlots(city)) {
                plot.updateRegion(false);
            }
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + resident.getName() + " ist nun " + newRole.getFriendlyName() + " der Gilde '" + city.getFriendlyName() + "'!");
    }
}
