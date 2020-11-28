package net.silthus.rccities;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.rccities.api.city.AbstractCity;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.request.JoinRequest;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.tables.TCity;
import de.raidcraft.rccities.tables.TJoinRequest;
import de.raidcraft.rcupgrades.RCUpgradesPlugin;
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

        id = tCity.getId();
        name = tCity.getName();
        creator = tCity.getCreatorId();
        creationDate = tCity.getCreationDate();
        description = tCity.getDescription();
        plotCredit = tCity.getPlotCredit();
        maxRadius = tCity.getMaxRadius();
        exp = tCity.getExp();
        spawn = new Location(Bukkit.getWorld(tCity.getWorld()), (double) tCity.getX() / 1000D, (double) tCity.getY() / 1000D, (double) tCity.getZ() / 1000D, (float) tCity.getYaw() / 1000F, (float) tCity.getPitch() / 1000F);
        upgradeHolder = RaidCraft.getComponent(RCUpgradesPlugin.class).getUpgradeManager()
                .loadDatabaseUpgradeHolder(this, RaidCraft.getComponent(RCCitiesPlugin.class).getUpgradeConfiguration(), id, City.class);
    }

    @Override
    public int getSize() {

        return RaidCraft.getComponent(RCCitiesPlugin.class).getPlotManager().getPlots(this).size();
    }

    @Override
    public void setFlag(Player player, String flagName, String flagValue) throws RaidCraftException {

        RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().setCityFlag(this, player, flagName, flagValue);
    }

    @Override
    public void removeFlag(String flagName) {

        RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().removeCityFlag(this, flagName);
    }

    @Override
    public void refreshFlags() {

        RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().refreshCityFlags(this);
    }

    @Override
    public List<Resident> getResidents() {

        return RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().getResidents(this);
    }

    @Override
    public List<JoinRequest> getJoinRequests() {

        List<JoinRequest> joinRequests = new ArrayList<>();

        List<TJoinRequest> tJoinRequests = RaidCraft.getDatabase(RCCitiesPlugin.class)
                .find(TJoinRequest.class).where().eq("city_id", getId()).findList();
        for (TJoinRequest tJoinRequest : tJoinRequests) {
            JoinRequest joinRequest = new DatabaseJoinRequest(tJoinRequest.getPlayerId(),
                    this, tJoinRequest.isRejected(), tJoinRequest.getRejectReason());
            joinRequests.add(joinRequest);
        }
        return joinRequests;
    }

    @Override
    public JoinRequest getJoinRequest(UUID playerId) {

        TJoinRequest tJoinRequest = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TJoinRequest.class)
                .where().eq("city_id", getId())
                .eq("player_id", playerId).findUnique();
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
        if (getId() == 0) {
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
            upgradeHolder = RaidCraft.getComponent(RCUpgradesPlugin.class).getUpgradeManager()
                    .createDatabaseUpgradeHolder(this, RaidCraft.getComponent(RCCitiesPlugin.class).getUpgradeConfiguration(), City.class);
            tCity.setUpgradeId(upgradeHolder.getId());
            RaidCraft.getDatabase(RCCitiesPlugin.class).save(tCity);
            id = tCity.getId();
            RaidCraft.getEconomy().createAccount(AccountType.CITY, getBankAccountName());
        }
        // update existing city
        else {
            TCity tCity = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCity.class, getId());
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
            RaidCraft.getDatabase(RCCitiesPlugin.class).update(tCity);
        }
    }

    @Override
    public void delete() {

        RCCitiesPlugin plugin = RaidCraft.getComponent(RCCitiesPlugin.class);
        for (Plot plot : plugin.getPlotManager().getPlots(this)) {
            plot.delete();
        }
        for (Resident resident : plugin.getResidentManager().getResidents(this)) {
            resident.delete();
        }

        RaidCraft.getEconomy().deleteAccount(AccountType.CITY, getBankAccountName());
        RaidCraft.getComponent(RCUpgradesPlugin.class).getUpgradeManager().deleteUpgradeHolder(getUpgrades().getId());

        plugin.getCityManager().removeFromCache(this);
        TCity tCity = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCity.class, getId());
        RaidCraft.getDatabase(RCCitiesPlugin.class).delete(tCity);
    }
}
