package net.silthus.rccities.upgrades.api.holder;

import net.silthus.rccities.RCCities;
import net.silthus.rccities.upgrades.RequirementManager;
import net.silthus.rccities.upgrades.api.level.SimpleUpgradeLevel;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.requirement.Requirement;
import net.silthus.rccities.upgrades.api.reward.Reward;
import net.silthus.rccities.upgrades.api.reward.RewardManager;
import net.silthus.rccities.upgrades.api.upgrade.SimpleUpgrade;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Philip Urban
 */
public abstract class ConfigurationUpgradeHolder<T> extends AbstractUpgradeHolder<T> {

    protected ConfigurationUpgradeHolder(T object, ConfigurationSection config, Class<T> clazz) {

        super(object, clazz);

        Logger logger = RCCities.instance().getLogger();

        this.name = config.getString("name");
        this.description = config.getString("description");

        //logger.info("Loading configuration upgrade holder: " + this.name);

        ConfigurationSection upgradesSection = config.getConfigurationSection("upgrades");
        if(upgradesSection == null) return;

        for(String key : upgradesSection.getKeys(false)) {
            ConfigurationSection upgradeSection = upgradesSection.getConfigurationSection(key);
            if(upgradeSection == null) continue;
            String name = upgradeSection.getString("name");
            String description = upgradeSection.getString("description");

            //logger.info("Loading upgrade section: " + name);

            ConfigurationSection levels = upgradeSection.getConfigurationSection("level");
            Upgrade upgrade = new SimpleUpgrade(key, name, description);
            if(levels != null) {
                for(String levelIdentifier : levels.getKeys(false)) {
                    ConfigurationSection level = levels.getConfigurationSection(levelIdentifier);
                    if(level == null) continue;
                    String levelName = level.getString("name");
                    boolean stored = level.getBoolean("stored", true);
                    int levelNumber = level.getInt("level", 0);
                    List<String> requirementDescription = level.getStringList("requirement-desc");
                    List<String> rewardDescription = level.getStringList("reward-desc");

                    logger.info("Loading level: " + levelName);

                    UpgradeLevel<T> upgradeLevel = new SimpleUpgradeLevel(this, levelIdentifier,
                            levelNumber, levelName, stored, requirementDescription, rewardDescription);

                    // requirements
                    ConfigurationSection requirements = level.getConfigurationSection("requirements");
                    List<Requirement<T>> requirementList = RequirementManager.createRequirements(requirements);

                    //logger.info("Loaded " + requirementList.size() + " requirements for level "
                    //        + upgradeLevel.getName());

                    upgradeLevel.setRequirements(requirementList);

                    // rewards
                    ConfigurationSection rewards = level.getConfigurationSection("rewards");
                    List<Reward<T>> rewardsList = RewardManager.createRewards(rewards);

                    //logger.info("Loaded " + rewardsList.size() + " rewards for level "
                    //        + upgradeLevel.getName());

                    upgradeLevel.setRewards(rewardsList);

                    upgrade.addLevel(upgradeLevel);
                }
            }

            upgrades.put(upgrade.getId(), upgrade);
        }
    }
}
