package net.silthus.rccities.upgrades.events;

import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.unlockresult.UnlockResult;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class UpgradeUnlockEvent extends Event implements Cancellable {

    private final UpgradeLevel upgradeLevel;
    private final UnlockResult unlockResult;
    private final Object object;
    private boolean cancelled;

    public UpgradeUnlockEvent(UpgradeLevel upgradeLevel, UnlockResult unlockResult, Object object) {

        this.upgradeLevel = upgradeLevel;
        this.unlockResult = unlockResult;
        this.object = object;
        this.cancelled = false;
    }

    public UpgradeLevel getUpgradeLevel() {

        return upgradeLevel;
    }

    public UnlockResult getUnlockResult() {

        return unlockResult;
    }

    public Object getObject() {

        return object;
    }

    /*///////////////////////////////////////////////////
    //              Needed Bukkit Stuff
    ///////////////////////////////////////////////////*/

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

    @Override
    public boolean isCancelled() {

        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {

        this.cancelled = cancel;
    }
}
