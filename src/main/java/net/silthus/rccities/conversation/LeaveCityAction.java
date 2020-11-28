package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.RolePermission;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.ParseString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "LEAVE_CITY")
public class LeaveCityAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        Resident resident = RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                .getResident(conversation.getPlayer().getUniqueId(), city);
        if (resident == null) {
            conversation.getPlayer().sendMessage(" ");
            conversation.getPlayer().sendMessage(ChatColor.RED + "Du bist kein Mitglied dieser Gilde!");
            conversation.endConversation(EndReason.INFORM);
            return;
        }

        if (!resident.getRole().hasPermission(RolePermission.LEAVE)) {
            conversation.getPlayer().sendMessage(" ");
            conversation.getPlayer().sendMessage(ChatColor.RED + "Du darfst diese Gilde nichtverlassen!");
            conversation.endConversation(EndReason.INFORM);
            return;
        }

        Bukkit.broadcastMessage(ChatColor.GOLD + resident.getName() + " hat die Gilde '" + resident.getCity().getFriendlyName() + "' verlassen!");
        resident.delete();
        conversation.endConversation(EndReason.INFORM);
    }
}
