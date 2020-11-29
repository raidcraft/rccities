package net.silthus.rccities.upgrades.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_upgrades")
public class TUpgrade extends BaseEntity {

    public static final Finder<UUID, TUpgrade> find = new Finder<>(TUpgrade.class);

    private String name;
    @ManyToOne
    private TUpgradeHolder holder;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "upgrade_id")
    private Set<TUpgradeLevel> levels;
}
