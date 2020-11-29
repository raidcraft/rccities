package net.silthus.rccities.upgrades.api.level;

import net.silthus.rccities.requirement.Requirement;
import net.silthus.rccities.requirementsapi.reward.Reward;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import net.silthus.rccities.upgrades.api.unlockresult.UnlockResult;
import net.silthus.rccities.upgrades.events.UpgradeUnlockEvent;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.List;

/**
 * @author Philip Urban
 */
public class SimpleUpgradeLevel<T> extends AbstractUpgradeLevel<T> {

    private Collection<Requirement<T>> requirements;
    private List<Reward<T>> rewards;
    private UnlockResult unlockResult;

    public SimpleUpgradeLevel(UpgradeHolder<T> upgradeHolder, String id, int level, String name, boolean stored, List<String> requirementDescription, List<String> rewardDescription) {

        super(upgradeHolder, id, level, name, stored, false, requirementDescription, rewardDescription);
        this.unlockResult = new UnlockResult();
    }

    @Override
    public void setRequirements(Collection<Requirement<T>> requirements) {

        this.requirements = requirements;

        for(Requirement<T> req: requirements) {
            if(!req.getDescription(getUpgradeHolder().getObject()).isPresent()) continue;
            addRequirementDescription(req.getDescription(getUpgradeHolder().getObject()).get());
        }
    }



    @Override
    public void setRewards(List<Reward<T>> rewards) {

        this.rewards = rewards;

        for(Reward reward : rewards) {
            if(reward.getDescription() == null || reward.getDescription().isEmpty()) continue;
            addRewardDescription(reward.getDescription());
        }
    }

    public <T> boolean isMeetingAllRequirements(T object) {

        for(Requirement requirement : requirements) {

            if(!requirement.test(object)) {
                unlockResult.setSuccessful(false);
                if(requirement instanceof ReasonableRequirement)
                    unlockResult.setLongReason(((ReasonableRequirement) requirement).getReason(object));
                return false;
            }
        }
        unlockResult.setSuccessful(true);
        unlockResult.clearReasons();
        return true;
    }

    @Override
    public UnlockResult tryToUnlock(T object) {

        if (isMeetingAllRequirements(object)) {

            UpgradeUnlockEvent event = new UpgradeUnlockEvent(this, unlockResult, object);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                unlockResult.setSuccessful(false);
                unlockResult.setLongReason("Unlock was cancelled by plugin!");
                return unlockResult;
            }

            // reward
            for (Reward reward : rewards) {
                reward.reward(object);
            }
            // save
            setUnlocked(true);
            getUpgradeHolder().save();
        }
        return unlockResult;
    }
}
