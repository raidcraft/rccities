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
import java.util.UUID;

/**
 * @author Philip Urban
 */
public class UpgradeManager {

    private final Set<String> createdUpgradeInfo = new HashSet<>();


    /**
     * Load and returns existing UpgradeHolder.
     *
     */
    public <O> UpgradeHolder<O> loadDatabaseUpgradeHolder(O object, ConfigurationSection holderConfig, UUID id, Class<O> clazz) {

        UpgradeHolder<O> upgradeHolder = new DatabaseUpgradeHolder<O>(object, holderConfig, id, clazz);
        createDatabaseUpgradeInfo(upgradeHolder);
        return upgradeHolder;
    }

    /**
     * Creates a new UpgradeHolder from given holder configuration and stores them in the database.
     *
     */
    public <O> UpgradeHolder createDatabaseUpgradeHolder(O object, ConfigurationSection holderConfig, Class<O> clazz) {

        UpgradeHolder<O> upgradeHolder = new DatabaseUpgradeHolder<O>(object, holderConfig, clazz);
        upgradeHolder.save();
        return upgradeHolder;
    }

    public void deleteUpgradeHolder(UUID id) {

        if(id == null) return;
        TUpgradeHolder tUpgradeHolder = TUpgradeHolder.find.byId(id);
        if(tUpgradeHolder != null) {
            tUpgradeHolder.delete();
        }
    }

    private <O> void createDatabaseUpgradeInfo(UpgradeHolder<O> upgradeHolder) {

        if(createdUpgradeInfo.contains(StringUtils.formatName(upgradeHolder.getName()))) return;
        createdUpgradeInfo.add(StringUtils.formatName(upgradeHolder.getName()));

        // delete existing
        List<TUpgradeInfo> tUpgradeInfos = TUpgradeInfo.find.query().where().ieq("holder_id", StringUtils.formatName(upgradeHolder.getName())).findList();
        tUpgradeInfos.forEach((tUpgradeInfo ->
                tUpgradeInfo.delete()));

        // create new
        for(Upgrade upgrade : upgradeHolder.getUpgrades()) {
            TUpgradeInfo tUpgradeInfo = new TUpgradeInfo();
            tUpgradeInfo.setHolderId(StringUtils.formatName(upgradeHolder.getName()));
            tUpgradeInfo.setHolderName(upgradeHolder.getName());
            tUpgradeInfo.setDescription(upgrade.getDescription());
            tUpgradeInfo.setName(upgrade.getName());
            tUpgradeInfo.save();

            // save level info
            for(UpgradeLevel level : upgrade.getLevels()) {

                TLevelInfo tLevelInfo = new TLevelInfo();
                tLevelInfo.setName(level.getName());
                tLevelInfo.setIdentifier(level.getId());
                tLevelInfo.setLevelNumber(level.getLevel());
                tLevelInfo.setUpgradeInfo(tUpgradeInfo);
                tLevelInfo.setRequirementDescription(Joiner.on("|").join(level.getRequirementDescription()));
                tLevelInfo.setRewardDescription(Joiner.on("|").join(level.getRewardDescription()));
                tLevelInfo.save();
            }
        }

    }
}
