package net.silthus.rccities.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.api.city.City;

import javax.persistence.Entity;
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
public class TJoinRequest extends BaseEntity {

    public static final Finder<UUID, TJoinRequest> find = new Finder<>(TJoinRequest.class);

    @ManyToOne
    private TCity city;
    private String player;
    private UUID playerId;
    private boolean rejected;
    private String rejectReason;

    public void setCity(City city) {

        this.city = TCity.find.byId(city.getId());
    }

    public void setCity(TCity city) {
        this.city = city;
    }
}
