package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.util.UUIDUtil;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "FIND_CITY_RESIDENT")
public class FindCityResidentAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String residentName = args.getString("resident");
        residentName = ParseString.INST.parse(conversation, residentName);
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        Resident resident = RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                .getResident(UUIDUtil.convertPlayer(residentName), city);
        if (resident == null) {
            changeStage(conversation, failure);
            return;
        } else {

            conversation.set("resident_name", resident.getName());
            changeStage(conversation, success);
            return;
        }
    }

    private void changeStage(Conversation conversation, String stage) {

        if (stage != null) {
            conversation.setCurrentStage(stage);
            conversation.triggerCurrentStage();
        }
    }
}
