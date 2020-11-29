package net.silthus.rccities.upgrades.api.level;


import lombok.Getter;
import lombok.Setter;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;

import java.util.List;

/**
 * @author Philip Urban
 */
@Getter
@Setter
public abstract class AbstractUpgradeLevel<T> implements UpgradeLevel<T> {

    private UpgradeHolder<T> upgradeHolder;
    private String id;
    private String name;
    private boolean stored;
    private boolean unlocked;
    private List<String> requirementDescription;
    private List<String> rewardDescription;
    private int level;

    protected AbstractUpgradeLevel(UpgradeHolder<T> upgradeHolder, String id, int level, String name, boolean stored, boolean unlocked, List<String> requirementDescription, List<String> rewardDescription) {

        this.upgradeHolder = upgradeHolder;
        this.id = id;
        this.name = name;
        this.stored = stored;
        this.unlocked = unlocked;
        this.requirementDescription = requirementDescription;
        this.rewardDescription = rewardDescription;
        this.level = level;
    }

    protected void addRequirementDescription(String description) {

        requirementDescription.add(description);
    }

    protected void addRewardDescription(String description) {

        rewardDescription.add(description);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractUpgradeLevel that = (AbstractUpgradeLevel) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        return id.hashCode();
    }
}
