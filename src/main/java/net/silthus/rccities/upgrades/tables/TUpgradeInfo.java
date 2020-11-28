package net.silthus.rccities.upgrades.tables;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcupgrades_upgrade_info")
public class TUpgradeInfo {

    @Id
    private int id;
    private String holderId;
    private String holderName;
    private String name;
    private String description;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "upgrade_info_id")
    private Set<TLevelInfo> levelInfo;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getHolderId() {

        return holderId;
    }

    public void setHolderId(String holderId) {

        this.holderId = holderId;
    }

    public String getHolderName() {

        return holderName;
    }

    public void setHolderName(String holderName) {

        this.holderName = holderName;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Set<TLevelInfo> getLevelInfo() {

        return levelInfo;
    }

    public void setLevelInfo(Set<TLevelInfo> levelInfo) {

        this.levelInfo = levelInfo;
    }
}
