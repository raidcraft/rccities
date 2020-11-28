package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.CityFlag;
import de.raidcraft.rccities.api.request.JoinRequest;
import de.raidcraft.rccities.api.resident.RolePermission;
import de.raidcraft.rccities.flags.city.admin.InviteCityFlag;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.ParseString;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "SEND_CITY_JOIN_REQUEST")
public class SendJoinRequestAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName()
                    + "': City '" + cityName + "' does not exist!");
        }

        // invitation is locked
        CityFlag inviteFlag = RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().getCityFlag(city, InviteCityFlag.class);
        if (inviteFlag != null && !inviteFlag.getType().convertToBoolean(inviteFlag.getValue())) {
            conversation.getPlayer().sendMessage("");
            conversation.getPlayer().sendMessage(ChatColor.RED
                    + "Diese Gilde darf zurzeit keine neuen Mitglieder aufnehmen!");
            conversation.endConversation(EndReason.INFORM);
            return;
        }

        JoinRequest joinRequest = city.getJoinRequest(conversation.getPlayer().getUniqueId());
        if (joinRequest != null) {
            if (joinRequest.isRejected()) {
                conversation.getPlayer().sendMessage("");
                conversation.getPlayer().sendMessage(ChatColor.RED + "Diese Gilde will dich nicht als Mitglied haben!");
                conversation.getPlayer().sendMessage(ChatColor.RED + "Grund:" + joinRequest.getRejectReason());
                conversation.endConversation(EndReason.INFORM);
                return;
            }
        }

        city.sendJoinRequest(conversation.getPlayer().getUniqueId());
        RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                .broadcastCityMessage(city, conversation.getPlayer().getName()
                        + " m�chte gerne der Gilde beitreten!", RolePermission.STAFF);

        // delete all other join requests
        RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                .deleteOtherJoinRequests(conversation.getPlayer().getUniqueId(), city);
    }
}
