package net.silthus.rccities.api.resident;

import lombok.Getter;
import lombok.Setter;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Philip Urban
 */
@Setter
@Getter
public abstract class AbstractResident implements Resident {

    protected int id;
    protected UUID playerId;
    protected Role profession;
    protected City city;

    protected AbstractResident() {

    }

    public AbstractResident(UUID playerID, Role profession, City city) {

        this.playerId = playerID;
        this.profession = profession;
        this.city = city;

        save();
    }

    @Override
    public void setRole(Role role) {

        this.profession = role;
        save();
    }

    @Override
    public Role getRole() {

        return profession;
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
        if (!playerId.equals(that.getPlayerId())) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = playerId.hashCode();
        result = 31 * result + city.hashCode();
        return result;
    }
}
