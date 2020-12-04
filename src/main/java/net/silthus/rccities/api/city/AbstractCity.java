package net.silthus.rccities.api.city;

import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import org.bukkit.Bukkit;
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
    public String getFriendlyName() {

        return name.replace('_', ' ');
    }

    @Override
    public String getBankAccountName() {

        return "city_" + name.toLowerCase();
    }

    @Override
    public boolean hasEnoughMoney(double amount) {
        return RCCitiesPlugin.getPlugin().getEconomy().has(Bukkit.getOfflinePlayer(getBankAccountName()), amount);
    }

    @Override
    public boolean withdrawMoney(double amount) {
        return RCCitiesPlugin.getPlugin().getEconomy().withdrawPlayer(
                Bukkit.getOfflinePlayer(getBankAccountName()), amount).type == EconomyResponse.ResponseType.SUCCESS;
    }

    @Override
    public boolean depositMoney(double amount) {
        return RCCitiesPlugin.getPlugin().getEconomy().depositPlayer(
                Bukkit.getOfflinePlayer(getBankAccountName()), amount).type == EconomyResponse.ResponseType.SUCCESS;
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
    public void setMaxRadius(int maxRadius) {

        this.maxRadius = maxRadius;
        save();
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

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }
}
