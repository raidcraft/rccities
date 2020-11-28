package net.silthus.rccities.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.plot.Plot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rccities_plot_flags")
public class TPlotFlag {

    @Id
    private int id;
    @ManyToOne
    private TPlot plot;
    private String name;
    private String value;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TPlot getPlot() {

        return plot;
    }

    public void setPlot(Plot plot) {

        TPlot tPlot = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TPlot.class, plot.getId());
        this.plot = tPlot;
    }

    public void setPlot(TPlot plot) {

        this.plot = plot;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getValue() {

        return value;
    }

    public void setValue(String value) {

        this.value = value;
    }
}
