package net.silthus.rccities.upgrades;

import com.google.common.base.Joiner;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import net.silthus.rccities.upgrades.tables.TLevelInfo;
import net.silthus.rccities.upgrades.tables.TUpgradeHolder;
import net.silthus.rccities.upgrades.tables.TUpgradeInfo;
import net.silthus.rccities.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Philip Urban
 */
public class UpgradeManager {

    private Set<String> createdUpgradeInfo = new HashSet<>();


    /**
     * Load and returns existing UpgradeHolder.
     *
     */
    public <O> UpgradeHolder<O> loadDatabaseUpgradeHolder(O object, ConfigurationSection holderConfig, int id, Class<O> clazz) {

        UpgradeHolder<O> upgradeHolder = new de.raidcraft.rcupgrades.DatabaseUpgradeHolder<O>(object, holderConfig, id, clazz);
        createDatabaseUpgradeInfo(upgradeHolder);
        return upgradeHolder;
    }

    /**
     * Creates a new UpgradeHolder from given holder configuration and stores them in the database.
     *
     */
    public <O> UpgradeHolder createDatabaseUpgradeHolder(O object, ConfigurationSection holderConfig, Class<O> clazz) {

        UpgradeHolder<O> upgradeHolder = new de.raidcraft.rcupgrades.DatabaseUpgradeHolder<O>(object, holderConfig, clazz);
        upgradeHolder.save();
        return upgradeHolder;
    }

    public void deleteUpgradeHolder(int id) {

        TUpgradeHolder tUpgradeHolder = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).find(TUpgradeHolder.class, id);
        RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).delete(tUpgradeHolder);
    }

    private <O> void createDatabaseUpgradeInfo(UpgradeHolder<O> upgradeHolder) {

        if(createdUpgradeInfo.contains(StringUtils.formatName(upgradeHolder.getName()))) return;
        createdUpgradeInfo.add(StringUtils.formatName(upgradeHolder.getName()));

        // delete existing
        List<TUpgradeInfo> tUpgradeInfos = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class)
                .find(TUpgradeInfo.class).where().ieq("holder_id", StringUtils.formatName(upgradeHolder.getName())).findList();
        RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).delete(tUpgradeInfos);

        // create new
        for(Upgrade upgrade : upgradeHolder.getUpgrades()) {
            TUpgradeInfo tUpgradeInfo = new TUpgradeInfo();
            tUpgradeInfo.setHolderId(StringUtils.formatName(upgradeHolder.getName()));
            tUpgradeInfo.setHolderName(upgradeHolder.getName());
            tUpgradeInfo.setDescription(upgrade.getDescription());
            tUpgradeInfo.setName(upgrade.getName());
            RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).save(tUpgradeInfo);

            // save level info
            for(UpgradeLevel level : upgrade.getLevels()) {

                TLevelInfo tLevelInfo = new TLevelInfo();
                tLevelInfo.setName(level.getName());
                tLevelInfo.setIdentifier(level.getId());
                tLevelInfo.setLevelNumber(level.getLevel());
                tLevelInfo.setUpgradeInfo(tUpgradeInfo);
                tLevelInfo.setRequirementDescription(Joiner.on("|").join(level.getRequirementDescription()));
                tLevelInfo.setRewardDescription(Joiner.on("|").join(level.getRewardDescription()));
                RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).save(tLevelInfo);
            }
        }

    }
}
