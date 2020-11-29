package net.silthus.rccities.api.city;

import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public abstract class AbstractCity implements City {

    protected int id;
    protected String name;
    protected UUID creator;
    protected Timestamp creationDate;
    protected Location spawn;
    protected String description;
    protected int plotCredit;
    protected int maxRadius;
    protected int exp;
    protected UpgradeHolder upgradeHolder;

    protected AbstractCity() {
    }

    protected AbstractCity(String name, Location spawn, UUID creator) {

        this.name = name;
        this.spawn = spawn;
        this.creator = creator;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.plotCredit = RCCitiesPlugin.getPlugin().getPluginConfig().getInitialPlotCredit();
        this.maxRadius = RCCitiesPlugin.getPlugin().getPluginConfig().getDefaultTownRadius();

        save();
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public final String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return name.replace('_', ' ');
    }

    @Override
    public String getBankAccountName() {

        return "bank_account_city_" + name;
    }

    @Override
    public final UUID getCreator() {

        return creator;
    }

    @Override
    public final Timestamp getCreationDate() {

        return creationDate;
    }

    @Override
    public final Location getSpawn() {

        return spawn;
    }

    @Override
    public final void setSpawn(Location spawn) {

        this.spawn = spawn;
        save();
    }

    @Override
    public final String getDescription() {

        return description;
    }

    @Override
    public final void setDescription(String description) {

        this.description = description;
        save();
    }

    @Override
    public void setPlotCredit(int plotCredit) {

        this.plotCredit = plotCredit;
        save();
    }

    @Override
    public int getPlotCredit() {

        return plotCredit;
    }

    @Override
    public void removeExp(int exp) {

        this.exp -= exp;
        save();
    }

    @Override
    public void addExp(int exp) {

        this.exp += exp;
        save();
    }

    @Override
    public int getExp() {

        return exp;
    }

    @Override
    public void setMaxRadius(int maxRadius) {

        this.maxRadius = maxRadius;
        save();
    }

    @Override
    public int getMaxRadius() {

        return maxRadius;
    }

    @Override
    public UpgradeHolder<City> getUpgrades() {

        return upgradeHolder;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCity that = (AbstractCity) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }
}
