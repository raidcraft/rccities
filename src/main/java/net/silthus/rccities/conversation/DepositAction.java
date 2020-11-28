package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.api.economy.BalanceSource;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;
import org.bukkit.ChatColor;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "CITY_DEPOSIT")
public class DepositAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        Economy economy = RaidCraft.getEconomy();
        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);
        String amountString = args.getString("amount");
        amountString = ParseString.INST.parse(conversation, amountString);
        double amount = economy.parseCurrencyInput(amountString);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        if (amount == 0 || economy.getBalance(conversation.getPlayer().getUniqueId()) < amount) {
            changeStage(conversation, failure);
            return;
        }

        economy.add(AccountType.CITY, city.getBankAccountName(), amount,
                BalanceSource.GUILD, "Einzahlung von " + conversation.getPlayer().getName());
        economy.substract(conversation.getPlayer().getUniqueId(), amount,
                BalanceSource.GUILD, "Einzahlung in Gildenkasse");

        conversation.getPlayer().sendMessage(ChatColor.GREEN + "Du hast " + economy.getFormattedAmount(amount) + ChatColor.GREEN + " in die Stadtkasse eingezahlt!");
        RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().broadcastCityMessage(city, conversation.getPlayer().getName()
                + " hat " + economy.getFormattedAmount(amount) + ChatColor.GOLD + " in die Stadtkasse eingezahlt!");

        changeStage(conversation, success);
    }

    private void changeStage(Conversation conversation, String stage) {

        if (stage != null) {
            conversation.setCurrentStage(stage);
            conversation.triggerCurrentStage();
        }
    }
}
