package net.silthus.rccities.manager;

import io.ebean.Model;
import net.silthus.rccities.DatabaseResident;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.api.resident.Role;
import net.silthus.rccities.api.resident.RolePermission;
import net.silthus.rccities.tables.TJoinRequest;
import net.silthus.rccities.tables.TResident;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public class ResidentManager {

    private final RCCitiesPlugin plugin;
    private final Map<UUID, List<Resident>> cachedResidents = new HashMap<>();

    public ResidentManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    public void broadcastCityMessage(City city, String message) {

        String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + city.getFriendlyName() + ChatColor.DARK_GRAY + "] ";

        for (Resident resident : getResidents(city)) {

            Player player = resident.getPlayer();
            if (player != null) {
                player.sendMessage(prefix + ChatColor.GOLD + message);
            }
        }
    }

    public void broadcastCityMessage(City city, String message, RolePermission receiverPermission) {

        String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + city.getFriendlyName() + ChatColor.DARK_GRAY + "] ";

        for (Resident resident : getResidents(city)) {

            if (!resident.getRole().hasPermission(receiverPermission)) continue;
            Player player = resident.getPlayer();
            if (player != null) {
                player.sendMessage(prefix + ChatColor.GOLD + message);
            }
        }
    }

    public Resident addResident(City city, OfflinePlayer player) throws RaidCraftException {

        return addResident(city, player.getUniqueId());
    }

    public Resident addResident(City city, UUID playerId) throws RaidCraftException {

        Resident resident = getResident(playerId, city);
        if (resident != null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            throw new RaidCraftException(offlinePlayer.getName()
                    + " ist bereits Einwohner von '" + city.getFriendlyName() + "'!");
        }

        resident = new DatabaseResident(playerId, Role.RESIDENT, city);
        List<Resident> residentList = cachedResidents.get(playerId);
        if (residentList == null) {
            cachedResidents.put(playerId, new ArrayList<>());
            residentList = cachedResidents.get(playerId);
        }
        residentList.add(resident);
        return resident;
    }

    public void removeFromCache(Resident resident) {

        List<Resident> residentList = cachedResidents.get(resident.getPlayerId());
        if (residentList == null) return;
        residentList.remove(resident);
    }

    public void printResidentInfo(UUID playerId, CommandSender sender) {

        List<Resident> citizenships = getCitizenships(playerId);
        if (citizenships == null || citizenships.size() == 0) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            sender.sendMessage(ChatColor.RED + "Keine Einwohner Informationen zu '" + offlinePlayer.getName() + "' gefunden!");
            return;
        }

        sender.sendMessage("*********************************");
        sender.sendMessage(ChatColor.GOLD + "Einwohner Informationen zu '" + ChatColor.YELLOW + citizenships.get(0).getName() + ChatColor.GOLD + "'");
        if (citizenships.size() == 1) {
            sender.sendMessage(ChatColor.GOLD + "Bürgerschaft in " + ChatColor.YELLOW + citizenships.size() + ChatColor.GOLD + " Stadt:");
        } else {
            sender.sendMessage(ChatColor.GOLD + "Bürgerschaft in " + ChatColor.YELLOW + citizenships.size() + ChatColor.GOLD + " Städten:");
        }
        for (Resident resident : citizenships) {
            sender.sendMessage(ChatColor.GOLD + resident.getCity().getFriendlyName() + " : " + ChatColor.YELLOW + resident.getRole().getFriendlyName());
        }
        sender.sendMessage("*********************************");
    }

    public void deleteOtherJoinRequests(UUID playerId, City exceptedCity) {

        List<TJoinRequest> tJoinRequests = TJoinRequest.find.query()
                .where().ieq("player_id", playerId.toString()).ne("city_id", exceptedCity.getId()).findList();
        if (tJoinRequests.size() == 0) return;
        tJoinRequests.forEach(Model::delete);
    }

    public void addPrefixSkill(Resident resident) {

        if (!resident.getRole().hasPermission(RolePermission.PREFIX_SKILL)) return;
        if (resident.getPlayer() == null || !resident.getPlayer().isOnline()) return;

        plugin.getPermission().playerAdd(resident.getPlayer(), "group.c-" + resident.getCity().getName().toLowerCase());
    }

    public void removePrefixSkill(Resident resident) {

        if(Bukkit.getPlayer(resident.getPlayerId()) == null) {
            return;
        }

        plugin.getPermission().playerRemove(resident.getPlayer(), "group.c-" + resident.getCity().getName().toLowerCase());
    }

    public List<Resident> getCitizenships(UUID playerId) {

        return getCitizenships(playerId, true);
    }

    public List<Resident> getCitizenships(UUID playerId, boolean load) {

        List<Resident> residents = cachedResidents.getOrDefault(playerId, new ArrayList<>());

        if (!load) {
            return residents;
        }

        if (residents == null || residents.size() == 0) {
            List<TResident> tResidents = TResident.find.query()
                    .where().ieq("player_id", playerId.toString()).findList();
            if (tResidents.size() > 0) {
                cachedResidents.put(playerId, new ArrayList<>());
                for (TResident tResident : tResidents) {
                    Resident resident = new DatabaseResident(tResident);
                    if (resident.getCity() != null) {
                        cachedResidents.get(playerId).add(resident);
                    }
                }
            }
        }
        return cachedResidents.get(playerId);
    }

    public List<Resident> getCitizenships(UUID playerId, RolePermission permission) {

        List<Resident> residents = getCitizenships(playerId);
        List<Resident> citizenships = new ArrayList<>();

        if (residents == null) return null;

        for (Resident resident : residents) {
            if (resident.getRole().hasPermission(permission)) {
                citizenships.add(resident);
            }
        }

        return citizenships;
    }

    public Resident getResident(UUID playerId, City city) {

        List<Resident> residents = getCitizenships(playerId);

        if (residents != null) {
            for (Resident resident : residents) {
                if (resident.getCity().equals(city)) {
                    return resident;
                }
            }
        }

        return null;
    }

    public Resident getResident(String residentName, City city) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(residentName);
        return getResident(offlinePlayer.getUniqueId(), city);
    }

    public List<Resident> getResidents(City city) {

        return getResidents(city, true);
    }

    public List<Resident> getResidents(City city, boolean load) {

        List<Resident> residents = new ArrayList<>();

        if (!load) {
            for (List<Resident> residentList : cachedResidents.values()) {
                for (Resident resident : residentList) {
                    if (resident.getCity().equals(city)) {
                        residents.add(resident);
                    }
                }
            }
            return residents;
        }

        List<TResident> tResidents = TResident.find.query()
                .where().eq("city_id", city.getId()).findList();
        for (TResident tResident : tResidents) {
            if (!cachedResidents.containsKey(tResident.getPlayerId())) {
                cachedResidents.put(tResident.getPlayerId(), new ArrayList<>());
            }
            boolean exist = false;
            for (Resident resident : cachedResidents.get(tResident.getPlayerId())) {
                if (resident.getCity().equals(city)) {
                    residents.add(resident);
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                Resident resident = new DatabaseResident(tResident);
                cachedResidents.get(resident.getPlayerId()).add(resident);
                residents.add(resident);
            }
        }

        return residents;
    }

    public void reload() {

        cachedResidents.clear();

        for (City city : plugin.getCityManager().getCities()) {

            getResidents(city, true);
        }
    }
}
