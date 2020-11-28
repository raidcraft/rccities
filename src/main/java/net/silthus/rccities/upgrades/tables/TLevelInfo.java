package net.silthus.rccities.upgrades.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Table(name = "rcupgrades_level_info")
public class TLevelInfo {

    @Id
    private int id;
    private String identifier;
    private String name;
    private int levelNumber;
    private String requirementDescription;
    private String rewardDescription;
    @ManyToOne
    private TUpgradeInfo upgradeInfo;

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

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getLevelNumber() {

        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {

        this.levelNumber = levelNumber;
    }

    public String getRequirementDescription() {

        return requirementDescription;
    }

    public void setRequirementDescription(String requirementDescription) {

        this.requirementDescription = requirementDescription;
    }

    public String getRewardDescription() {

        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {

        this.rewardDescription = rewardDescription;
    }

    public TUpgradeInfo getUpgradeInfo() {

        return upgradeInfo;
    }

    public void setUpgradeInfo(TUpgradeInfo upgradeInfo) {

        this.upgradeInfo = upgradeInfo;
    }
}
