package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.request.JoinRequest;
import de.raidcraft.rccities.api.resident.RolePermission;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "REJECT_CITY_JOIN_REQUEST")
public class RejectJoinRequestAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String candidate = args.getString("candidate");
        candidate = ParseString.INST.parse(conversation, candidate);
        String reason = args.getString("reason");
        reason = ParseString.INST.parse(conversation, reason);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        JoinRequest joinRequest = city.getJoinRequest(UUIDUtil.convertPlayer(candidate));
        if (joinRequest == null) return;
        joinRequest.reject(reason);
        RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                .broadcastCityMessage(city, "Der Beitrittsantrag von " + joinRequest.getPlayer() + " wurde abgelehnt! (" + reason + ")", RolePermission.STAFF);
        Player targetPlayer = Bukkit.getPlayer(joinRequest.getPlayer());
        if (targetPlayer == null) return;
        targetPlayer.sendMessage(ChatColor.DARK_RED + "Dein Mitgliedsantrag bei '" + ChatColor.GOLD + joinRequest.getCity().getFriendlyName() + ChatColor.DARK_RED + "' wurde abgelehnt!");
    }
}
