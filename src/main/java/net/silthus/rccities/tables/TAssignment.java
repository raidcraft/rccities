package net.silthus.rccities.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.upgrades.tables.TLevelInfo;

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
@Table(name = "rccities_assignments")
public class TAssignment extends BaseEntity {

    public static final Finder<UUID, TAssignment> find = new Finder<>(TAssignment.class);

    @ManyToOne
    private TPlot plot;
    @ManyToOne
    private TResident resident;

    public void setPlot(Plot plot) {

        TPlot tPlot = TPlot.find.byId(plot.getId());
        this.plot = tPlot;
    }

    public void setResident(Resident resident) {

        TResident tResident = TResident.find.byId(resident.getId());
        this.resident = tResident;
    }
}
