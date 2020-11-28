package net.silthus.rccities.flags.city;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.AbstractCityFlag;
import de.raidcraft.rccities.api.plot.Plot;

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

        for (Plot plot : RaidCraft.getComponent(RCCitiesPlugin.class).getPlotManager().getPlots(getCity())) {
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
