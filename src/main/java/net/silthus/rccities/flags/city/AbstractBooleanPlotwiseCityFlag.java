package net.silthus.rccities.flags.city;


import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.AbstractCityFlag;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.util.RaidCraftException;

/**
 * @author Philip Urban
 */
public abstract class AbstractBooleanPlotwiseCityFlag extends AbstractCityFlag {

    protected AbstractBooleanPlotwiseCityFlag(City city) {

        super(city);
    }

    @Override
    public void refresh() throws RaidCraftException {

        if (getCity() == null) return;

        boolean currentValue = getType().convertToBoolean(getValue());
        announce(currentValue);

        for (Plot plot : RCCitiesPlugin.getPlugin().getPlotManager().getPlots(getCity())) {
            if (currentValue) {
                allow(plot);
            } else {
                deny(plot);
            }
        }
    }

    public abstract void announce(boolean state);

    public abstract void allow(Plot plot);

    public abstract void deny(Plot plot);
}
