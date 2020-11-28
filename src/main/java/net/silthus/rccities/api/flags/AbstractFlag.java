package net.silthus.rccities.api.flags;

import de.raidcraft.api.RaidCraftException;
import de.raidcraft.util.StringUtils;

/**
 * @author Philip Urban
 */
public abstract class AbstractFlag implements Flag {

    private String name;
    private String friendlyName;
    private String value;
    private FlagType type;
    private long lastChange;

    protected AbstractFlag() {

        FlagInformation annotation = getClass().getAnnotation(FlagInformation.class);
        this.name = StringUtils.formatName(annotation.name());
        this.friendlyName = annotation.friendlyName();
        this.type = annotation.type();
    }

    @Override
    public String getValue() {

        return value;
    }

    @Override
    public void setValue(String value) throws RaidCraftException {

        if (!type.validate(value)) throw new RaidCraftException("Falscher Wertetyp: " + type.getErrorMsg());

        this.value = value;
        lastChange = System.currentTimeMillis();
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public FlagType getType() {

        return type;
    }

    @Override
    public long getLastChange() {

        return lastChange;
    }
}
