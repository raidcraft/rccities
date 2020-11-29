package net.silthus.rccities.tables;


import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_upgrade_requests")
public class TUpgradeRequest extends BaseEntity {

    public static final Finder<UUID, TUpgradeRequest> find = new Finder<>(TUpgradeRequest.class);

    @ManyToOne
    private TCity city;
    private String levelIdentifier;
    private String info;
    private boolean rejected;
    private boolean accepted;
    private String rejectReason;
    private Timestamp rejectDate;

    public City getRCCity() {

        return RCCitiesPlugin.getPlugin().getCityManager().getCity(city.getName());
    }

    public void setCity(City city) {

        TCity tCity = TCity.find.byId(city.getId());
        this.city = tCity;
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
}
