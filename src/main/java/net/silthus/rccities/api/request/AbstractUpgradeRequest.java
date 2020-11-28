package net.silthus.rccities.api.request;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;

/**
 * @author Philip Urban
 */
public abstract class AbstractUpgradeRequest extends AbstractRequest implements UpgradeRequest {

    protected City city;
    protected UpgradeLevel<City> upgradeLevel;
    protected String info;
    protected long rejectDate;
    protected boolean accepted;

    protected AbstractUpgradeRequest(City city, UpgradeLevel<City> upgradeLevel, String info) {

        this.city = city;
        this.upgradeLevel = upgradeLevel;
        this.info = info;
    }

    @Override
    public boolean isAccepted() {

        return accepted;
    }

    @Override
    public long getRejectExpirationDate() {

        return rejectDate + RaidCraft.getComponent(RCCitiesPlugin.class).getConfig().upgradeRequestCooldown * 60 * 1000;
    }

    @Override
    public UpgradeLevel<City> getUpgradeLevel() {

        return upgradeLevel;
    }

    @Override
    public City getCity() {

        return city;
    }

    @Override
    public String getInfo() {

        return info;
    }
}
