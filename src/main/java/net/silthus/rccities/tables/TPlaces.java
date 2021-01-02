package net.silthus.rccities.tables;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import net.silthus.ebean.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Setter
@Getter
@Entity
public class TPlaces extends BaseEntity {

    public static final Finder<UUID, TPlaces> find = new Finder<>(TPlaces.class);

    @ManyToOne
    private TCity city;
    private String name;
    private int x;
    private int y;
    private int z;
}
