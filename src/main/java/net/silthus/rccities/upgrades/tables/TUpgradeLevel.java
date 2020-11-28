package net.silthus.rccities.upgrades.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcupgrades_levels")
public class TUpgradeLevel {

    @Id
    private int id;
    private String identifier;
    private boolean unlocked;
    @ManyToOne
    private TUpgrade upgrade;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getIdentifier() {

        return identifier;
    }

    public void setIdentifier(String identifier) {

        this.identifier = identifier;
    }

    public boolean isUnlocked() {

        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {

        this.unlocked = unlocked;
    }

    public TUpgrade getUpgrade() {

        return upgrade;
    }

    public void setUpgrade(TUpgrade upgrade) {

        this.upgrade = upgrade;
    }
}
