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
import de.raidcraft.rcconversations.conversations.EndReason;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.unlockresult.UnlockResult;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;
import de.raidcraft.util.DateUtil;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "REQUEST_UPRAGE_UNLOCK")
public class RequestUpgradeLevelUnlockAction extends AbstractAction {

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

        UpgradeRequest upgradeRequest = RaidCraft.getComponent(RCCitiesPlugin.class).getUpgradeRequestManager().getRequest(city, level);
        conversation.getPlayer().sendMessage("");

        // level is already unlocked
        if (level.isUnlocked() && level.isStored()) {
            conversation.getPlayer().sendMessage(ChatColor.RED + "Dieses Upgrade ist bereits freigeschaltet!");
            conversation.endConversation(EndReason.INFORM);
            return;
        }

        // check existing request
        if (upgradeRequest != null) {
            // check if rejected (cooldown)
            if (upgradeRequest.isRejected() && System.currentTimeMillis() < upgradeRequest.getRejectExpirationDate()) {
                // check if cooldown over
                if (System.currentTimeMillis() < upgradeRequest.getRejectExpirationDate()) {
                    conversation.getPlayer().sendMessage(ChatColor.RED + "Die Freischaltung wurde vor kurzem abgelehnt!");
                    conversation.getPlayer().sendMessage(ChatColor.RED + "Grund: " + upgradeRequest.getRejectReason());
                    conversation.getPlayer().sendMessage(ChatColor.RED + "Der nächste Antrag kann am " + DateUtil.getDateString(upgradeRequest.getRejectExpirationDate())
                            + " gestellt werden.");
                    conversation.endConversation(EndReason.INFORM);
                    return;
                }
            }
            // check if in request progress
            else if (!upgradeRequest.isRejected()) {
                conversation.getPlayer().sendMessage(ChatColor.RED + "Die Freischaltung wurde bereits beantragt.");
                conversation.endConversation(EndReason.INFORM);
                return;
            }
            // check if the reject expiration date is in the past and reactivate the upgrade request
            else if (upgradeRequest.isRejected() && System.currentTimeMillis() > upgradeRequest.getRejectExpirationDate()) {
                conversation.getPlayer().sendMessage(ChatColor.RED + "Die Freischaltung wurde noch einmal beantragt.");
                upgradeRequest.reactivate();
                return;
            }
        }

        // add request
        UnlockResult unlockResult = level.tryToUnlock(city);

        if (unlockResult.isSuccessful()) {
            conversation.getPlayer().sendMessage(ChatColor.GREEN + "Die Freischaltung war erfolgreich!");
            conversation.endConversation(EndReason.INFORM);
        } else {
            conversation.getPlayer().sendMessage(ChatColor.RED + unlockResult.getLongReason());
            conversation.endConversation(EndReason.INFORM);
        }
    }
}
