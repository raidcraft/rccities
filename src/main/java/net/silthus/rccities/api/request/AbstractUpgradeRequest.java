package net.silthus.rccities.api.request;


import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;

/**
 * @author Philip Urban
 */
public abstract class AbstractUpgradeRequest extends AbstractRequest implements UpgradeRequest {

    protected RCCitiesPlugin plugin;
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

        return rejectDate + RCCitiesPlugin.getPlugin().getPluginConfig().getUpgradeRequestCooldown() * 60 * 1000;
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
