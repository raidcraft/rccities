package net.silthus.rccities.upgrades.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

/**
 * @author Philip Urban
 */
@Entity
@Getter
@Setter
@Table(name = "rccities_upgrade_levels")
public class TUpgradeLevel extends BaseEntity {

    public static final Finder<UUID, TUpgradeLevel> find = new Finder<>(TUpgradeLevel.class);

    private String identifier;
    private boolean unlocked;
    @ManyToOne
    private TUpgrade upgrade;
}
