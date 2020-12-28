package net.silthus.rccities.api.resident;

import de.raidcraft.economy.wrapper.Economy;
import lombok.Getter;
import lombok.Setter;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Philip Urban
 */
@Setter
@Getter
public abstract class AbstractResident implements Resident {

    protected UUID id;
    protected UUID playerId;
    protected Role profession;
    protected City city;
    protected double depositAmount;
    protected double withdrawAmount;

    protected AbstractResident() {

    }

    public AbstractResident(UUID playerID, Role profession, City city) {

        this.playerId = playerID;
        this.profession = profession;
        this.city = city;

        save();
    }

    @Override
    public void setRole(Role newRole) {

        boolean couldBuild = false;
        boolean shouldBuild = false;

        if(profession != null && profession.hasPermission(RolePermission.BUILD_EVERYWHERE)) {
            couldBuild = true;
        }

        if(newRole.hasPermission(RolePermission.BUILD_EVERYWHERE)) {
            shouldBuild = true;
        }

        // update region if build permissions changes
        if (couldBuild != shouldBuild) {
            for (Plot plot : RCCitiesPlugin.instance().getPlotManager().getPlots(city)) {
                plot.updateRegion(false);
            }
        }

        this.profession = newRole;
        save();
    }

    @Override
    public Role getRole() {

        return profession;
    }

    @Override
    public boolean depositCity(double amount) {

        if(!Economy.get().has(getPlayer(), amount)) {
            return false;
        }

        Economy.get().withdrawPlayer(getPlayer(), amount, "Einzahlung an " + city.getFriendlyName());
        city.depositMoney(amount);
        depositAmount += amount;
        save();
        return true;
    }

    @Override
    public boolean withdrawCity(double amount) {

        if(!city.hasMoney(amount)) {
            return false;
        }

        city.withdrawMoney(amount);
        Economy.get().depositPlayer(getPlayer(), amount, "Auszahlung von " + city.getFriendlyName());
        withdrawAmount += amount;
        save();
        return true;
    }

    @Override
    public String getName() {

        return Bukkit.getOfflinePlayer(playerId).getName();
    }


    @Override
    public Player getPlayer() {

        return Bukkit.getPlayer(playerId);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractResident that = (AbstractResident) o;

        if (!city.equals(that.getCity())) return false;
        return playerId.equals(that.getPlayerId());
    }

    @Override
    public int hashCode() {

        int result = playerId.hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}
