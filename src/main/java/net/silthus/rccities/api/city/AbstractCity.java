package net.silthus.rccities.api.city;

import lombok.Getter;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import org.bukkit.Location;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Getter
public abstract class AbstractCity implements City {

    protected UUID id;
    protected String name;
    protected UUID creator;
    protected Timestamp creationDate;
    protected Location spawn;
    protected String description;
    protected int plotCredit;
    protected int exp;
    protected UpgradeHolder<City> upgradeHolder;
    protected double money;

    private void initBankAccount() {


    }

    protected AbstractCity() {
    }

    protected AbstractCity(String name, Location spawn, UUID creator) {

        this.name = name;
        this.spawn = spawn;
        this.creator = creator;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.plotCredit = RCCitiesPlugin.instance().getPluginConfig().getInitialPlotCredit();

        save();
    }

    @Override
    public String getFriendlyName() {

        return name.replace('_', ' ');
    }



    @Override
    public boolean hasMoney(double amount) {

        initBankAccount();
        return money >= amount;
    }

    @Override
    public boolean withdrawMoney(double amount) {

        initBankAccount();

        if(amount <= 0) return false;

        if(!hasMoney(amount)) return false;

        money -= amount;
        save();
        return true;
    }

    @Override
    public boolean depositMoney(double amount) {

        initBankAccount();

        if(amount <= 0) return false;

        money += amount;
        save();
        return true;
    }

    @Override
    public double getMoney() {

        initBankAccount();
        return money;
    }

    @Override
    public final void setSpawn(Location spawn) {

        this.spawn = spawn;
        save();
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
    public UpgradeHolder<City> getUpgrades() {

        return upgradeHolder;
    }

    @Override
    public int getMaxRadius() {

        // Calculate max radius by square root of number of available plots
        // (claimed and credit).
        // Add some extra space to allow some oval cities and not just
        // perfect circles.

        int additionalPlotLengths = RCCitiesPlugin.instance().getPluginConfig().getAdditionalRadiusPlots();
        additionalPlotLengths *= additionalPlotLengths;

        int maxRadius = (int)Math.sqrt(getSize() + getPlotCredit()
                + additionalPlotLengths) * 16;

        return maxRadius;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCity that = (AbstractCity) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }
}
