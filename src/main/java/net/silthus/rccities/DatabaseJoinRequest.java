package net.silthus.rccities;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.request.AbstractJoinRequest;
import de.raidcraft.rccities.tables.TJoinRequest;
import de.raidcraft.util.UUIDUtil;
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
            RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().addResident(getCity(), getPlayer());
            Bukkit.broadcastMessage(ChatColor.GOLD + UUIDUtil.getNameFromUUID(getPlayer())
                    + " ist nun Einwohner von '" + getCity().getFriendlyName() + "'!");
        } catch (RaidCraftException e) {
        }
        delete();
    }

    @Override
    public void reject(String reason) {

        TJoinRequest joinRequest = RaidCraft.getDatabase(RCCitiesPlugin.class)
                .find(TJoinRequest.class)
                .where()
                .eq("city_id", getCity().getId())
                .eq("player_id", getPlayer()).findUnique();
        if (joinRequest == null) return;

        joinRequest.setRejected(true);
        joinRequest.setRejectReason(reason);
        RaidCraft.getDatabase(RCCitiesPlugin.class).update(joinRequest);
    }

    @Override
    public void save() {

        TJoinRequest tJoinRequest = RaidCraft.getDatabase(RCCitiesPlugin.class)
                .find(TJoinRequest.class)
                .where().eq("city_id", getCity().getId())
                .eq("player_id", getPlayer()).findUnique();
        if (tJoinRequest == null) {
            tJoinRequest = new TJoinRequest();
            tJoinRequest.setCity(getCity());
            tJoinRequest.setPlayerId(getPlayer());
            RaidCraft.getDatabase(RCCitiesPlugin.class).save(tJoinRequest);
        } else {
            tJoinRequest.setRejected(isRejected());
            tJoinRequest.setRejectReason(getRejectReason());
            RaidCraft.getDatabase(RCCitiesPlugin.class).update(tJoinRequest);
        }
    }

    private void delete() {

        TJoinRequest joinRequest = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TJoinRequest.class)
                .where().eq("city_id", getCity().getId())
                .eq("player_id", getPlayer()).findUnique();
        if (joinRequest != null) {
            RaidCraft.getDatabase(RCCitiesPlugin.class).delete(joinRequest);
        }
    }

    @Override
    public void reactivate() {

    }
}
