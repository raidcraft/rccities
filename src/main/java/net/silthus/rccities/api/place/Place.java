package net.silthus.rccities.api.place;

import net.silthus.rccities.api.city.City;
import org.bukkit.Location;

public interface Place {

    City getCity();

    String getFriendlyName();

    String getTechnicalName();

    Location getLocation();

    void save();

    void delete();
}
