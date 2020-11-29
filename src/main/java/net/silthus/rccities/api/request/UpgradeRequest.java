package net.silthus.rccities.api.request;


import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;

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
