package net.silthus.rccities;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.request.AbstractJoinRequest;
import net.silthus.rccities.tables.TJoinRequest;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public class DatabaseJoinRequest extends AbstractJoinRequest {

    public DatabaseJoinRequest(UUID playerId, City city, boolean rejected, String rejectReason) {

        super(playerId, city, rejected, rejectReason);
    }

    @Override
    public void accept() {

        try {
            RCCities.instance().getResidentManager().addResident(getCity(), getPlayer());
            Bukkit.broadcastMessage(ChatColor.GOLD + Bukkit.getOfflinePlayer(getPlayer()).getName()
                    + " ist nun Einwohner von '" + getCity().getFriendlyName() + "'!");
        } catch (RaidCraftException e) {
        }
        delete();
    }

    @Override
    public void reject(String reason) {

        TJoinRequest joinRequest = TJoinRequest.find.query()
                .where()
                .eq("city_id", getCity().getId())
                .eq("player_id", getPlayer()).findOne();
        if (joinRequest == null) return;

        joinRequest.setRejected(true);
        joinRequest.setRejectReason(reason);
        joinRequest.update();
    }

    @Override
    public void save() {

        TJoinRequest tJoinRequest = TJoinRequest.find.query()
                .where().eq("city_id", getCity().getId())
                .eq("player_id", getPlayer()).findOne();
        if (tJoinRequest == null) {
            tJoinRequest = new TJoinRequest();
            tJoinRequest.setCity(getCity());
            tJoinRequest.setPlayerId(getPlayer());
            tJoinRequest.save();
        } else {
            tJoinRequest.setRejected(isRejected());
            tJoinRequest.setRejectReason(getRejectReason());
            tJoinRequest.update();
        }
    }

    private void delete() {

        TJoinRequest joinRequest = TJoinRequest.find.query()
                .where().eq("city_id", getCity().getId())
                .eq("player_id", getPlayer()).findOne();
        if (joinRequest != null) {
            joinRequest.delete();
        }
    }

    @Override
    public void reactivate() {

    }
}
