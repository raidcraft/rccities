package net.silthus.rccities.api.plot;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Philip Urban
 */
public interface Plot {

    int getId();

    String getRegionName();

    Location getLocation();

    ProtectedRegion getRegion();

    City getCity();

    List<Resident> getAssignedResidents();

    void assignResident(Resident resident);

    void removeResident(Resident resident);

    void setFlag(Player player, String flagName, String flagValue) throws RaidCraftException;

    void removeFlag(String flagName);

    void refreshFlags();

    void save();

    void updateRegion(boolean create);

    void delete();
}
