package net.silthus.rccities.upgrades.tables;

import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_upgrade_level_info")
public class TLevelInfo extends BaseEntity {

    private String identifier;
    private String name;
    private int levelNumber;
    private String requirementDescription;
    private String rewardDescription;
    @ManyToOne
    private TUpgradeInfo upgradeInfo;
}
