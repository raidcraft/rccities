package net.silthus.rccities.api.resident;

import net.silthus.rccities.api.city.City;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface Resident {

    UUID getId();

    UUID getPlayerId();

    String getName();

    Role getRole();

    void setRole(Role role);

    City getCity();

    Player getPlayer();

    boolean depositCity(double amount);

    boolean withdrawCity(double amount);

    void save();

    void delete();
}
