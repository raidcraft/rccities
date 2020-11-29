package net.silthus.rccities.upgrades.api.holder;


import net.silthus.rccities.upgrades.api.upgrade.Upgrade;

import java.util.List;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface UpgradeHolder<T> {

    UUID getId();

    String getName();

    String getDescription();

    T getObject();

    Upgrade getUpgrade(String id);

    List<Upgrade> getUpgrades();

    void save();

    Class<T> getType();
}
