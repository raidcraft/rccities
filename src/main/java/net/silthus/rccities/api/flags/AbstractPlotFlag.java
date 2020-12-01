package net.silthus.rccities.api.flags;


import net.silthus.rccities.api.plot.Plot;

/**
 * @author Philip Urban
 */
public abstract class AbstractPlotFlag extends AbstractFlag implements PlotFlag {

    private final Plot plot;

    protected AbstractPlotFlag(Plot plot) {

        this.plot = plot;
    }

    @Override
    public Plot getPlot() {

        return plot;
    }
}
