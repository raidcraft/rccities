package net.silthus.rccities.api.city;

import net.silthus.rccities.api.request.JoinRequest;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.upgrades.api.holder.UpgradeHolder;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface City {

    UUID getId();

    String getName();

    String getFriendlyName();

    double getMoney();

    boolean hasMoney(double amount);

    boolean withdrawMoney(double amount);

    boolean depositMoney(double amount);

    UUID getCreator();

    Timestamp getCreationDate();

    Location getSpawn();

    void setSpawn(Location spawn);

    String getDescription();

    void setDescription(String description);

    int getPlotCredit();

    void setPlotCredit(int plotCredit);

    UpgradeHolder<City> getUpgrades();

    int getMaxRadius();

    int getExp();

    void addExp(int exp);

    void removeExp(int exp);

    int getSize();

    void setFlag(Player player, String flagName, String flagValue) throws RaidCraftException;

    void removeFlag(String flagName);

    void refreshFlags();

    List<Resident> getResidents();

    List<JoinRequest> getJoinRequests();

    JoinRequest getJoinRequest(UUID playerId);

    void sendJoinRequest(UUID playerId);

    void save();

    void delete();

    boolean equals(Object o);

    int hashCode();

}
