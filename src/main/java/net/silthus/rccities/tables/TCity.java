package net.silthus.rccities.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class TCity {

    @Id
    private int id;
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
    private int upgradeId;

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

        plots = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TPlot.class).where().eq("city_id", id).findSet();
        residents = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TResident.class).where().eq("city_id", id).findSet();
        settings = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCityFlag.class).where().eq("city_id", id).findSet();
        requests = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TJoinRequest.class).where().eq("city_id", id).findSet();
    }
}
