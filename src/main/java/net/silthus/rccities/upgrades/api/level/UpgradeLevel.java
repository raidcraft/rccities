package net.silthus.rccities.upgrades.api.level;


import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import net.silthus.rccities.upgrades.api.requirement.Requirement;
import net.silthus.rccities.upgrades.api.reward.Reward;
import net.silthus.rccities.upgrades.api.unlockresult.UnlockResult;

import java.util.Collection;
import java.util.List;

/**
 * @author Philip Urban
 */
public interface UpgradeLevel<T> {

    UpgradeHolder<T> getUpgradeHolder();

    String getId();

    String getName();

    int getLevel();

    boolean isStored();

    void setRequirements(Collection<Requirement<T>> requirements);

    void setRewards(List<Reward<T>> rewards);

    List<String> getRequirementDescription();

    List<String> getRewardDescription();

    boolean isUnlocked();

    void setUnlocked(boolean unlocked);

    UnlockResult tryToUnlock(T object);
}
