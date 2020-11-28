package net.silthus.rccities.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Setter
@Getter
@Entity
@Table(name = "rccities_residents")
public class TResident {

    @Id
    private int id;
    @ManyToOne
    private TCity city;
    private UUID playerId;
    private String profession;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "resident_id")
    private Set<TAssignment> assignment;

    public void setCity(City city) {

        TCity tCity = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCity.class, city.getId());
        this.city = tCity;
    }

    public void setCity(TCity city) {

        this.city = city;
    }
}
