package net.silthus.rccities.tables;

import io.ebean.Finder;
import io.ebean.annotation.DbDefault;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Setter
@Getter
@Entity
@Table(name = "rccities_cities")
public class TCity extends BaseEntity {

    public static final Finder<UUID, TCity> find = new Finder<>(TCity.class);

    private String name;
    private UUID creatorId;
    private Timestamp creationDate;
    @Lob
    private String description;
    private String world;
    private int x;
    private int y;
    private int z;
    private int pitch;
    private int yaw;
    private int plotCredit;
    private int maxRadius;
    private int exp;
    private UUID upgradeId;
    private Double money;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "city_id")
    private Set<TPlot> plots;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "city_id")
    private Set<TResident> residents;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "city_id")
    private Set<TCityFlag> settings;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "city_id")
    private Set<TJoinRequest> requests;

    public void loadChildren() {

        plots = TPlot.find.query().where().eq("city_id", id()).findSet();
        residents = TResident.find.query().where().eq("city_id", id()).findSet();
        settings = TCityFlag.find.query().where().eq("city_id", id()).findSet();
        requests = TJoinRequest.find.query().where().eq("city_id", id()).findSet();
    }
}
