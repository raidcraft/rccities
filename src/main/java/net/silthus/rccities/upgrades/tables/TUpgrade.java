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
@Table(name = "rccities_upgrades")
public class TUpgrade extends BaseEntity {

    private String name;
    @ManyToOne
    private TUpgradeHolder holder;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "upgrade_id")
    private Set<TUpgradeLevel> levels;
}
