package net.silthus.rccities.api.request;


import net.silthus.rccities.api.city.City;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public abstract class AbstractJoinRequest extends AbstractRequest implements JoinRequest {

    private final UUID playerId;
    private final City city;

    protected AbstractJoinRequest(UUID playerId, City city, boolean rejected, String rejectReason) {

        this.playerId = playerId;
        this.city = city;
        this.rejected = rejected;
        this.rejectReason = rejectReason;
        save();
    }

    @Override
    public UUID getPlayer() {

        return playerId;
    }

    @Override
    public City getCity() {

        return city;
    }
}
