package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.RolePermission;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.conversation.Conversation;

import java.util.List;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "HAS_CITIZENSHIP")
public class HasCitizenshipAction extends AbstractAction {

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String success = args.getString("onsuccess", null);
        String failure = args.getString("onfailure", null);

        List<Resident> citizenships = RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                .getCitizenships(conversation.getPlayer().getUniqueId(), RolePermission.LEAVE);
        if (citizenships == null || citizenships.size() == 0) {
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
