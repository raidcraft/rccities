package net.silthus.rccities.api.place;

import lombok.Getter;
import net.silthus.rccities.api.city.AbstractCity;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.util.StringUtils;
import org.bukkit.Location;

public abstract class AbstractPlace implements Place {

    @Getter
    private String friendlyName;

    @Getter
    private Location location;

    @Getter
    private City city;

    public AbstractPlace(City city, String friendlyName, Location location) {
        this.city = city;
        this.friendlyName = friendlyName;
        this.location = location;
    }

    @Override
    public String getTechnicalName() {

        String fixedName = getFriendlyName().toLowerCase();
        fixedName = fixedName.replace(" ", "_");
        fixedName = StringUtils.replaceUmlaut(fixedName);

        return fixedName;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place that = (Place) o;

        if(!that.getCity().equals(getCity())) return false;

        return getTechnicalName().equals(that.getTechnicalName());
    }

    @Override
    public int hashCode() {

        return (getCity().getTechnicalName() + "_" + getTechnicalName()).hashCode();
    }
}
