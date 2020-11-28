package net.silthus.rccities.api.flags;

import de.raidcraft.rccities.api.plot.Plot;

/**
 * @author Philip Urban
 */
public abstract class AbstractPlotFlag extends AbstractFlag implements PlotFlag {

    private Plot plot;

    protected AbstractPlotFlag(Plot plot) {

        this.plot = plot;
    }

    @Override
    public Plot getPlot() {

        return plot;
    }
}
