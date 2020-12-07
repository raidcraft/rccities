package net.silthus.rccities.upgrades;

import net.silthus.rccities.upgrades.api.holder.ConfigurationUpgradeHolder;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import net.silthus.rccities.upgrades.tables.TUpgrade;
import net.silthus.rccities.upgrades.tables.TUpgradeHolder;
import net.silthus.rccities.upgrades.tables.TUpgradeLevel;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public class DatabaseUpgradeHolder<T> extends ConfigurationUpgradeHolder<T> {

    public DatabaseUpgradeHolder(T object, ConfigurationSection config, Class<T> clazz) {

        super(object, config, clazz);
    }

    public DatabaseUpgradeHolder(T object, ConfigurationSection config, UUID id, Class<T> clazz) {

        super(object, config, clazz);
        this.id = id;
        load();
    }

    @Override
    public void save() {

        // save new
        if(getId() == null) {

            //save holder
            TUpgradeHolder tUpgradeHolder = new TUpgradeHolder();
            tUpgradeHolder.setName(getName());
            tUpgradeHolder.save();
            id = tUpgradeHolder.id();
        }
        // update
        else {

            // get holder
            TUpgradeHolder tUpgradeHolder = TUpgradeHolder.find.byId(getId());

            // save upgrades
            for(Upgrade upgrade : getUpgrades()) {
                TUpgrade tUpgrade = TUpgrade.find.query().where().eq("holder_id", tUpgradeHolder.id())
                        .ieq("name", upgrade.getId()).findOne();

                // save upgrade if not exist
                if(tUpgrade == null) {
                    tUpgrade = new TUpgrade();
                    tUpgrade.setName(upgrade.getId());
                    tUpgrade.setHolder(tUpgradeHolder);
                    tUpgrade.save();
                }

                // save levels
                for(UpgradeLevel level : upgrade.getLevels()) {
                    if(!level.isStored()) continue;
                    TUpgradeLevel tUpgradeLevel = TUpgradeLevel
                            .find.query().where().eq("upgrade_id", tUpgrade.id())
                            .ieq("identifier", level.getId()).findOne();

                    // save if not exist
                    if(tUpgradeLevel == null) {
                        tUpgradeLevel = new TUpgradeLevel();
                        tUpgradeLevel.setIdentifier(level.getId());
                        tUpgradeLevel.setUpgrade(tUpgrade);
                        tUpgradeLevel.setUnlocked(level.isUnlocked());
                        tUpgradeLevel.save();
                    }
                    // otherwise update
                    else {
                        tUpgradeLevel.setUnlocked(level.isUnlocked());
                        tUpgradeLevel.update();
                    }
                }
            }
        }
    }

    private void load() {

        if(id == null) return;

        // get holder
        TUpgradeHolder tUpgradeHolder = TUpgradeHolder.find.byId(getId());
        if(tUpgradeHolder == null) return;

        // load upgrades
        for(Upgrade upgrade : getUpgrades()) {
            TUpgrade tUpgrade = TUpgrade.find.query().where().eq("holder_id", tUpgradeHolder.id())
                    .ieq("name", upgrade.getId()).findOne();

            if(tUpgrade == null) continue;

            // load levels
            for(UpgradeLevel level : upgrade.getLevels()) {
                TUpgradeLevel tUpgradeLevel = TUpgradeLevel.find.query().where()
                        .eq("upgrade_id", tUpgrade.id()).eq("identifier", level.getId()).findOne();

                if(tUpgradeLevel == null) continue;

                level.setUnlocked(tUpgradeLevel.isUnlocked());
            }
        }
    }
}
