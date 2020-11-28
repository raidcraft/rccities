package net.silthus.rccities.api.request;

import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;

/**
 * @author Philip Urban
 */
public interface UpgradeRequest extends Request {

    City getCity();

    UpgradeLevel<City> getUpgradeLevel();

    long getRejectExpirationDate();

    boolean isAccepted();

    String getInfo();

    void save();

    void delete();
}
