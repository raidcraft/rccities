package net.silthus.rccities.upgrades.api.upgrade;

/**
 * @author Philip Urban
 */
public abstract class AbstractUpgrade implements Upgrade {

    private final String id;
    private final String name;
    private final String description;

    protected AbstractUpgrade(String id, String name, String description) {

        this.id = id;
        this.name = name;
        this.description = description;
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
    public String getId() {

        return id;
    }
}
