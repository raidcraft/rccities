package net.silthus.rccities;

import net.milkbowl.vault.economy.Economy;
import net.silthus.rccities.api.city.AbstractCity;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.request.JoinRequest;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.tables.TCity;
import net.silthus.rccities.tables.TJoinRequest;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public class DatabaseCity extends AbstractCity {

    public DatabaseCity(String name, Location spawn, UUID creator) {

        super(name, spawn, creator);
    }

    public DatabaseCity(TCity tCity) {

        id = tCity.id();
        name = tCity.getName();
        creator = tCity.getCreatorId();
        creationDate = tCity.getCreationDate();
        description = tCity.getDescription();
        plotCredit = tCity.getPlotCredit();
        maxRadius = tCity.getMaxRadius();
        exp = tCity.getExp();
        spawn = new Location(Bukkit.getWorld(tCity.getWorld()), (double) tCity.getX() / 1000D,
                (double) tCity.getY() / 1000D, (double) tCity.getZ() / 1000D,
                (float) tCity.getYaw() / 1000F, (float) tCity.getPitch() / 1000F);
        upgradeHolder = RCCitiesPlugin.getPlugin().getUpgrades().getUpgradeManager()
                .loadDatabaseUpgradeHolder(this, RCCitiesPlugin.getPlugin().getUpgradeConfiguration(), id, City.class);
    }

    @Override
    public int getSize() {

        return RCCitiesPlugin.getPlugin().getPlotManager().getPlots(this).size();
    }

    @Override
    public void setFlag(Player player, String flagName, String flagValue) throws RaidCraftException {

        RCCitiesPlugin.getPlugin().getFlagManager().setCityFlag(this, player, flagName, flagValue);
    }

    @Override
    public void removeFlag(String flagName) {

        RCCitiesPlugin.getPlugin().getFlagManager().removeCityFlag(this, flagName);
    }

    @Override
    public void refreshFlags() {

        RCCitiesPlugin.getPlugin().getFlagManager().refreshCityFlags(this);
    }

    @Override
    public List<Resident> getResidents() {

        return RCCitiesPlugin.getPlugin().getResidentManager().getResidents(this);
    }

    @Override
    public List<JoinRequest> getJoinRequests() {

        List<JoinRequest> joinRequests = new ArrayList<>();

        List<TJoinRequest> tJoinRequests = TJoinRequest.find.query().where().eq("city_id", getId()).findList();
        for (TJoinRequest tJoinRequest : tJoinRequests) {
            JoinRequest joinRequest = new DatabaseJoinRequest(tJoinRequest.getPlayerId(),
                    this, tJoinRequest.isRejected(), tJoinRequest.getRejectReason());
            joinRequests.add(joinRequest);
        }
        return joinRequests;
    }

    @Override
    public JoinRequest getJoinRequest(UUID playerId) {

        TJoinRequest tJoinRequest = TJoinRequest.find.query()
                .where().eq("city_id", getId())
                .eq("player_id", playerId).findOne();
        if (tJoinRequest == null) return null;
        return new DatabaseJoinRequest(tJoinRequest.getPlayerId(), this, tJoinRequest.isRejected(),
                tJoinRequest.getRejectReason());
    }

    @Override
    public void sendJoinRequest(UUID playerId) {

        new DatabaseJoinRequest(playerId, this, false, null);
    }

    @Override
    public void save() {

        // save new city
        if (getId() == null) {
            TCity tCity = new TCity();
            tCity.setCreationDate(new Timestamp(System.currentTimeMillis()));
            tCity.setCreatorId(getCreator());
            tCity.setName(getName());
            tCity.setWorld(getSpawn().getWorld().getName());
            tCity.setX((int) getSpawn().getX() * 1000);
            tCity.setY((int) getSpawn().getY() * 1000);
            tCity.setZ((int) getSpawn().getZ() * 1000);
            tCity.setPitch((int) getSpawn().getPitch() * 1000);
            tCity.setYaw((int) getSpawn().getYaw() * 1000);
            tCity.setMaxRadius(getMaxRadius());
            tCity.setPlotCredit(getPlotCredit());
            upgradeHolder = RCCitiesPlugin.getPlugin().getUpgrades().getUpgradeManager()
                    .createDatabaseUpgradeHolder(this, RCCitiesPlugin.getPlugin().getUpgradeConfiguration(), City.class);
            tCity.setUpgradeId(upgradeHolder.getId());
            tCity.save();
            id = tCity.id();
            RCCitiesPlugin.getPlugin().getEconomy().createPlayerAccount(getBankAccountName());
        }
        // update existing city
        else {
            TCity tCity = TCity.find.byId(getId());
            tCity.setWorld(getSpawn().getWorld().getName());
            tCity.setX((int) getSpawn().getX() * 1000);
            tCity.setY((int) getSpawn().getY() * 1000);
            tCity.setZ((int) getSpawn().getZ() * 1000);
            tCity.setPitch((int) getSpawn().getPitch() * 1000);
            tCity.setYaw((int) getSpawn().getYaw() * 1000);
            tCity.setDescription(getDescription());
            tCity.setPlotCredit(getPlotCredit());
            tCity.setMaxRadius(getMaxRadius());
            tCity.setExp(getExp());
            tCity.update();
        }
    }

    @Override
    public void delete() {

        RCCitiesPlugin plugin = RCCitiesPlugin.getPlugin();
        for (Plot plot : plugin.getPlotManager().getPlots(this)) {
            plot.delete();
        }
        for (Resident resident : plugin.getResidentManager().getResidents(this)) {
            resident.delete();
        }

        Economy economy;
        economy = plugin.getEconomy();
        economy.withdrawPlayer(getBankAccountName(), economy.getBalance(getBankAccountName()));
        RCCitiesPlugin.getPlugin().getUpgrades().getUpgradeManager().deleteUpgradeHolder(getUpgrades().getId());

        plugin.getCityManager().removeFromCache(this);
        TCity tCity = TCity.find.byId(getId());
        tCity.delete();
    }
}
