package net.silthus.rccities.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_assignments")
public class TAssignment extends BaseEntity {

    public static final Finder<UUID, TAssignment> find = new Finder<>(TAssignment.class);

    @ManyToOne
    private TPlot plot;
    @ManyToOne
    private TResident resident;

    public void setPlot(Plot plot) {

        this.plot = TPlot.find.byId(plot.getId());
    }

    public void setResident(Resident resident) {

        this.resident = TResident.find.byId(resident.getId());
    }
}
