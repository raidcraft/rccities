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
@Table(name = "rccities_upgrade_info")
public class TUpgradeInfo extends BaseEntity {

    public static final Finder<UUID, TUpgradeInfo> find = new Finder<>(TUpgradeInfo.class);

    private String holderId;
    private String holderName;
    private String name;
    private String description;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "upgrade_info_id")
    private Set<TLevelInfo> levelInfo;
}
