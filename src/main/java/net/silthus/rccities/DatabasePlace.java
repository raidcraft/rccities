package net.silthus.rccities;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.place.AbstractPlace;
import org.bukkit.Location;

public class DatabasePlace extends AbstractPlace {

    public DatabasePlace(City city, String friendlyName, Location location) {
        super(city, friendlyName, location);
    }

    @Override
    public void save() {
        // TODO
    }

    @Override
    public void delete() {
        // TODO
    }
}
