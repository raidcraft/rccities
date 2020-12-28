package net.silthus.rccities.api.city;

import de.raidcraft.economy.wrapper.Economy;
import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import net.silthus.rccities.util.StringUtils;
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
    protected int exp;
    protected UpgradeHolder<City> upgradeHolder;
    protected double money;
    private boolean bankInitialized = false;
    private String bankAccountName;
    private final static double BANK_ACCOUNT_CONVERTED_PATTERN = -1337.1337;

    private void initBankAccount() {
        if(bankInitialized) return;

        bankInitialized = true;

        bankAccountName = "city_" + getTechnicalName();

        // Check if bank account does no exists
        if(Economy.get().bankBalance(bankAccountName).type == EconomyResponse.ResponseType.FAILURE) {

            // There is no API to create bank account without any player assignment.
            // Therefore take Strasse36 (my) user account as bank owner to avoid triggering
            // web request to search for not existing user by "getOfflinePlayer" method.
            UUID ownerUUID = UUID.fromString("78e15490-cfb7-4d9c-84ea-78390aac7952");
            if(Economy.get().createBank(bankAccountName, Bukkit.getOfflinePlayer(ownerUUID)).type
                    == EconomyResponse.ResponseType.SUCCESS) {
                Economy.get().bankDeposit(bankAccountName, money);
                money = BANK_ACCOUNT_CONVERTED_PATTERN; // Mark this city bank account as converted
                save();
            }
        }
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
    public String getTechnicalName() {

        String fixedCityName = getFriendlyName().toLowerCase();
        fixedCityName = fixedCityName.replace(" ", "_");
        fixedCityName = StringUtils.replaceUmlaut(fixedCityName);

        return fixedCityName;
    }

    @Override
    public boolean hasMoney(double amount) {

        initBankAccount();

        if(money != BANK_ACCOUNT_CONVERTED_PATTERN) {
            return money >= amount;
        } else {
            if(Economy.get().bankHas(bankAccountName, amount).type == EconomyResponse.ResponseType.SUCCESS) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean withdrawMoney(double amount) {

        initBankAccount();

        if(amount <= 0) return false;

        if(!hasMoney(amount)) return false;

        if(money != BANK_ACCOUNT_CONVERTED_PATTERN) {
            money -= amount;
            save();
            return true;
        } else {
            EconomyResponse response = Economy.get().bankWithdraw(bankAccountName, amount);
            if(response.type == EconomyResponse.ResponseType.SUCCESS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean depositMoney(double amount) {

        initBankAccount();

        if(amount <= 0) return false;

        if(money != BANK_ACCOUNT_CONVERTED_PATTERN) {
            money += amount;
            save();
            return true;
        } else {
            EconomyResponse response = Economy.get().bankDeposit(bankAccountName, amount);
            if(response.type == EconomyResponse.ResponseType.SUCCESS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getMoney() {

        initBankAccount();

        if(money != BANK_ACCOUNT_CONVERTED_PATTERN) {
            return money;
        } else {
            EconomyResponse response = Economy.get().bankBalance(bankAccountName);
            if(response.type == EconomyResponse.ResponseType.SUCCESS) {
                return response.balance;
            }
        }

        return 0;
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
