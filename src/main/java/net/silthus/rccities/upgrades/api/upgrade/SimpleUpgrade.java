package net.silthus.rccities.upgrades.api.upgrade;

import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.util.CaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class SimpleUpgrade extends AbstractUpgrade {

    private Map<String, UpgradeLevel> levels = new CaseInsensitiveMap<>();

    public SimpleUpgrade(String id, String name, String description) {

        super(id, name, description);
    }

    @Override
    public void setLevels(List<UpgradeLevel> levels) {

        for(UpgradeLevel upgradeLevel : levels) {
            this.levels.put(upgradeLevel.getId(), upgradeLevel);
        }
    }

    @Override
    public void addLevel(UpgradeLevel level) {

        this.levels.put(level.getId(), level);
    }

    @Override
    public UpgradeLevel getLowestLockedLevel() {

        UpgradeLevel upgradeLevel = null;
        for(UpgradeLevel level : levels.values()) {
            if(level.isUnlocked()) continue;
            if(upgradeLevel == null || level.getLevel() < upgradeLevel.getLevel()) upgradeLevel = level;
        }
        return upgradeLevel;
    }

    @Override
    public UpgradeLevel getHighestUnlockedLevel() {

        UpgradeLevel upgradeLevel = null;
        for(UpgradeLevel level : levels.values()) {
            if(!level.isUnlocked()) continue;
            if(upgradeLevel == null || level.getLevel() > upgradeLevel.getLevel()) upgradeLevel = level;
        }
        return upgradeLevel;
    }

    @Override
    public UpgradeLevel getLevel(String id) {

        return levels.get(id);
    }

    @Override
    public UpgradeLevel getLevel(int level) {

        for(UpgradeLevel upgradeLevel : levels.values()) {
            if(upgradeLevel.getLevel() == level) return upgradeLevel;
        }
        return null;
    }

    @Override
    public List<UpgradeLevel> getLevels() {

        if(levels.values() == null || levels.values().size() == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(levels.values());
    }
}
