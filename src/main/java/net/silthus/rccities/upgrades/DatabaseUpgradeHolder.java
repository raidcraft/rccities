package net.silthus.rccities.upgrades;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcupgrades.api.holder.ConfigurationUpgradeHolder;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;
import de.raidcraft.rcupgrades.tables.TUpgrade;
import de.raidcraft.rcupgrades.tables.TUpgradeHolder;
import de.raidcraft.rcupgrades.tables.TUpgradeLevel;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Philip Urban
 */
public class DatabaseUpgradeHolder<T> extends ConfigurationUpgradeHolder<T> {

    public DatabaseUpgradeHolder(T object, ConfigurationSection config, Class<T> clazz) {

        super(object, config, clazz);
    }

    public DatabaseUpgradeHolder(T object, ConfigurationSection config, int id, Class<T> clazz) {

        super(object, config, clazz);
        this.id = id;
        load();
    }

    @Override
    public void save() {

        // save new
        if(getId() == 0) {

            //save holder
            TUpgradeHolder tUpgradeHolder = new TUpgradeHolder();
            tUpgradeHolder.setName(getName());
            RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).save(tUpgradeHolder);
            id = tUpgradeHolder.getId();
        }
        // update
        else {

            // get holder
            TUpgradeHolder tUpgradeHolder = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).find(TUpgradeHolder.class, getId());

            // save upgrades
            for(Upgrade upgrade : getUpgrades()) {
                TUpgrade tUpgrade = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).find(TUpgrade.class).where().eq("holder_id", tUpgradeHolder.getId()).ieq("name", upgrade.getId()).findUnique();

                // save upgrade if not exist
                if(tUpgrade == null) {
                    tUpgrade = new TUpgrade();
                    tUpgrade.setName(upgrade.getId());
                    tUpgrade.setHolder(tUpgradeHolder);
                    RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).save(tUpgrade);
                }

                // save levels
                for(UpgradeLevel level : upgrade.getLevels()) {
                    if(!level.isStored()) continue;
                    TUpgradeLevel tUpgradeLevel = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class)
                            .find(TUpgradeLevel.class).where().eq("upgrade_id", tUpgrade.getId()).ieq("identifier", level.getId()).findUnique();

                    // save if not exist
                    if(tUpgradeLevel == null) {
                        tUpgradeLevel = new TUpgradeLevel();
                        tUpgradeLevel.setIdentifier(level.getId());
                        tUpgradeLevel.setUpgrade(tUpgrade);
                        tUpgradeLevel.setUnlocked(level.isUnlocked());
                        RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).save(tUpgradeLevel);
                    }
                    // otherwise update
                    else {
                        tUpgradeLevel.setUnlocked(level.isUnlocked());
                        RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).update(tUpgradeLevel);
                    }
                }
            }
        }
    }

    private void load() {

        if(id == 0) return;

        // get holder
        TUpgradeHolder tUpgradeHolder = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).find(TUpgradeHolder.class, getId());

        // load upgrades
        for(Upgrade upgrade : getUpgrades()) {
            TUpgrade tUpgrade = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class).find(TUpgrade.class).where().eq("holder_id", tUpgradeHolder.getId()).ieq("name", upgrade.getId()).findUnique();

            if(tUpgrade == null) continue;

            // load levels
            for(UpgradeLevel level : upgrade.getLevels()) {
                TUpgradeLevel tUpgradeLevel = RaidCraft.getDatabase(de.raidcraft.rcupgrades.RCUpgradesPlugin.class)
                        .find(TUpgradeLevel.class).where().eq("upgrade_id", tUpgrade.getId()).eq("identifier", level.getId()).findUnique();

                if(tUpgradeLevel == null) continue;

                level.setUnlocked(tUpgradeLevel.isUnlocked());
            }
        }
    }
}
