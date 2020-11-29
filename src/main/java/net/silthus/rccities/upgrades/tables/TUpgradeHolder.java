package net.silthus.rccities.upgrades.tables;

import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_upgrade_holders")
public class TUpgradeHolder extends BaseEntity {

    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "holder_id")
    private Set<TUpgrade> upgrades;
}
