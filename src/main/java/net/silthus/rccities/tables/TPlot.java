package net.silthus.rccities.tables;


import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.api.city.City;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.*;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_plots")
public class TPlot extends BaseEntity {

    public static final Finder<UUID, TPlot> find = new Finder<>(TPlot.class);

    @ManyToOne
    private TCity city;
    private int x;
    private int z;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "plot_id")
    private Set<TAssignment> assignment = new HashSet<>();
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "plot_id")
    private List<TPlotFlag> flags = new ArrayList<>();

    public void setCity(City city) {

        TCity tCity = TCity.find.byId(city.getId());
        this.city = tCity;
    }
}
