package net.silthus.rccities.conversation;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rcconversations.actions.common.StageAction;
import de.raidcraft.rcconversations.actions.variables.SetVariableAction;
import de.raidcraft.rcconversations.api.action.AbstractAction;
import de.raidcraft.rcconversations.api.action.ActionArgumentList;
import de.raidcraft.rcconversations.api.action.ActionInformation;
import de.raidcraft.rcconversations.api.action.WrongArgumentValueException;
import de.raidcraft.rcconversations.api.answer.Answer;
import de.raidcraft.rcconversations.api.answer.SimpleAnswer;
import de.raidcraft.rcconversations.api.conversation.Conversation;
import de.raidcraft.rcconversations.api.stage.SimpleStage;
import de.raidcraft.rcconversations.api.stage.Stage;
import de.raidcraft.rcconversations.util.ParseString;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
@ActionInformation(name = "LIST_CITY_UPGRADE_LEVEL")
public class ListUpgradeLevelAction extends AbstractAction {

    private static final int MAX_PLACES_PER_STAGE = 4;

    @Override
    public void run(Conversation conversation, ActionArgumentList args) throws RaidCraftException {

        String cityName = args.getString("city");
        cityName = ParseString.INST.parse(conversation, cityName);
        String text = args.getString("text");
        text = ParseString.INST.parse(conversation, text);
        String upgradeType = args.getString("upgrade-type");
        upgradeType = ParseString.INST.parse(conversation, upgradeType);
        String nextStage = args.getString("next-stage", "next");
        String varName = args.getString("var", "city_level");
        int pageSize = args.getInt("pagesize", MAX_PLACES_PER_STAGE);

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(cityName);
        if (city == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': City '" + cityName + "' does not exist!");
        }

        Upgrade upgrade = city.getUpgrades().getUpgrade(upgradeType);
        if (upgrade == null) {
            throw new WrongArgumentValueException("Wrong argument value in action '" + getName() + "': Upgrade '" + upgradeType + "' does not exist!");
        }

        List<UpgradeLevel> levels = upgrade.getLevels();
        // delete not reachable levels
        UpgradeLevel highestLockedLevel = upgrade.getLowestLockedLevel();
        for (UpgradeLevel level : new ArrayList<>(levels)) {
            if (highestLockedLevel != null && level.getLevel() > highestLockedLevel.getLevel() && level.isStored()) levels.remove(level);
        }
        String entranceStage = "city_levels_";


        int pages = (int) Math.ceil(((double) levels.size() / (double) pageSize));
        if (pages == 0) pages = 1;
        for (int i = 0; i < pages; i++) {

            Stage stage;
            List<Answer> answers = new ArrayList<>();

            int a;

            for (a = 0; a < pageSize; a++) {
                if (levels.size() <= a + (i * pageSize)) break;
                answers.add(createAnswer(conversation.getPlayer(), a, levels.get(i * pageSize + a), nextStage, varName));
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

    private Answer createAnswer(Player player, int number, UpgradeLevel level, String nextStage, String varName) {

        List<ActionArgumentList> actions = new ArrayList<>();
        int i = 0;
        Map<String, Object> data = new HashMap<>();
        data.put("variable", varName);
        data.put("value", level.getId());
        data.put("local", true);
        Map<String, Object> data2 = new HashMap<>();
        data2.put("variable", varName + "_name");
        data2.put("value", level.getName());
        data2.put("local", true);
        actions.add(new ActionArgumentList(String.valueOf(i++), SetVariableAction.class, data));
        actions.add(new ActionArgumentList(String.valueOf(i++), SetVariableAction.class, data2));
        actions.add(new ActionArgumentList(String.valueOf(i++), StageAction.class, "stage", nextStage));

        String crossed = (level.isUnlocked() && level.isStored()) ? ChatColor.RED + ChatColor.STRIKETHROUGH.toString() : ChatColor.GREEN.toString();
        return new SimpleAnswer(String.valueOf(number + 1), crossed + level.getName(), actions);
    }
}
