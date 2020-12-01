package net.silthus.rccities.api.flags;


import net.silthus.rccities.api.city.City;

/**
 * @author Philip Urban
 */
public abstract class AbstractCityFlag extends AbstractFlag implements CityFlag {

    private final City city;

    public AbstractCityFlag(City city) {

        this.city = city;
    }

    @Override
    public City getCity() {

        return city;
    }
}
