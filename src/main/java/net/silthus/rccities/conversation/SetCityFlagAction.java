package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.FlagInformation;
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
@ActionInformation(name = "SET_CITY_FLAG")
public class SetCityFlagAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String flagName = args.getString("flag-name");
        flagName = ParseString.INST.parse(conversation, flagName);
        String flagValue = args.getString("flag-value");
        flagValue = ParseString.INST.parse(conversation, flagValue);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }


        try {
            FlagInformation flagInformation = RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().getRegisteredCityFlagInformation(flagName);
            RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().setCityFlag(city, conversation.getPlayer(), flagName, flagValue);
            conversation.getPlayer().sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Flag '" + ChatColor.YELLOW + flagInformation.friendlyName()
                    + ChatColor.GREEN + "' auf den Wert '" + ChatColor.YELLOW + flagValue.toUpperCase() + ChatColor.GREEN + "' gesetzt!");
        } catch (NullPointerException | RaidCraftException e) {
            conversation.getPlayer().sendMessage(ChatColor.RED + "Fehler beim �ndern der Flag: " + e.getMessage());
            conversation.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
        conversation.endConversation(EndReason.INFORM);
    }
}
