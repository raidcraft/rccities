package net.silthus.rccities.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Setter
@Getter
@Entity
@Table(name = "rccities_join_requests")
public class TJoinRequest {

    @Id
    private int id;
    @ManyToOne
    private TCity city;
    private String player;
    private UUID playerId;
    private boolean rejected;
    private String rejectReason;

    public int getId() {

        return id;
    }

    public void setCity(City city) {

        TCity tCity = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCity.class, city.getId());
        this.city = tCity;
    }

    public void setCity(TCity city) {
        this.city = city;
    }
}
