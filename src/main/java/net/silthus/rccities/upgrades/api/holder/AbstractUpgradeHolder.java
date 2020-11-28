package net.silthus.rccities.upgrades.api.holder;

import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import net.silthus.rccities.util.CaseInsensitiveMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public abstract class AbstractUpgradeHolder<T> implements UpgradeHolder<T> {

    protected int id;
    protected String name;
    protected String description;
    protected T object;
    protected Class<T> clazz;
    protected Map<String, Upgrade> upgrades = new CaseInsensitiveMap<>();

    public AbstractUpgradeHolder(T object, Class<T> clazz) {
        this.clazz = clazz;
        this.object = object;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public Upgrade getUpgrade(String id) {

        return upgrades.get(id);
    }

    @Override
    public List<Upgrade> getUpgrades() {

        if(upgrades.values() == null || upgrades.values().size() == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(upgrades.values());
    }

    @Override
    public Class<T> getType() {
        return clazz;
    }

    public void setType(Class<T> clazz)
    {
        this.clazz = clazz;
    }
}
