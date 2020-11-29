package net.silthus.rccities.api.flags;


import net.silthus.rccities.util.RaidCraftException;

/**
 * @author Philip Urban
 */
public interface Flag {

    String getName();

    String getFriendlyName();

    FlagType getType();

    String getValue();

    void setValue(String value) throws RaidCraftException;

    long getLastChange();

    void refresh() throws RaidCraftException;
}
