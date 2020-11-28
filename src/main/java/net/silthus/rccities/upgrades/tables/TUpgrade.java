package net.silthus.rccities.upgrades.tables;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcupgrades_upgrades")
public class TUpgrade {

    @Id
    private int id;
    private String name;
    @ManyToOne
    private TUpgradeHolder holder;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "upgrade_id")
    private Set<TUpgradeLevel> levels;

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

    public TUpgradeHolder getHolder() {

        return holder;
    }

    public void setHolder(TUpgradeHolder holder) {

        this.holder = holder;
    }

    public Set<TUpgradeLevel> getLevels() {

        return levels;
    }

    public void setLevels(Set<TUpgradeLevel> levels) {

        this.levels = levels;
    }
}
