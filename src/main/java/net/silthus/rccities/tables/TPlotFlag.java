package net.silthus.rccities.tables;


import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.api.plot.Plot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_plot_flags")
public class TPlotFlag extends BaseEntity {

    public static final Finder<UUID, TPlotFlag> find = new Finder<>(TPlotFlag.class);

    @ManyToOne
    private TPlot plot;
    private String name;
    private String value;


    public void setPlot(Plot plot) {

        TPlot tPlot = TPlot.find.byId(plot.getId());
        this.plot = tPlot;
    }
}
