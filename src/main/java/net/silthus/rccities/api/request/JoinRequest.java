package net.silthus.rccities.api.request;


import net.silthus.rccities.api.city.City;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public interface JoinRequest extends Request {

    UUID getPlayer();

    City getCity();

    void save();
}
