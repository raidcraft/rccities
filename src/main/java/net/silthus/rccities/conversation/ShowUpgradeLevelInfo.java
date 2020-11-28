package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.request.UpgradeRequest;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "SHOW_UPGRADE_INFO")
public class ShowUpgradeLevelInfo extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String upgradeType = args.getString("upgrade-type");
        upgradeType = ParseString.INST.parse(conversation, upgradeType);
        String upgradeLevel = args.getString("upgrade-level");
        upgradeLevel = ParseString.INST.parse(conversation, upgradeLevel);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        Upgrade upgrade = city.getUpgrades().getUpgrade(upgradeType);
        if (upgrade == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Upgrade '" + upgradeType + "' does not exist!");
        }

        UpgradeLevel<City> level = upgrade.getLevel(upgradeLevel);
        if (level == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Level '" + upgradeLevel + "' does not exist!");
        }

        String state;
        UpgradeRequest upgradeRequest = RaidCraft.getComponent(RCCitiesPlugin.class).getUpgradeRequestManager().getRequest(city, level);

        if (!level.isStored()) {
            state = ChatColor.GRAY + "Beliebig oft kaufbar!";
            conversation.set("level_state_indicator", "");
        } else if (level.isUnlocked()) {
            state = ChatColor.GREEN + "Freigeschaltet";
            conversation.set("level_state_indicator", "&c&m");
        } else if (upgradeRequest == null) {
            state = ChatColor.RED + "Nicht Freigeschaltet";
            conversation.set("level_state_indicator", "");
        } else if (upgradeRequest.isRejected()) {
            state = ChatColor.RED + "Freischaltung abgelehnt (" + upgradeRequest.getRejectReason() + ")";
            conversation.set("level_state_indicator", "");
        } else {
            state = ChatColor.YELLOW + "Freischaltung beantragt";
            conversation.set("level_state_indicator", "&c&m");
        }

        Player player = conversation.getPlayer();

        player.sendMessage(" ");
        player.sendMessage(ChatColor.AQUA + "Informationen zum Upgrade '" + ChatColor.GOLD + level.getName() + ChatColor.AQUA + "':");
        player.sendMessage(ChatColor.AQUA + "Status: " + state);
        player.sendMessage(ChatColor.AQUA + "Freischalt-Bedingung:");
        for (String requirement : level.getRequirementDescription()) {
            player.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + requirement);
        }
        player.sendMessage(ChatColor.AQUA + "Belohnung:");
        for (String reward : level.getRewardDescription()) {
            player.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + reward);
        }
    }
}
