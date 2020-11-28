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
import de.raidcraft.rcconversations.util.ParseString;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "HAS_CITY_PERMISSION")
public class HasRolePermissionAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);
        String permission = args.getString("permission");

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        Resident resident = RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                .getResident(conversation.getPlayer().getUniqueId(), city);
        if (resident == null) {
            changeStage(conversation, failure);
            return;
        }

        RolePermission rolePermission;
        try {
            rolePermission = RolePermission.valueOf(permission.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Role Permission '" + permission + "' does not exist!");
        }

        if (resident.getRole().hasPermission(rolePermission)) {
            changeStage(conversation, success);
        } else {
            changeStage(conversation, failure);
        }
    }

    private void changeStage(Conversation conversation, String stage) {

        if (stage != null) {
            conversation.setCurrentStage(stage);
            conversation.triggerCurrentStage();
        }
    }
}
