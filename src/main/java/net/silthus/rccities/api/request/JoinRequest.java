package net.silthus.rccities.api.request;

import de.raidcraft.rccities.api.city.City;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface JoinRequest extends Request {

    UUID getPlayer();

    City getCity();

    void save();
}
