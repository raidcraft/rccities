package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.CityFlag;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.request.JoinRequest;
import de.raidcraft.rccities.flags.city.JoinCostsCityFlag;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "FIND_CITY")
public class FindCityAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        Plot plot = RaidCraft.getComponent(RCCitiesPlugin.class).getPlotManager().getPlot(conversation.getPlayer().getLocation().getChunk());
        if (plot == null) {
            changeStage(conversation, failure);
            return;
        }
        City city = plot.getCity();
        Economy economy = RaidCraft.getEconomy();

        conversation.set("city_friendlyname", city.getFriendlyName());
        conversation.set("city_name", city.getName());
        conversation.set("city_money", economy.getBalance(AccountType.CITY, city.getBankAccountName()));
        conversation.set("city_money_formatted", economy.getFormattedAmount(economy.getBalance(AccountType.CITY, city.getBankAccountName())));
        conversation.set("city_bankaccount", city.getBankAccountName());

        int openRequests = 0;
        for (JoinRequest joinRequest : city.getJoinRequests()) {
            if (!joinRequest.isRejected()) openRequests++;
        }
        conversation.set("city_open_requests", openRequests);

        double joinCosts = 0;
        CityFlag joinCostsCityFlag = RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().getCityFlag(city, JoinCostsCityFlag.class);
        if (joinCostsCityFlag != null) {
            joinCosts = joinCostsCityFlag.getType().convertToMoney(joinCostsCityFlag.getValue());
        }
        conversation.set("city_join_costs_friendly", economy.getFormattedAmount(joinCosts));
        conversation.set("city_join_costs", joinCosts);

        changeStage(conversation, success);
    }

    private void changeStage(Conversation conversation, String stage) {

        if (stage != null) {
            conversation.setCurrentStage(stage);
            conversation.triggerCurrentStage();
        }
    }
}
