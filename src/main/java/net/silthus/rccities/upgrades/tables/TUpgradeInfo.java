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
@Table(name = "rccities_upgrade_info")
public class TUpgradeInfo extends BaseEntity {

    private String holderId;
    private String holderName;
    private String name;
    private String description;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "upgrade_info_id")
    private Set<TLevelInfo> levelInfo;
}
