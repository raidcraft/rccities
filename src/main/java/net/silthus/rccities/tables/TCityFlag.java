package net.silthus.rccities.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.api.city.City;

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
@Table(name = "rccities_city_flags")
public class TCityFlag extends BaseEntity {

    public static final Finder<UUID, TCityFlag> find = new Finder<>(TCityFlag.class);

    @ManyToOne
    private TCity city;
    private String name;
    private String value;

    public void setCity(City city) {

        this.city = TCity.find.byId(city.getId());
    }
}
