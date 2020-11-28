package net.silthus.rccities.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rccities_upgrade_requests")
public class TUpgradeRequest {

    @Id
    private int id;
    @ManyToOne
    private TCity city;
    private String levelIdentifier;
    private String info;
    private boolean rejected;
    private boolean accepted;
    private String rejectReason;
    private Timestamp rejectDate;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TCity getCity() {

        return city;
    }

    public City getRCCity() {

        return RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(city.getName());
    }

    public void setCity(City city) {

        TCity tCity = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCity.class, city.getId());
        this.city = tCity;
    }

    public void setCity(TCity city) {

        this.city = city;
    }

    public String getLevelIdentifier() {

        return levelIdentifier;
    }

    public UpgradeLevel<City> getUpgradeLevel() {

        for (Upgrade upgrade : getRCCity().getUpgrades().getUpgrades()) {
            for (UpgradeLevel upgradeLevel : upgrade.getLevels()) {
                if (upgradeLevel.getId().equalsIgnoreCase(getLevelIdentifier())) {
                    return upgradeLevel;
                }
            }
        }
        return null;
    }

    public void setLevelIdentifier(String levelIdentifier) {

        this.levelIdentifier = levelIdentifier;
    }

    public String getInfo() {

        return info;
    }

    public void setInfo(String info) {

        this.info = info;
    }

    public boolean isRejected() {

        return rejected;
    }

    public void setRejected(boolean rejected) {

        this.rejected = rejected;
    }

    public boolean isAccepted() {

        return accepted;
    }

    public void setAccepted(boolean accepted) {

        this.accepted = accepted;
    }

    public String getRejectReason() {

        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {

        this.rejectReason = rejectReason;
    }

    public Timestamp getRejectDate() {

        return rejectDate;
    }

    public void setRejectDate(Timestamp rejectDate) {

        this.rejectDate = rejectDate;
    }
}
