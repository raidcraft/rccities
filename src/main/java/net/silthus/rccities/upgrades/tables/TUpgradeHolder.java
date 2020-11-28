package net.silthus.rccities.upgrades.tables;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcupgrades_holders")
public class TUpgradeHolder {

    @Id
    private int id;
    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "holder_id")
    private Set<TUpgrade> upgrades;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Set<TUpgrade> getUpgrades() {

        return upgrades;
    }

    public void setUpgrades(Set<TUpgrade> upgrades) {

        this.upgrades = upgrades;
    }
}
