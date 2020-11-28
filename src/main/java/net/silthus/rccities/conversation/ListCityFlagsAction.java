package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rcconversations.actions.common.StageAction;
import de.raidcraft.rcconversations.actions.variables.SetVariableAction;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.answer.Answer;
import de.raidcraft.rcconversations.api.answer.SimpleAnswer;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.api.stage.SimpleStage;
import de.raidcraft.rcconversations.api.stage.Stage;
import de.raidcraft.rcconversations.util.ParseString;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "LIST_CITY_FlAGS")
public class ListCityFlagsAction extends AbstractAction {

    private static final int MAX_PLACES_PER_STAGE = 4;

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String text = args.getString("text");
        text = ParseString.INST.parse(conversation, text);
        String nextStage = args.getString("next-stage", "next");
        String varName = args.getString("var", "city_flag");
        boolean showAdminFlags = args.getBoolean("admin-flags", false);
        int pageSize = args.getInt("pagesize", MAX_PLACES_PER_STAGE);

        List<FlagInformation> flagInformationList = RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().getRegisteredCityFlagInformationList();
        for (FlagInformation flagInformation : new ArrayList<>(flagInformationList)) {
            if (flagInformation.adminOnly() && !showAdminFlags) flagInformationList.remove(flagInformation);
        }

        String entranceStage = "city_flags_";


        int pages = (int) Math.ceil(((double) flagInformationList.size() / (double) pageSize));
        if (pages == 0) pages = 1;
        for (int i = 0; i < pages; i++) {

            Stage stage;
            List<Answer> answers = new ArrayList<>();

            int a;

            for (a = 0; a < pageSize; a++) {
                if (flagInformationList.size() <= a + (i * pageSize)) break;
                answers.add(createAnswer(conversation.getPlayer(), a, flagInformationList.get(i * pageSize + a), nextStage, varName));
            }
            a++;

            String nextDynamicStage;
            if (pages - 1 == i) {
                nextDynamicStage = entranceStage;
            } else {
                nextDynamicStage = entranceStage + "_" + (i + 1);
            }
            String thisStage;
            if (i == 0) {
                thisStage = entranceStage;
            } else {
                thisStage = entranceStage + "_" + i;
            }

            if (pages > 1) {
                answers.add(new SimpleAnswer(String.valueOf(a), "&7Nächste Seite", new ActionArgumentList(String.valueOf(a), StageAction.class, "stage", nextDynamicStage)));
            }
            stage = new SimpleStage(thisStage, text + "|&7(Seite " + (i + 1) + "/" + pages + ")", answers);

            conversation.addStage(stage);
        }
        conversation.setCurrentStage(entranceStage);
        conversation.triggerCurrentStage();
    }

    private Answer createAnswer(Player player, int number, FlagInformation flagInformation, String nextStage, String varName) {

        List<ActionArgumentList> actions = new ArrayList<>();
        int i = 0;
        Map<String, Object> data = new HashMap<>();
        data.put("variable", varName);
        data.put("value", flagInformation.name());
        data.put("local", true);
        Map<String, Object> data2 = new HashMap<>();
        data2.put("variable", varName + "_friendlyname");
        data2.put("value", flagInformation.friendlyName());
        data2.put("local", true);
        actions.add(new ActionArgumentList(String.valueOf(i++), SetVariableAction.class, data));
        actions.add(new ActionArgumentList(String.valueOf(i++), SetVariableAction.class, data2));
        actions.add(new ActionArgumentList(String.valueOf(i++), StageAction.class, "stage", nextStage));

        return new SimpleAnswer(String.valueOf(number + 1), flagInformation.friendlyName(), actions);
    }
}
