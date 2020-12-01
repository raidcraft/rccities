package net.silthus.rccities.upgrades.api.holder;

import net.silthus.rccities.upgrades.api.reward.Reward;
import net.silthus.rccities.upgrades.api.reward.RewardManager;
import net.silthus.rccities.upgrades.RequirementManager;
import net.silthus.rccities.upgrades.api.level.SimpleUpgradeLevel;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.requirement.Requirement;
import net.silthus.rccities.upgrades.api.upgrade.SimpleUpgrade;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * @author Philip Urban
 */
public abstract class ConfigurationUpgradeHolder<T> extends AbstractUpgradeHolder<T> {

    protected ConfigurationUpgradeHolder(T object, ConfigurationSection config, Class<T> clazz) {

        super(object, clazz);
        this.name = config.getString("name");
        this.description = config.getString("description");

        ConfigurationSection upgradesSection = config.getConfigurationSection("upgrades");
        if(upgradesSection == null) return;

        for(String key : upgradesSection.getKeys(false)) {
            ConfigurationSection upgradeSection = upgradesSection.getConfigurationSection(key);
            String name = upgradeSection.getString("name");
            String description = upgradeSection.getString("description");

            ConfigurationSection levels = upgradeSection.getConfigurationSection("level");
            Upgrade upgrade = new SimpleUpgrade(key, name, description);
            if(levels != null) {
                for(String levelIdentifier : levels.getKeys(false)) {
                    ConfigurationSection level = levels.getConfigurationSection(levelIdentifier);
                    String levelName = level.getString("name");
                    boolean stored = level.getBoolean("stored", true);
                    int levelNumber = level.getInt("level", 0);
                    List<String> requirementDescription = level.getStringList("requirement-desc");
                    List<String> rewardDescription = level.getStringList("reward-desc");

                    UpgradeLevel<T> upgradeLevel = new SimpleUpgradeLevel(this, levelIdentifier,
                            levelNumber, levelName, stored, requirementDescription, rewardDescription);

                    // requirements
                    ConfigurationSection requirements = level.getConfigurationSection("requirements");
                    List<Requirement<T>> requirementList = RequirementManager.createRequirements(requirements);
//                    RaidCraft.LOGGER.info("[RCUpgrades] Es wurden " + requirementList.size() +
//                    " Requirements für das Upgrade-Level " + upgradeLevel.getName() + " geladen!");
                    upgradeLevel.setRequirements(requirementList);

                    // rewards
                    ConfigurationSection rewards = level.getConfigurationSection("rewards");
                    List<Reward<T>> rewardsList = RewardManager.createRewards(rewards);
                    upgradeLevel.setRewards(rewardsList);

                    upgrade.addLevel(upgradeLevel);
                }
            }

            upgrades.put(upgrade.getId(), upgrade);
        }
    }
}
