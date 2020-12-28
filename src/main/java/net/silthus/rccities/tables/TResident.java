package net.silthus.rccities.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.api.city.City;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Setter
@Getter
@Entity
@Table(name = "rccities_residents")
public class TResident extends BaseEntity {

    public static final Finder<UUID, TResident> find = new Finder<>(TResident.class);

    @ManyToOne
    private TCity city;
    private UUID playerId;
    private String profession;
    private Double depositAmount;
    private Double withdrawAmount;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "resident_id")
    private Set<TAssignment> assignment;

    public void setCity(City city) {

        this.city = TCity.find.byId(city.getId());
    }
}
