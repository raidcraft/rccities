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
@Table(name = "rccities_upgrade_levels")
public class TUpgradeLevel extends BaseEntity {

    private String identifier;
    private boolean unlocked;
    @ManyToOne
    private TUpgrade upgrade;
}
