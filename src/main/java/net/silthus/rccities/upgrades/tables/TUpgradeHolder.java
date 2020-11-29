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
@Table(name = "rccities_upgrade_holders")
public class TUpgradeHolder extends BaseEntity {

    public static final Finder<UUID, TUpgradeHolder> find = new Finder<>(TUpgradeHolder.class);

    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "holder_id")
    private Set<TUpgrade> upgrades;
}
